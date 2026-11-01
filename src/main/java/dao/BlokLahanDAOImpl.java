package dao;

import config.Koneksi;
import model.BlokLahan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlokLahanDAOImpl implements BlokLahanDAO {
    private static final String SELECT_ALL = "SELECT kode_blok_lahan, nama_blok_lahan, luas_blok_lahan, komoditas FROM blok_lahan ORDER BY kode_blok_lahan";
    private static final String SELECT_BY_KODE = "SELECT kode_blok_lahan, nama_blok_lahan, luas_blok_lahan, komoditas FROM blok_lahan WHERE kode_blok_lahan = ?";
    private static final String INSERT = "INSERT INTO blok_lahan (kode_blok_lahan, nama_blok_lahan, luas_blok_lahan, komoditas) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE nama_blok_lahan=?, luas_blok_lahan=?, komoditas=?";
    private static final String UPDATE = "UPDATE blok_lahan SET nama_blok_lahan=?, luas_blok_lahan=?, komoditas=? WHERE kode_blok_lahan=?";
    private static final String DELETE = "DELETE FROM blok_lahan WHERE kode_blok_lahan=?";

    @Override
    public List<BlokLahan> findAll() throws SQLException {
        List<BlokLahan> list = new ArrayList<>();
        Connection c = Koneksi.getConnection();
        if (c == null) return list;
        try (PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(BlokLahanMapper.toModel(rs));
            }
        }
        return list;
    }

    @Override
    public BlokLahan findByKode(String kode) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return null;
        try (PreparedStatement ps = c.prepareStatement(SELECT_BY_KODE)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return BlokLahanMapper.toModel(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void save(BlokLahan b) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return;
        try (PreparedStatement ps = c.prepareStatement(INSERT)) {
            ps.setString(1, b.getKodeBlok());
            ps.setString(2, b.getNamaLahan());
            ps.setDouble(3, b.getLuasLahan());
            ps.setString(4, b.getKomoditas());
            ps.setString(5, b.getNamaLahan());
            ps.setDouble(6, b.getLuasLahan());
            ps.setString(7, b.getKomoditas());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(BlokLahan b) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return;
        try (PreparedStatement ps = c.prepareStatement(UPDATE)) {
            ps.setString(1, b.getNamaLahan());
            ps.setDouble(2, b.getLuasLahan());
            ps.setString(3, b.getKomoditas());
            ps.setString(4, b.getKodeBlok());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String kode) throws SQLException {
        Connection c = Koneksi.getConnection();
        if (c == null) return;
        try (PreparedStatement ps = c.prepareStatement(DELETE)) {
            ps.setString(1, kode);
            ps.executeUpdate();
        }
    }
}

