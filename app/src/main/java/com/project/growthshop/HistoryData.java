package com.project.growthshop;

public class HistoryData {

    private String namaCostumer;
    private String statusPemesanan;
    private double hargaPerItem;
    private int jumlahItem;
    private String url;
    private String kodePemesanan;
    private String namaBarang;
    private String totalHarga;
    private String gambarBarang;

    public HistoryData() {
        // Diperlukan konstruktor kosong untuk deserialisasi Firebase
    }

    public HistoryData(String namaCostumer, String statusPemesanan, double hargaPerItem, int jumlahItem, String gambarBarang, String kodePemesanan, String namaBarang, String totalHarga) {
        this.namaCostumer = namaCostumer;
        this.statusPemesanan = statusPemesanan;
        this.hargaPerItem = hargaPerItem;
        this.jumlahItem = jumlahItem;
        this.gambarBarang = gambarBarang;
        this.kodePemesanan = kodePemesanan;
        this.namaBarang = namaBarang;
        this.totalHarga = totalHarga;
    }

    public String getNamaCostumer() {
        return namaCostumer;
    }

    public void setNamaCostumer(String namaCostumer) {
        this.namaCostumer = namaCostumer;
    }

    public String getStatusPemesanan() {
        return statusPemesanan;
    }

    public void setStatusPemesanan(String statusPemesanan) {
        this.statusPemesanan = statusPemesanan;
    }

    public double getHargaPerItem() {
        return hargaPerItem;
    }

    public void setHargaPerItem(double hargaPerItem) {
        this.hargaPerItem = hargaPerItem;
    }

    public int getJumlahItem() {
        return jumlahItem;
    }

    public void setJumlahItem(int jumlahItem) {
        this.jumlahItem = jumlahItem;
    }

    public String getGambarBarang() {
        return gambarBarang;
    }

    public void setGambarBarang(String gambarBarang) {
        this.gambarBarang = gambarBarang;
    }

    public String getKodePemesanan() {
        return kodePemesanan;
    }

    public void setKodePemesanan(String kodePemesanan) {
        this.kodePemesanan = kodePemesanan;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }
}
