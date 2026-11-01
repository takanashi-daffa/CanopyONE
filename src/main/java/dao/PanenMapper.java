package dao;

import model.Panen;
import java.sql.ResultSet;
import java.sql.SQLException;

final class PanenMapper {
    private PanenMapper() { }

    static Panen toModel(ResultSet rs) throws SQLException {
        String idPanen = rs.getString("id_panen");
        String tgl = rs.getString("tanggal_panen");
        String kodeBlok = rs.getString("kode_blok_lahan");
        String namaBlok = "";
        String kom = "Sawit";
        try {
            namaBlok = rs.getString("nama_blok_lahan");
            if (namaBlok == null) namaBlok = "";
        } catch (SQLException ignored) {}
        try {
            kom = rs.getString("komoditas");
            if (kom == null || kom.isEmpty()) kom = "Sawit";
        } catch (SQLException ignored) {}

        String blokDisplay = kodeBlok + (namaBlok.isEmpty() ? "" : " - " + namaBlok);

        double total = rs.getDouble("total_jumlah_panen");
        double tidakLayak = rs.getDouble("hasil_panen_tidak_layak");
        String kualitas = rs.getString("kualitas_panen");

        double input1 = 0;
        double input2 = 0;
        try {
            if ("Karet".equalsIgnoreCase(kom)) {
                input1 = rs.getDouble("berat_kotor_karet");
                input2 = rs.getDouble("kadar_air_karet");
            } else {
                input1 = rs.getDouble("jumlah_tandan");
                input2 = rs.getDouble("jumlah_brondolan");
            }
        } catch (SQLException ignored) {}

        return new Panen(idPanen, tgl, blokDisplay, kom, input1, input2, total, tidakLayak, kualitas);
    }
}
