package dao;

import model.Evaluasi;
import java.sql.ResultSet;
import java.sql.SQLException;

final class EvaluasiMapper {
    private EvaluasiMapper() { }

    static Evaluasi toModel(ResultSet rs) throws SQLException {
        String idEval = rs.getString("id_evaluasi");
        String tgl = rs.getString("tanggal_evaluasi");
        String status = rs.getString("status");
        String catatan = rs.getString("catatan_tindak_lanjut");
        if (catatan == null || catatan.trim().isEmpty()) {
            catatan = rs.getString("catatan_evaluasi");
        }
        if (catatan == null) catatan = "-";

        String tglAwal = rs.getString("tanggal_awal");
        String tglAkhir = rs.getString("tanggal_akhir");
        if (tglAwal == null) tglAwal = tgl;
        if (tglAkhir == null) tglAkhir = tgl;

        double total = rs.getDouble("total_panen_evaluasi");
        double persen = rs.getDouble("persen_tidak_layak");
        return new Evaluasi(idEval, tglAwal, tglAkhir, total, persen, status, catatan);
    }
}
