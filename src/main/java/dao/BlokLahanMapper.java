package dao;

import model.BlokLahan;
import java.sql.ResultSet;
import java.sql.SQLException;

final class BlokLahanMapper {
    private BlokLahanMapper() { }

    static BlokLahan toModel(ResultSet rs) throws SQLException {
        String kode = rs.getString("kode_blok_lahan");
        String nama = rs.getString("nama_blok_lahan");
        double luas = rs.getDouble("luas_blok_lahan");
        String kom = rs.getString("komoditas");
        return new BlokLahan(kode, nama, luas, kom);
    }
}
