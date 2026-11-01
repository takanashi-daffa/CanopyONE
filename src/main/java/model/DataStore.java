package model;
 
import config.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class DataStore {
    private static final Pattern POLA_PERIODE = Pattern.compile(
            "Periode (\\d{4}-\\d{2}-\\d{2}) s/d (\\d{4}-\\d{2}-\\d{2}), total ([0-9.]+) Kg, tidak layak ([0-9.]+)%");
 
    private static final DataStore INSTANCE = new DataStore();
    public static DataStore getInstance() { return INSTANCE; }
 
    private String currentUser = "";
 
    private final List<BlokLahan> blokList = new ArrayList<>();
    private final List<Pegawai> pegawaiList = new ArrayList<>();
    private final List<Panen> panenList = new ArrayList<>();
    private final List<Evaluasi> evaluasiList = new ArrayList<>();
    private final List<Riwayat> riwayatList = new ArrayList<>();
 
    private int panenCounter = 1;
    private int evalCounter = 1;
    private int riwayatCounter = 1;
 
    public DataStore() {
        loadFromDatabase();
    }
 
 
    private interface SqlWork {
        void run(Connection c) throws Exception;
    }
 
    // Menjalankan beberapa perintah SQL sekaligus. Kalau salah satu gagal, semuanya dibatalkan.
    private boolean runTx(SqlWork work, String pesanError) {
        Connection c = Koneksi.getKoneksi();
        if (c == null) {
            System.err.println(pesanError + ": tidak ada koneksi database");
            return false;
        }
        try {
            c.setAutoCommit(false);
            work.run(c);
            c.commit();
            return true;
        } catch (Exception e) {
            try { c.rollback(); } catch (Exception ignored) {}
            System.err.println(pesanError + ": " + e.getMessage());
            return false;
        } finally {
            try { c.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }
 
    private void exec(Connection c, String sql, Object... params) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }
 
    private String nextId(Connection c, String table, String column, String prefix) throws Exception {
        int mulai = prefix.length() + 1;
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(CAST(SUBSTRING(" + column + ", " + mulai + ") AS UNSIGNED)) FROM " + table)) {
            int max = 0;
            if (rs.next()) max = rs.getInt(1);
            return String.format("%s%03d", prefix, max + 1);
        }
    }
 
    private String getCurrentUserId() {
        for (Pegawai p : pegawaiList) {
            if (p.getUsername() != null && p.getUsername().equalsIgnoreCase(currentUser)) {
                return p.getIdPegawai();
            }
        }
        return null;
    }
 
    private String ambilKodeBlok(String blokStr) {
        if (blokStr == null) return "";
        if (blokStr.contains(" - ")) {
            return blokStr.split(" - ")[0].trim();
        }
        return blokStr.trim();
    }
 
    private String tahunTanamBlok(String blokStr) {
        String kode = ambilKodeBlok(blokStr);
        for (BlokLahan b : blokList) {
            if (b.getKodeBlok().equalsIgnoreCase(kode)) {
                return String.valueOf(b.getTahunTanam());
            }
        }
        return "2020";
    }
 
    private String normalisasiStatus(String s) {
        if (s == null || s.trim().isEmpty()) return "Pending";
        String t = s.trim();
        String l = t.toLowerCase();
        if (l.startsWith("selesai") || l.contains("sesuai")) return "Selesai";
        if (l.startsWith("proses") || l.contains("perlu")) return "Proses";
        if (l.startsWith("pending")) return "Pending";
        return t.length() > 10 ? t.substring(0, 10) : t;
    }
 
    private Evaluasi rapikanEvaluasi(Evaluasi ev) {
        return new Evaluasi(ev.getIdEval(), ev.getTglAwal(), ev.getTglAkhir(),
                ev.getTotalPanen(), ev.getPersenTidakLayak(),
                normalisasiStatus(ev.getStatus()),
                ev.getCatatan() == null ? "" : ev.getCatatan());
    }
 
    private String teksPeriode(Evaluasi ev) {
        return String.format(Locale.US, "Periode %s s/d %s, total %.2f Kg, tidak layak %.2f%%",
                ev.getTglAwal(), ev.getTglAkhir(), ev.getTotalPanen(), ev.getPersenTidakLayak());
    }
 
    private String tanggalEvaluasi(Evaluasi ev) {
        String t = ev.getTglAkhir();
        if (t == null || t.trim().isEmpty()) return LocalDate.now().toString();
        return t.trim();
    }
 
    private void loadFromDatabase() {
        Connection c = Koneksi.getKoneksi();
        if (c == null) {
            System.err.println("Gagal memuat database canopy: " + Koneksi.getLastError());
            return;
        }
        try (Statement st = c.createStatement()) {
            loadBlok(st);
            loadUser(st);
            loadPanen(st);
            loadEvaluasi(st);
        } catch (Exception e) {
            System.err.println("Gagal memuat database canopy: " + e.getMessage());
        }
    }
 
    private void reloadEvaluasi() {
        Connection c = Koneksi.getKoneksi();
        if (c == null) return;
        try (Statement st = c.createStatement()) {
            loadEvaluasi(st);
        } catch (Exception e) {
            System.err.println("Gagal memuat ulang evaluasi: " + e.getMessage());
        }
    }
 
    private void loadBlok(Statement st) throws Exception {
        List<BlokLahan> baru = new ArrayList<>();
        try (ResultSet rs = st.executeQuery(
                "SELECT kode_blok_lahan, nama_blok_lahan, luas_blok_lahan, komoditas FROM blok_lahan ORDER BY kode_blok_lahan")) {
            while (rs.next()) {
                baru.add(new BlokLahan(
                        rs.getString("kode_blok_lahan"),
                        rs.getString("nama_blok_lahan"),
                        rs.getDouble("luas_blok_lahan"),
                        rs.getString("komoditas"),
                        2020));
            }
        }
        blokList.clear();
        blokList.addAll(baru);
    }
 
    private void loadUser(Statement st) throws Exception {
        List<Pegawai> baru = new ArrayList<>();
        try (ResultSet rs = st.executeQuery(
                "SELECT u.id_user, u.nama, u.username, u.password, u.no_telepon, " +
                "CASE WHEN a.id_user IS NOT NULL THEN 'Admin' " +
                "     WHEN d.id_user IS NOT NULL THEN 'Direksi' " +
                "     WHEN p.id_user IS NOT NULL THEN 'Petugas' " +
                "     ELSE 'Tidak Dikenal' END AS role " +
                "FROM user u " +
                "LEFT JOIN admin a ON u.id_user = a.id_user " +
                "LEFT JOIN direksi d ON u.id_user = d.id_user " +
                "LEFT JOIN petugas p ON u.id_user = p.id_user " +
                "ORDER BY u.id_user")) {
            while (rs.next()) {
                baru.add(new Pegawai(
                        rs.getString("id_user"),
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("no_telepon"),
                        rs.getString("role")));
            }
        }
        pegawaiList.clear();
        pegawaiList.addAll(baru);
    }
 
    private void loadPanen(Statement st) throws Exception {
        List<Panen> baru = new ArrayList<>();
        Set<String> sudahAda = new HashSet<>();
        int maxNum = 0;
        try (ResultSet rs = st.executeQuery(
                "SELECT ap.id_panen, ap.tanggal_panen, ap.total_jumlah_panen, ap.hasil_panen_tidak_layak, ap.kualitas_panen, " +
                "       ap.kode_blok_lahan, bl.nama_blok_lahan, bl.komoditas, " +
                "       ps.jumlah_tandan, ps.jumlah_brondolan, " +
                "       kr.kadar_air_karet, kr.berat_kotor_karet " +
                "FROM aktivitas_panen ap " +
                "LEFT JOIN blok_lahan bl ON ap.kode_blok_lahan = bl.kode_blok_lahan " +
                "LEFT JOIN produk_panen pp ON ap.id_panen = pp.aktivitas_panen_id_panen " +
                "LEFT JOIN sawit ps ON pp.id_produk = ps.id_produk " +
                "LEFT JOIN karet kr ON pp.id_produk = kr.id_produk " +
                "ORDER BY ap.id_panen")) {
            while (rs.next()) {
                String idP = rs.getString("id_panen");
                if (!sudahAda.add(idP)) continue;
 
                String kodeB = rs.getString("kode_blok_lahan");
                String namaB = rs.getString("nama_blok_lahan");
                String blokStr = (namaB != null && !namaB.isEmpty()) ? (kodeB + " - " + namaB) : kodeB;
                String kom = rs.getString("komoditas");
                if (kom == null || kom.isEmpty()) kom = "Sawit";
 
                boolean sawit = "Sawit".equalsIgnoreCase(kom);
                int in1 = sawit ? rs.getInt("jumlah_tandan") : rs.getInt("berat_kotor_karet");
                int in2 = sawit ? rs.getInt("jumlah_brondolan") : rs.getInt("kadar_air_karet");
 
                baru.add(new Panen(idP, rs.getString("tanggal_panen"), blokStr, kom, in1, in2,
                        rs.getDouble("total_jumlah_panen"),
                        rs.getDouble("hasil_panen_tidak_layak"),
                        rs.getString("kualitas_panen")));
                try {
                    int num = Integer.parseInt(idP.replaceAll("\\D+", ""));
                    if (num > maxNum) maxNum = num;
                } catch (Exception ignored) {}
            }
        }
        panenList.clear();
        panenList.addAll(baru);
        panenCounter = maxNum + 1;
    }
 
    private void loadEvaluasi(Statement st) throws Exception {
        List<Evaluasi> baru = new ArrayList<>();
        int maxNum = 0;
        try (ResultSet rs = st.executeQuery(
                "SELECT ep.id_evaluasi, ep.tanggal_evaluasi, ep.status, ep.catatan_tindak_lanjut, ep.catatan_evaluasi, " +
                "       ap.total_jumlah_panen, ap.hasil_panen_tidak_layak " +
                "FROM evaluasi_panen ep " +
                "LEFT JOIN aktivitas_panen ap ON ep.aktivitas_panen_id_panen = ap.id_panen " +
                "ORDER BY ep.id_evaluasi")) {
            while (rs.next()) {
                String idEv = rs.getString("id_evaluasi");
                String tglEv = rs.getString("tanggal_evaluasi");
                String status = rs.getString("status");
                String catatan = rs.getString("catatan_tindak_lanjut");
                if (catatan == null) catatan = "";
 
                double tot = rs.getDouble("total_jumlah_panen");
                boolean punyaPanen = !rs.wasNull();
                double rej = rs.getDouble("hasil_panen_tidak_layak");
 
                String tglAwal = tglEv;
                String tglAkhir = tglEv;
                double pct = 0.0;
 
                if (punyaPanen) {
                    pct = tot > 0 ? (rej / tot) * 100.0 : 0.0;
                } else {
                    String info = rs.getString("catatan_evaluasi");
                    if (info != null) {
                        Matcher m = POLA_PERIODE.matcher(info);
                        if (m.find()) {
                            tglAwal = m.group(1);
                            tglAkhir = m.group(2);
                            tot = Double.parseDouble(m.group(3));
                            pct = Double.parseDouble(m.group(4));
                        }
                    }
                }
 
                baru.add(new Evaluasi(idEv, tglAwal, tglAkhir, tot, pct, status, catatan));
                try {
                    int num = Integer.parseInt(idEv.replaceAll("\\D+", ""));
                    if (num > maxNum) maxNum = num;
                } catch (Exception ignored) {}
            }
        }
        evaluasiList.clear();
        evaluasiList.addAll(baru);
        evalCounter = maxNum + 1;
    }
 
    public synchronized Pegawai findPegawai(String userOrId, String password) {
        if (userOrId == null || password == null) return null;
        for (Pegawai p : pegawaiList) {
            if (p.getPassword() == null) continue;
            boolean cocokUser = (p.getUsername() != null && p.getUsername().equalsIgnoreCase(userOrId))
                    || (p.getIdPegawai() != null && p.getIdPegawai().equalsIgnoreCase(userOrId));
            if (cocokUser && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }
 
    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { this.currentUser = currentUser; }
 
    public synchronized List<BlokLahan> getBlokList() { return new ArrayList<>(blokList); }
 
    public synchronized boolean addBlok(BlokLahan b) {
        for (BlokLahan x : blokList) {
            if (x.getKodeBlok().equalsIgnoreCase(b.getKodeBlok())) return false;
        }
        String uid = getCurrentUserId();
        boolean ok = runTx(c -> exec(c,
                "INSERT INTO blok_lahan (kode_blok_lahan, nama_blok_lahan, luas_blok_lahan, komoditas, id_user) VALUES (?, ?, ?, ?, ?)",
                b.getKodeBlok(), b.getNamaLahan(), (int) Math.round(b.getLuasLahan()), b.getKomoditas(), uid),
                "Gagal simpan blok");
        if (ok) blokList.add(b);
        return ok;
    }
 
    public synchronized boolean updateBlok(BlokLahan b) {
        for (int i = 0; i < blokList.size(); i++) {
            if (blokList.get(i).getKodeBlok().equalsIgnoreCase(b.getKodeBlok())) {
                boolean ok = runTx(c -> exec(c,
                        "UPDATE blok_lahan SET nama_blok_lahan=?, luas_blok_lahan=?, komoditas=? WHERE kode_blok_lahan=?",
                        b.getNamaLahan(), (int) Math.round(b.getLuasLahan()), b.getKomoditas(), b.getKodeBlok()),
                        "Gagal update blok");
                if (ok) blokList.set(i, b);
                return ok;
            }
        }
        return false;
    }
 
    public synchronized boolean deleteBlok(String kode) {
        boolean ok = runTx(c -> exec(c, "DELETE FROM blok_lahan WHERE kode_blok_lahan = ?", kode),
                "Gagal hapus blok");
        if (ok) blokList.removeIf(b -> b.getKodeBlok().equalsIgnoreCase(kode));
        return ok;
    }
 
    public synchronized List<Pegawai> getPegawaiList() { return new ArrayList<>(pegawaiList); }
 
    private void simpanRole(Connection c, String id, String role) throws Exception {
        String r = role == null ? "" : role.trim();
        if (r.equalsIgnoreCase("Admin")) {
            exec(c, "INSERT INTO admin (id_user, bagian_administrasi) VALUES (?, ?)", id, "Administrasi Umum");
        } else if (r.equalsIgnoreCase("Direksi")) {
            exec(c, "INSERT INTO direksi (id_user, jabatan_direksi) VALUES (?, ?)", id, "Direksi");
        } else {
            exec(c, "INSERT INTO petugas (id_user, wilayah_penugasan) VALUES (?, ?)", id, "Belum ditentukan");
        }
    }
 
    private void hapusSemuaRole(Connection c, String id) throws Exception {
        exec(c, "DELETE FROM admin WHERE id_user = ?", id);
        exec(c, "DELETE FROM direksi WHERE id_user = ?", id);
        exec(c, "DELETE FROM petugas WHERE id_user = ?", id);
    }
 
    public synchronized boolean addPegawai(Pegawai p) {
        for (Pegawai x : pegawaiList) {
            if (x.getUsername() != null && x.getUsername().equalsIgnoreCase(p.getUsername())) {
                System.err.println("Gagal simpan user: username sudah dipakai");
                return false;
            }
        }
        final String[] idBaru = new String[1];
        boolean ok = runTx(c -> {
            idBaru[0] = nextId(c, "user", "id_user", "U");
            exec(c, "INSERT INTO user (id_user, nama, username, password, no_telepon) VALUES (?, ?, ?, ?, ?)",
                    idBaru[0], p.getNamaPegawai(), p.getUsername(), p.getPassword(), p.getNoTelp());
            simpanRole(c, idBaru[0], p.getRole());
        }, "Gagal simpan user");
        if (ok) {
            pegawaiList.add(new Pegawai(idBaru[0], p.getNamaPegawai(), p.getUsername(),
                    p.getPassword(), p.getNoTelp(), p.getRole()));
        }
        return ok;
    }
 
    public synchronized boolean updatePegawai(Pegawai p) {
        for (int i = 0; i < pegawaiList.size(); i++) {
            Pegawai lama = pegawaiList.get(i);
            if (lama.getIdPegawai().equalsIgnoreCase(p.getIdPegawai())) {
                String passBaru = (p.getPassword() == null || p.getPassword().isEmpty())
                        ? lama.getPassword() : p.getPassword();
                Pegawai baru = new Pegawai(lama.getIdPegawai(), p.getNamaPegawai(), p.getUsername(),
                        passBaru, p.getNoTelp(), p.getRole());
                boolean roleBerubah = !String.valueOf(lama.getRole()).equalsIgnoreCase(String.valueOf(p.getRole()));
 
                boolean ok = runTx(c -> {
                    exec(c, "UPDATE user SET nama=?, username=?, password=?, no_telepon=? WHERE id_user=?",
                            baru.getNamaPegawai(), baru.getUsername(), baru.getPassword(), baru.getNoTelp(), baru.getIdPegawai());
                    if (roleBerubah) {
                        hapusSemuaRole(c, baru.getIdPegawai());
                        simpanRole(c, baru.getIdPegawai(), baru.getRole());
                    }
                }, "Gagal update user");
                if (ok) pegawaiList.set(i, baru);
                return ok;
            }
        }
        return false;
    }
 
    public synchronized boolean deletePegawai(String idOrUser) {
        Pegawai target = null;
        for (Pegawai p : pegawaiList) {
            if (p.getIdPegawai().equalsIgnoreCase(idOrUser)
                    || (p.getUsername() != null && p.getUsername().equalsIgnoreCase(idOrUser))) {
                target = p;
                break;
            }
        }
        if (target == null) return false;
        if (target.getUsername() != null && target.getUsername().equalsIgnoreCase(currentUser)) {
            System.err.println("Gagal hapus user: tidak boleh menghapus akun yang sedang login");
            return false;
        }
        final String id = target.getIdPegawai();
        boolean ok = runTx(c -> {
            hapusSemuaRole(c, id);
            exec(c, "DELETE FROM user WHERE id_user = ?", id);
        }, "Gagal hapus user");
        if (ok) pegawaiList.removeIf(p -> p.getIdPegawai().equalsIgnoreCase(id));
        return ok;
    }
 
    public synchronized List<Panen> getPanenList() { return new ArrayList<>(panenList); }
 
    public synchronized String generatePanenId() {
        return String.format("PN%03d", panenCounter++);
    }
 
    private void simpanDetailPanen(Connection c, Panen p) throws Exception {
        String idProduk = null;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id_produk FROM produk_panen WHERE aktivitas_panen_id_panen = ? LIMIT 1")) {
            ps.setString(1, p.getIdPanen());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idProduk = rs.getString(1);
            }
        }
 
        boolean sawit = "Sawit".equalsIgnoreCase(p.getKomoditas());
        String namaProduk = sawit ? "Sawit" : "Karet";
 
        if (idProduk == null) {
            idProduk = nextId(c, "produk_panen", "id_produk", "PR");
            exec(c, "INSERT INTO produk_panen (id_produk, aktivitas_panen_id_panen, nama_produk, tahun_tanam) VALUES (?, ?, ?, ?)",
                    idProduk, p.getIdPanen(), namaProduk, tahunTanamBlok(p.getBlokLahan()));
        } else {
            exec(c, "UPDATE produk_panen SET nama_produk = ? WHERE id_produk = ?", namaProduk, idProduk);
        }
 
        exec(c, "DELETE FROM sawit WHERE id_produk = ?", idProduk);
        exec(c, "DELETE FROM karet WHERE id_produk = ?", idProduk);
 
        int in1 = (int) Math.round(p.getInput1());
        int in2 = (int) Math.round(p.getInput2());
        if (sawit) {
            exec(c, "INSERT INTO sawit (id_produk, jumlah_tandan, jumlah_brondolan) VALUES (?, ?, ?)", idProduk, in1, in2);
        } else {
            exec(c, "INSERT INTO karet (id_produk, kadar_air_karet, berat_kotor_karet) VALUES (?, ?, ?)", idProduk, in2, in1);
        }
    }
 
    public synchronized boolean addPanen(Panen p) {
        String kodeBlok = ambilKodeBlok(p.getBlokLahan());
        String uid = getCurrentUserId();
        boolean ok = runTx(c -> {
            exec(c, "INSERT INTO aktivitas_panen (id_panen, tanggal_panen, total_jumlah_panen, hasil_panen_tidak_layak, kualitas_panen, kode_blok_lahan, id_user) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    p.getIdPanen(), p.getTanggal(), p.getTotalPanen(), (int) Math.round(p.getTidakLayak()),
                    p.getKualitas(), kodeBlok, uid);
            simpanDetailPanen(c, p);
        }, "Gagal simpan panen");
        if (ok) {
            panenList.add(p);
            logRiwayat("Tambah Data", p.getIdPanen(), "-",
                    "Komoditas: " + p.getKomoditas() + ", Total: " + p.getTotalPanen() + " Kg");
        }
        return ok;
    }
 
    public synchronized boolean updatePanen(Panen p) {
        for (int i = 0; i < panenList.size(); i++) {
            if (panenList.get(i).getIdPanen().equalsIgnoreCase(p.getIdPanen())) {
                Panen old = panenList.get(i);
                String before = "Total: " + old.getTotalPanen() + " Kg, Kualitas: " + old.getKualitas();
                String after = "Total: " + p.getTotalPanen() + " Kg, Kualitas: " + p.getKualitas();
                String kodeBlok = ambilKodeBlok(p.getBlokLahan());
 
                boolean ok = runTx(c -> {
                    exec(c, "UPDATE aktivitas_panen SET tanggal_panen=?, total_jumlah_panen=?, hasil_panen_tidak_layak=?, kualitas_panen=?, kode_blok_lahan=? WHERE id_panen=?",
                            p.getTanggal(), p.getTotalPanen(), (int) Math.round(p.getTidakLayak()),
                            p.getKualitas(), kodeBlok, p.getIdPanen());
                    simpanDetailPanen(c, p);
                }, "Gagal update panen");
                if (ok) {
                    panenList.set(i, p);
                    logRiwayat("Pembaruan Data", p.getIdPanen(), before, after);
                }
                return ok;
            }
        }
        return false;
    }
 
    public synchronized boolean deletePanen(String idPanen) {
        for (int i = 0; i < panenList.size(); i++) {
            if (panenList.get(i).getIdPanen().equalsIgnoreCase(idPanen)) {
                Panen old = panenList.get(i);
                boolean ok = runTx(c -> {
                    exec(c, "UPDATE aktivitas_panen SET id_evaluasi = NULL WHERE id_panen = ?", idPanen);
                    exec(c, "UPDATE aktivitas_panen SET id_evaluasi = NULL WHERE id_evaluasi IN (SELECT id_evaluasi FROM evaluasi_panen WHERE aktivitas_panen_id_panen = ?)", idPanen);
                    exec(c, "DELETE FROM evaluasi_panen WHERE aktivitas_panen_id_panen = ?", idPanen);
                    exec(c, "DELETE FROM sawit WHERE id_produk IN (SELECT id_produk FROM produk_panen WHERE aktivitas_panen_id_panen = ?)", idPanen);
                    exec(c, "DELETE FROM karet WHERE id_produk IN (SELECT id_produk FROM produk_panen WHERE aktivitas_panen_id_panen = ?)", idPanen);
                    exec(c, "DELETE FROM produk_panen WHERE aktivitas_panen_id_panen = ?", idPanen);
                    exec(c, "DELETE FROM aktivitas_panen WHERE id_panen = ?", idPanen);
                }, "Gagal hapus panen");
                if (ok) {
                    panenList.remove(i);
                    logRiwayat("Hapus Data", idPanen, "Total: " + old.getTotalPanen() + " Kg", "Dihapus dari sistem");
                    reloadEvaluasi();
                }
                return ok;
            }
        }
        return false;
    }
 
    public synchronized List<Evaluasi> getEvaluasiList() { return new ArrayList<>(evaluasiList); }
 
    public synchronized String generateEvalId() {
        return String.format("EV%03d", evalCounter++);
    }
 
    public synchronized boolean addEvaluasi(Evaluasi evAsli) {
        Evaluasi ev = rapikanEvaluasi(evAsli);
        String uid = getCurrentUserId();
        if (uid == null) {
            System.err.println("Gagal simpan evaluasi: pengguna yang login tidak ditemukan");
            return false;
        }
        boolean ok = runTx(c -> exec(c,
                "INSERT INTO evaluasi_panen (id_evaluasi, tanggal_evaluasi, status, catatan_tindak_lanjut, aktivitas_panen_id_panen, id_user, catatan_evaluasi) VALUES (?, ?, ?, ?, NULL, ?, ?)",
                ev.getIdEval(), tanggalEvaluasi(ev), ev.getStatus(), ev.getCatatan(), uid, teksPeriode(ev)),
                "Gagal simpan evaluasi");
        if (ok) evaluasiList.add(ev);
        return ok;
    }
 
    public synchronized boolean updateEvaluasi(Evaluasi evAsli) {
        Evaluasi ev = rapikanEvaluasi(evAsli);
        for (int i = 0; i < evaluasiList.size(); i++) {
            if (evaluasiList.get(i).getIdEval().equalsIgnoreCase(ev.getIdEval())) {
                boolean ok = runTx(c -> exec(c,
                        "UPDATE evaluasi_panen SET tanggal_evaluasi=?, status=?, catatan_tindak_lanjut=?, catatan_evaluasi=? WHERE id_evaluasi=?",
                        tanggalEvaluasi(ev), ev.getStatus(), ev.getCatatan(), teksPeriode(ev), ev.getIdEval()),
                        "Gagal update evaluasi");
                if (ok) evaluasiList.set(i, ev);
                return ok;
            }
        }
        return false;
    }
 
    public synchronized boolean deleteEvaluasi(String idEval) {
        boolean ok = runTx(c -> {
            exec(c, "UPDATE aktivitas_panen SET id_evaluasi = NULL WHERE id_evaluasi = ?", idEval);
            exec(c, "DELETE FROM evaluasi_panen WHERE id_evaluasi = ?", idEval);
        }, "Gagal hapus evaluasi");
        if (ok) evaluasiList.removeIf(ev -> ev.getIdEval().equalsIgnoreCase(idEval));
        return ok;
    }
 
    public synchronized List<Riwayat> getRiwayatList() { return new ArrayList<>(riwayatList); }
 
    public synchronized void logRiwayat(String action, String idPanen, String before, String after) {
        String id = String.format("RW%03d", riwayatCounter++);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        riwayatList.add(0, new Riwayat(id, time, action, currentUser, idPanen, before, after));
    }
 
    public synchronized double[] calculateMetrics(String startDate, String endDate) {
        double totalKg = 0;
        double totalReject = 0;
        for (Panen p : panenList) {
            boolean inRange = true;
            if (startDate != null && !startDate.trim().isEmpty() && p.getTanggal().compareTo(startDate.trim()) < 0) {
                inRange = false;
            }
            if (endDate != null && !endDate.trim().isEmpty() && p.getTanggal().compareTo(endDate.trim()) > 0) {
                inRange = false;
            }
            if (inRange) {
                totalKg += p.getTotalPanen();
                totalReject += p.getTidakLayak();
            }
        }
        double rejectPercent = totalKg > 0 ? (totalReject / totalKg) * 100.0 : 0.0;
        return new double[]{ totalKg, rejectPercent };
    }
}
 