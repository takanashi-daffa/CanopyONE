package controller;

import java.util.Locale;

public final class RoleAccess {

    private RoleAccess() {
        // Mencegah objek dibuat dari class ini.
    }

    public enum Fitur {
        KELOLA_USER,
        KELOLA_BLOK,
        KELOLA_PANEN,
        LIHAT_REKAP,
        LIHAT_EVALUASI
    }

    public static String normalisasiRole(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }

        String hasil = role.trim().toUpperCase(Locale.ROOT);

        // Menyamakan istilah Pegawai dan Petugas.
        if (hasil.equals("PEGAWAI")) {
            return "PETUGAS";
        }

        return hasil;
    }

    public static boolean bolehAkses(
            String role, Fitur fitur) {

        if (fitur == null) {
            return false;
        }

        String r = normalisasiRole(role);

        // Admin mempunyai seluruh hak akses.
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
                // Role tidak dikenal selalu ditolak.
                return false;
        }
    }
}