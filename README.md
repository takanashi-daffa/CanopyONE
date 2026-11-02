<div align="center">

# 🌴 CanopyONE

### Sistem Informasi Pencatatan dan Pemantauan Hasil Panen Perkebunan Sawit dan Karet pada PT Canopy Berbasis Java

**Kelompok 2**

</div>

---

## 👥 Anggota Kelompok

| No. | Nama | NIM |
|:---:|---|:---:|
| 1 | Daffa Arkhabista | 2509116018 |
| 2 | Muhammad Fahriel | 2509116050 |
| 3 | Alya Aulia | 2509116060 |
| 4 | Iqbal Nuriz Ramadhan | 2509116067 |
| 5 | Nabila Viviana Asri | 2509116098 |

---

## 📑 Daftar Isi

- [📖 1. Deskripsi Program](#-1-deskripsi-program)
- [🌴 2. Gambaran Program](#-2-gambaran-program)
  - [🔐 Tampilan Login](#-tampilan-login)
  - [🌱 Tampilan Kelola Blok Lahan](#-tampilan-kelola-blok-lahan)
  - [👥 Tampilan Kelola Data Pegawai](#-tampilan-kelola-data-pegawai)
  - [🌾 Tampilan Kelola Data Panen](#-tampilan-kelola-data-panen)
  - [📊 Tampilan Rekapitulasi Hasil Panen](#-tampilan-rekapitulasi-hasil-panen)
  - [📝 Tampilan Evaluasi Hasil Panen](#-tampilan-evaluasi-hasil-panen)
- [💻 3. Implementasi Program Java](#-3-implementasi-program-java)
  - [3.1 Koneksi Database](#31-koneksi-database)
  - [3.2 Login dan Autentikasi](#32-login-dan-autentikasi)
  - [3.3 Pembagian Hak Akses](#33-pembagian-hak-akses)
  - [3.4 Implementasi CRUD](#34-implementasi-crud)
    - [3.4.1 Create (Menambahkan Data)](#341-create-menambahkan-data)
    - [3.4.2 Read (Menampilkan Data)](#342-read-menampilkan-data)
    - [3.4.3 Update (Mengubah Data)](#343-update-mengubah-data)
    - [3.4.4 Delete (Menghapus Data)](#344-delete-menghapus-data)
  - [3.5 Validasi Input](#35-validasi-input)
- [🗄️ Implementasi Sistem Basis Data](#-implementasi-sistem-basis-data)
- [📁 Struktur Proyek](#-struktur-proyek)
- [📸 Dokumentasi Aplikasi](#-dokumentasi-aplikasi)

---

## 📖 1. Deskripsi Program

CanopyONE merupakan aplikasi berbasis desktop yang dikembangkan menggunakan bahasa pemrograman Java untuk membantu proses pencatatan dan pemantauan hasil panen perkebunan sawit dan karet pada PT Canopy.

Aplikasi ini menggunakan Java Swing sebagai antarmuka pengguna dan MySQL sebagai basis data. Sistem menyediakan pengelolaan data pegawai, blok lahan, hasil panen, rekapitulasi, serta evaluasi hasil panen berdasarkan hak akses pengguna.

---

## 🌴 2. Gambaran Program

### 🔐 Tampilan Login

<img width="877" height="605" alt="image" src="https://github.com/user-attachments/assets/8043f66e-fa42-4f55-bee3-57be2605bf3b" />

Halaman login merupakan tampilan awal aplikasi yang digunakan pengguna untuk memasukkan username dan password sebelum mengakses sistem.

### 🌱 Tampilan Kelola Blok Lahan

<img width="1168" height="710" alt="image" src="https://github.com/user-attachments/assets/2779e5a4-60c5-4385-8801-c0cf88b4385d" />

Halaman ini digunakan untuk mengelola data blok lahan perkebunan, meliputi kode blok, nama blok, luas lahan, komoditas, dan tahun tanam. Pengguna dapat menambahkan, mengubah, dan menghapus data blok lahan.

### 👥 Tampilan Kelola Data Pegawai

<img width="1168" height="712" alt="image" src="https://github.com/user-attachments/assets/c17335fc-0e8c-4fc6-bfe6-dd13a8d05b7c" />

Halaman ini digunakan untuk mengelola data pegawai yang memiliki akses ke aplikasi. Informasi yang dikelola meliputi nama pegawai, username, password, nomor telepon, dan hak akses pengguna (Admin, Petugas, atau Direksi).

### 🌾 Tampilan Kelola Data Panen

<img width="1172" height="713" alt="image" src="https://github.com/user-attachments/assets/d3869b0e-74a3-4b53-b3f3-b0f84cbc7431" />

Halaman ini digunakan untuk mencatat dan mengelola aktivitas hasil panen kelapa sawit dan karet. Data yang dikelola meliputi tanggal panen, blok lahan, komoditas, jumlah hasil panen, dan informasi kualitas hasil panen.

### 📊 Tampilan Rekapitulasi Hasil Panen

<img width="1162" height="711" alt="image" src="https://github.com/user-attachments/assets/2cbb7f54-3faa-4497-a855-7ce40f1eedd1" />

Halaman ini digunakan untuk melihat ringkasan hasil produksi perkebunan berdasarkan periode dan komoditas. Pengguna dapat menggunakan filter untuk menampilkan data rekapitulasi sesuai kebutuhan.

### 📝 Tampilan Evaluasi Hasil Panen

<img width="1167" height="710" alt="image" src="https://github.com/user-attachments/assets/8a850ffa-708a-4561-a9b2-3398970bdbdd" />

Halaman ini digunakan untuk melakukan evaluasi terhadap hasil panen berdasarkan periode tertentu. Informasi yang ditampilkan meliputi total hasil panen, hasil panen tidak layak, status evaluasi, dan catatan tindak lanjut.

---

## 💻 3. Implementasi Program Java

### 3.1 Koneksi Database

Koneksi database digunakan untuk menghubungkan aplikasi Java dengan MySQL. Pengaturan koneksi terdapat pada file `Koneksi.java`.

**File: `config/Koneksi.java`**

```java
public static synchronized Connection getKoneksi() {
    try {
        if (conn != null && !conn.isClosed()) {
            return conn;
        }
    } catch (SQLException ignored) {
        conn = null;
    }

    ensureDriver();

    String[] hosts = new String[] { "localhost", "127.0.0.1" };
    String[] passes = new String[] { "", "root" };
    String dbName = "db_pt_canopy";
    String params = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    for (String host : hosts) {
        for (String pass : passes) {
            String url = "jdbc:mysql://" + host + ":3306/" + dbName + params;

            try {
                conn = DriverManager.getConnection(url, "root", pass);

                if (conn != null && !conn.isClosed()) {
                    return conn;
                }
            } catch (Exception e) {
                lastError = e.getMessage();
            }
        }
    }

    return null;
}
```

Kode tersebut digunakan untuk membuat koneksi aplikasi ke database MySQL `db_pt_canopy`.

### 3.2 Login dan Autentikasi

Login digunakan untuk memeriksa identitas pengguna sebelum memasuki aplikasi.

**File: `view/LoginFrame.java`**

```java
String user = txtUsername.getText().trim();
String pass = new String(txtPassword.getPassword());

if (user.isEmpty() || pass.isEmpty()) {
    JOptionPane.showMessageDialog(
        this,
        "Username dan password wajib diisi!"
    );
    return;
}

Pegawai auth = DataStore.getInstance()
        .findPegawai(user, pass);

if (auth == null) {
    JOptionPane.showMessageDialog(
        this,
        "Username atau password salah!"
    );
    txtPassword.setText("");
    return;
}
```

Jika data pengguna sesuai, sistem akan melanjutkan proses login dan menampilkan halaman berdasarkan peran pengguna.

### 3.3 Pembagian Hak Akses

Sistem memiliki tiga jenis pengguna, yaitu Admin, Petugas, dan Direksi. Pengaturan hak akses dilakukan melalui class `RoleAccess.java`.

**File: `controller/RoleAccess.java`**

```java
public static boolean bolehAkses(
        String role, Fitur fitur) {

    if (fitur == null) {
        return false;
    }

    String r = normalisasiRole(role);

    if (r.equals("ADMIN")) {
        return true;
    }

    switch (r) {
        case "PETUGAS":
            return fitur == Fitur.KELOLA_PANEN
                    || fitur == Fitur.LIHAT_REKAP;

        case "DIREKSI":
            return fitur == Fitur.LIHAT_REKAP
                    || fitur == Fitur.LIHAT_EVALUASI;

        default:
            return false;
    }
}
```

Pembagian hak akses pengguna:

| Pengguna | Hak Akses |
|---|---|
| Admin | Mengelola data pegawai, blok lahan, hasil panen, dan evaluasi. |
| Petugas | Mengelola hasil panen sawit dan karet serta melihat rekapitulasi. |
| Direksi | Melihat rekapitulasi dan mengelola evaluasi. |

### 3.4 Implementasi CRUD

CRUD (Create, Read, Update, Delete) digunakan untuk mengelola data yang tersimpan di dalam database. Implementasi pengelolaan data panen terdapat pada class `DataStore.java`.

#### 3.4.1 Create (Menambahkan Data)

Proses Create digunakan untuk menambahkan data hasil panen baru ke database.

**File: `model/DataStore.java`**

```java
public synchronized boolean addPanen(Panen p) {
    String kodeBlok = ambilKodeBlok(p.getBlokLahan());
    String uid = getCurrentUserId();

    boolean ok = runTx(c -> {
        exec(c,
            "INSERT INTO aktivitas_panen " +
            "(id_panen, tanggal_panen, total_jumlah_panen, " +
            "hasil_panen_tidak_layak, kualitas_panen, " +
            "kode_blok_lahan, id_user) VALUES (?, ?, ?, ?, ?, ?, ?)",
            p.getIdPanen(),
            p.getTanggal(),
            p.getTotalPanen(),
            (int) Math.round(p.getTidakLayak()),
            p.getKualitas(),
            kodeBlok,
            uid
        );

        simpanDetailPanen(c, p);
    }, "Gagal simpan panen");

    if (ok) {
        panenList.add(p);
    }

    return ok;
}
```

Method `addPanen()` menyimpan data utama panen ke tabel `aktivitas_panen`, kemudian menyimpan detail komoditas melalui method `simpanDetailPanen()`.

#### 3.4.2 Read (Menampilkan Data)

Proses Read digunakan untuk mengambil data yang tersimpan di database.

**File: `model/DataStore.java`**

```java
private void loadBlok(Statement st) throws Exception {
    List<BlokLahan> baru = new ArrayList<>();

    try (ResultSet rs = st.executeQuery(
        "SELECT kode_blok_lahan, nama_blok_lahan, " +
        "luas_blok_lahan, komoditas " +
        "FROM blok_lahan ORDER BY kode_blok_lahan")) {

        while (rs.next()) {
            baru.add(new BlokLahan(
                rs.getString("kode_blok_lahan"),
                rs.getString("nama_blok_lahan"),
                rs.getDouble("luas_blok_lahan"),
                rs.getString("komoditas"),
                2020
            ));
        }
    }

    blokList.clear();
    blokList.addAll(baru);
}
```

Kode tersebut menggunakan perintah `SELECT` untuk mengambil data blok lahan, kemudian menyimpannya ke dalam `ArrayList`.

#### 3.4.3 Update (Mengubah Data)

Proses Update digunakan untuk memperbarui data panen yang sudah tersimpan.

**File: `model/DataStore.java`**

```java
boolean ok = runTx(c -> {
    exec(c,
        "UPDATE aktivitas_panen SET tanggal_panen=?, " +
        "total_jumlah_panen=?, hasil_panen_tidak_layak=?, " +
        "kualitas_panen=?, kode_blok_lahan=? " +
        "WHERE id_panen=?",
        p.getTanggal(),
        p.getTotalPanen(),
        (int) Math.round(p.getTidakLayak()),
        p.getKualitas(),
        kodeBlok,
        p.getIdPanen()
    );

    simpanDetailPanen(c, p);
}, "Gagal update panen");
```

Perintah `UPDATE` digunakan untuk memperbarui informasi hasil panen berdasarkan ID panen.

#### 3.4.4 Delete (Menghapus Data)

Proses Delete digunakan untuk menghapus data panen yang tidak lagi diperlukan.

**File: `model/DataStore.java`**

```java
boolean ok = runTx(c -> {
    exec(c,
        "DELETE FROM evaluasi_panen " +
        "WHERE aktivitas_panen_id_panen = ?",
        idPanen
    );

    exec(c,
        "DELETE FROM produk_panen " +
        "WHERE aktivitas_panen_id_panen = ?",
        idPanen
    );

    exec(c,
        "DELETE FROM aktivitas_panen " +
        "WHERE id_panen = ?",
        idPanen
    );
}, "Gagal hapus panen");
```

Penghapusan dilakukan dengan memperhatikan data yang memiliki hubungan dengan aktivitas panen agar tidak meninggalkan data terkait.

### 3.5 Validasi Input

Validasi input digunakan untuk memeriksa data yang dimasukkan pengguna sebelum diproses oleh sistem.

**File: `controller/InputValidator.java`**

```java
public static boolean isPositiveNumber(String str) {
    if (!isValidString(str)) return false;

    try {
        double val = Double.parseDouble(str.trim());
        return val > 0;
    } catch (NumberFormatException e) {
        return false;
    }
}
```

Validasi tersebut memastikan bahwa input berupa angka yang lebih besar dari nol.

---

## 🗄️ Implementasi Sistem Basis Data

<!-- Bagian ini akan dilengkapi nanti. -->

---

## 📁 Struktur Proyek

<!-- Tambahkan struktur folder dan file proyek di bagian ini. -->

---

## 📸 Dokumentasi Aplikasi

<!-- Tambahkan screenshot aplikasi di bagian ini. -->
