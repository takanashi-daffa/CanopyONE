package model;

public class BlokLahan {
    private String kodeBlok;
    private String namaLahan;
    private double luasLahan;
    private String komoditas;

    public BlokLahan(String kodeBlok, String namaLahan, double luasLahan, String komoditas) {
        this.kodeBlok = kodeBlok;
        this.namaLahan = namaLahan;
        this.luasLahan = luasLahan;
        this.komoditas = komoditas;
    }

    public String getKodeBlok() { return kodeBlok; }
    public void setKodeBlok(String kodeBlok) { this.kodeBlok = kodeBlok; }

    public String getNamaLahan() { return namaLahan; }
    public void setNamaLahan(String namaLahan) { this.namaLahan = namaLahan; }

    public double getLuasLahan() { return luasLahan; }
    public void setLuasLahan(double luasLahan) { this.luasLahan = luasLahan; }

    public String getKomoditas() { return komoditas; }
    public void setKomoditas(String komoditas) { this.komoditas = komoditas; }

    @Override
    public String toString() {
        return kodeBlok + " - " + namaLahan;
    }
}
