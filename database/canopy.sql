-- ========================================================
-- Skema Database CanopyONE - db_pt_canopy
-- Mengikuti dump db_pt_canopy (phpMyAdmin, MySQL 8.0)
-- ========================================================

CREATE DATABASE IF NOT EXISTS db_pt_canopy;
USE db_pt_canopy;

CREATE TABLE IF NOT EXISTS user (
    id_user VARCHAR(10) PRIMARY KEY,
    nama VARCHAR(100),
    username VARCHAR(50),
    password VARCHAR(100),
    no_telepon VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS admin (
    id_user VARCHAR(10) PRIMARY KEY,
    bagian_administrasi VARCHAR(100),
    CONSTRAINT fk_admin_user FOREIGN KEY (id_user) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS direksi (
    id_user VARCHAR(10) PRIMARY KEY,
    jabatan_direksi VARCHAR(100),
    CONSTRAINT fk_direksi_user FOREIGN KEY (id_user) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS petugas (
    id_user VARCHAR(10) PRIMARY KEY,
    wilayah_penugasan VARCHAR(100),
    CONSTRAINT fk_petugas_user FOREIGN KEY (id_user) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS blok_lahan (
    kode_blok_lahan VARCHAR(10) PRIMARY KEY,
    nama_blok_lahan VARCHAR(50),
    luas_blok_lahan INT,
    komoditas VARCHAR(25),
    id_user VARCHAR(10),
    CONSTRAINT fk_blok_lahan_user FOREIGN KEY (id_user) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS aktivitas_panen (
    id_panen VARCHAR(10) PRIMARY KEY,
    tanggal_panen DATE,
    total_jumlah_panen DECIMAL(10,2),
    hasil_panen_tidak_layak INT,
    kualitas_panen VARCHAR(15),
    kode_blok_lahan VARCHAR(10),
    id_evaluasi VARCHAR(10),
    id_user VARCHAR(10),
    CONSTRAINT fk_aktivitas_blok FOREIGN KEY (kode_blok_lahan) REFERENCES blok_lahan(kode_blok_lahan),
    CONSTRAINT fk_aktivitas_evaluasi FOREIGN KEY (id_evaluasi) REFERENCES evaluasi_panen(id_evaluasi),
    CONSTRAINT fk_aktivitas_panen_user FOREIGN KEY (id_user) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS evaluasi_panen (
    id_evaluasi VARCHAR(10) PRIMARY KEY,
    tanggal_evaluasi DATE,
    status VARCHAR(10) NOT NULL,
    catatan_tindak_lanjut VARCHAR(200),
    aktivitas_panen_id_panen VARCHAR(10),
    id_user VARCHAR(10) NOT NULL,
    catatan_evaluasi VARCHAR(200) NOT NULL,
    CONSTRAINT fk_evaluasi_aktivitas FOREIGN KEY (aktivitas_panen_id_panen) REFERENCES aktivitas_panen(id_panen),
    CONSTRAINT fk_evaluasi_panen_user FOREIGN KEY (id_user) REFERENCES user(id_user)
);

CREATE TABLE IF NOT EXISTS produk_panen (
    id_produk VARCHAR(10) PRIMARY KEY,
    aktivitas_panen_id_panen VARCHAR(10),
    nama_produk VARCHAR(50),
    tahun_tanam VARCHAR(4),
    CONSTRAINT fk_produk_aktivitas FOREIGN KEY (aktivitas_panen_id_panen) REFERENCES aktivitas_panen(id_panen)
);

CREATE TABLE IF NOT EXISTS sawit (
    id_produk VARCHAR(10) PRIMARY KEY,
    jumlah_tandan INT,
    jumlah_brondolan INT,
    CONSTRAINT fk_sawit_produk FOREIGN KEY (id_produk) REFERENCES produk_panen(id_produk)
);

CREATE TABLE IF NOT EXISTS karet (
    id_produk VARCHAR(10) PRIMARY KEY,
    kadar_air_karet INT,
    berat_kotor_karet INT,
    CONSTRAINT fk_karet_produk FOREIGN KEY (id_produk) REFERENCES produk_panen(id_produk)
);

CREATE TABLE IF NOT EXISTS riwayat_perubahan (
    id_riwayat VARCHAR(10) PRIMARY KEY,
    waktu_perubahan DATETIME NOT NULL,
    jenis_tindakan VARCHAR(50) NOT NULL,
    id_user VARCHAR(50) NOT NULL,
    id_panen VARCHAR(10),
    data_sebelum TEXT,
    data_setelah TEXT
);

-- ========================================================
-- Data contoh
-- ========================================================

INSERT INTO user (id_user, nama, username, password, no_telepon) VALUES
    ('U001', 'Ahmad Fauzi', 'ahmad.admin', 'admin123', '081234560001'),
    ('U002', 'Siti Rahmawati', 'siti.admin', 'admin123', '081234560002'),
    ('U003', 'Budi Santoso', 'budi.direksi', 'direksi123', '081234560003'),
    ('U004', 'Dewi Anggraini', 'dewi.direksi', 'direksi123', '081234560004'),
    ('U006', 'Andi Saputra', 'andi.petugas', 'petugas123', '081234560006'),
    ('U007', 'Muhammad Iqbal', 'iqbal.petugas', 'petugas123', '081234560007')
ON DUPLICATE KEY UPDATE nama=VALUES(nama), password=VALUES(password);

INSERT INTO admin (id_user, bagian_administrasi) VALUES
    ('U001', 'Administrasi Data Perkebunan'),
    ('U002', 'Administrasi Hasil Panen')
ON DUPLICATE KEY UPDATE bagian_administrasi=VALUES(bagian_administrasi);

INSERT INTO direksi (id_user, jabatan_direksi) VALUES
    ('U003', 'Direktur Utama'),
    ('U004', 'Direktur Operasional')
ON DUPLICATE KEY UPDATE jabatan_direksi=VALUES(jabatan_direksi);

INSERT INTO petugas (id_user, wilayah_penugasan) VALUES
    ('U006', 'Blok Sawit A01'),
    ('U007', 'Blok Sawit A02')
ON DUPLICATE KEY UPDATE wilayah_penugasan=VALUES(wilayah_penugasan);

INSERT INTO blok_lahan (kode_blok_lahan, nama_blok_lahan, luas_blok_lahan, komoditas, id_user) VALUES
    ('SW001', 'Blok Sawit A01', 25, 'Sawit', 'U001'),
    ('SW002', 'Blok Sawit A02', 30, 'Sawit', 'U001'),
    ('SW003', 'Blok Sawit A03', 28, 'Sawit', 'U001'),
    ('KR001', 'Blok Karet B01', 20, 'Karet', 'U001'),
    ('KR002', 'Blok Karet B02', 24, 'Karet', 'U001'),
    ('KR003', 'Blok Karet B03', 18, 'Karet', 'U001')
ON DUPLICATE KEY UPDATE nama_blok_lahan=VALUES(nama_blok_lahan);

INSERT INTO aktivitas_panen (id_panen, tanggal_panen, total_jumlah_panen, hasil_panen_tidak_layak, kualitas_panen, kode_blok_lahan, id_evaluasi, id_user) VALUES
    ('PN001', '2026-09-01', 4500.00, 120, 'Baik', 'SW001', NULL, 'U006'),
    ('PN002', '2026-09-02', 5200.00, 150, 'Baik', 'SW002', NULL, 'U006'),
    ('PN003', '2026-09-03', 4800.00, 180, 'Cukup', 'SW003', NULL, 'U006'),
    ('PN017', '2026-09-17', 1850.00, 60, 'Baik', 'KR001', NULL, 'U007'),
    ('PN018', '2026-09-18', 2100.00, 85, 'Baik', 'KR002', NULL, 'U007')
ON DUPLICATE KEY UPDATE total_jumlah_panen=VALUES(total_jumlah_panen);

INSERT INTO evaluasi_panen (id_evaluasi, tanggal_evaluasi, status, catatan_tindak_lanjut, aktivitas_panen_id_panen, id_user, catatan_evaluasi) VALUES
    ('EV001', '2026-09-02', 'Selesai', 'Hasil panen memenuhi standar kualitas.', 'PN001', 'U003', 'Hasil panen memenuhi target produksi.'),
    ('EV002', '2026-09-03', 'Proses', 'Produktivitas blok dalam kondisi baik.', 'PN002', 'U003', 'Kualitas panen sesuai standar perusahaan.')
ON DUPLICATE KEY UPDATE status=VALUES(status);

UPDATE aktivitas_panen SET id_evaluasi = 'EV001' WHERE id_panen = 'PN001';
UPDATE aktivitas_panen SET id_evaluasi = 'EV002' WHERE id_panen = 'PN002';

INSERT INTO produk_panen (id_produk, aktivitas_panen_id_panen, nama_produk, tahun_tanam) VALUES
    ('PR001', 'PN001', 'Sawit', '2015'),
    ('PR002', 'PN002', 'Sawit', '2016'),
    ('PR003', 'PN003', 'Sawit', '2017'),
    ('PR017', 'PN017', 'Karet', '2014'),
    ('PR018', 'PN018', 'Karet', '2015')
ON DUPLICATE KEY UPDATE nama_produk=VALUES(nama_produk);

INSERT INTO sawit (id_produk, jumlah_tandan, jumlah_brondolan) VALUES
    ('PR001', 350, 420),
    ('PR002', 410, 500),
    ('PR003', 375, 460)
ON DUPLICATE KEY UPDATE jumlah_tandan=VALUES(jumlah_tandan);

INSERT INTO karet (id_produk, kadar_air_karet, berat_kotor_karet) VALUES
    ('PR017', 35, 1850),
    ('PR018', 32, 2100)
ON DUPLICATE KEY UPDATE berat_kotor_karet=VALUES(berat_kotor_karet);

INSERT INTO riwayat_perubahan (id_riwayat, waktu_perubahan, jenis_tindakan, id_user, id_panen, data_sebelum, data_setelah) VALUES
    ('RW001', '2026-09-01 08:30:15', 'Tambah Data', 'andi.petugas', 'PN001', '-', 'Input awal panen Sawit SW001'),
    ('RW002', '2026-09-17 14:10:22', 'Tambah Data', 'iqbal.petugas', 'PN017', '-', 'Input awal panen Karet KR001')
ON DUPLICATE KEY UPDATE jenis_tindakan=VALUES(jenis_tindakan);
