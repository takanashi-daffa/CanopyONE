package dao;

import config.Koneksi;
import model.Evaluasi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvaluasiDAOImpl implements EvaluasiDAO {
    private static final String SELECT_BASE =
        "SELECT ep.id_evaluasi, ep.tanggal_evaluasi, ep.status, ep.catatan_tindak_lanjut, ep.catatan_evaluasi, " +
        "       ep.tanggal_evaluasi AS tanggal_awal, ep.tanggal_evaluasi AS tanggal_akhir, " +
        "       COALESCE(ap.total_jumlah_panen, 0) AS total_panen_evaluasi, " +
        "       CASE WHEN ap.total_jumlah_panen > 0 THEN (ap.hasil_panen_tidak_layak / ap.total_jumlah_panen) * 100 ELSE 0 END AS persen_tidak_layak " +
        "FROM evaluasi_panen ep " +
        "LEFT JOIN aktivitas_panen ap ON ep.aktivitas_panen_id_panen = ap.id_panen ";

    private static String status(String value) {
        String text = value == null ? "" : value.trim().toLowerCase();
        if (text.contains("selesai") || text.contains("sesuai")) return "Selesai";
        if (text.contains("pending") || text.contains("tunda")) return "Pending";
        if (text.isEmpty()) return "Proses";
        String raw = value.trim();
        return raw.length() > 10 ? raw.substring(0, 10) : raw;
    }

    @Override
    public List<Evaluasi> findAll() throws SQLException {
        List<Evaluasi> list = new ArrayList<>();
        Connection c = Koneksi.getConnection();
        if (c == null) return list;
        try (PreparedStatement ps = c.prepareStatement(SELECT_BASE + "ORDER BY ep.tanggal_evaluasi DESC, ep.id_evaluasi DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(EvaluasiMapper.toModel(rs));
            }
        }
        return list;
    }

    @Override
    public Evaluasi findById(String id) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return null;
        try (PreparedStatement ps = c.prepareStatement(SELECT_BASE + "WHERE ep.id_evaluasi = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return EvaluasiMapper.toModel(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void save(Evaluasi ev) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return;
        String insert = "INSERT INTO evaluasi_panen (id_evaluasi, tanggal_evaluasi, status, catatan_tindak_lanjut, id_user, catatan_evaluasi) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE tanggal_evaluasi=?, status=?, catatan_tindak_lanjut=?, id_user=?, catatan_evaluasi=?";
        try (PreparedStatement ps = c.prepareStatement(insert)) {
            ps.setString(1, ev.getIdEval());
            ps.setString(2, ev.getTglAkhir());
            ps.setString(3, status(ev.getStatus()));
            ps.setString(4, ev.getCatatan());
            ps.setString(5, "U001");
            ps.setString(6, ev.getCatatan() == null || ev.getCatatan().trim().isEmpty() ? "-" : ev.getCatatan());
            ps.setString(7, ev.getTglAkhir());
            ps.setString(8, status(ev.getStatus()));
            ps.setString(9, ev.getCatatan());
            ps.setString(10, "U001");
            ps.setString(11, ev.getCatatan() == null || ev.getCatatan().trim().isEmpty() ? "-" : ev.getCatatan());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Evaluasi ev) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return;
        String update = "UPDATE evaluasi_panen SET tanggal_evaluasi=?, status=?, catatan_tindak_lanjut=?, catatan_evaluasi=? WHERE id_evaluasi=?";
        try (PreparedStatement ps = c.prepareStatement(update)) {
            ps.setString(1, ev.getTglAkhir());
            ps.setString(2, status(ev.getStatus()));
            ps.setString(3, ev.getCatatan());
            ps.setString(4, ev.getCatatan() == null || ev.getCatatan().trim().isEmpty() ? "-" : ev.getCatatan());
            ps.setString(5, ev.getIdEval());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return;
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM evaluasi_panen WHERE id_evaluasi = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
