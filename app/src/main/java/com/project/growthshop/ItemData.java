package com.project.growthshop;

public class ItemData {
    private String nama;
    private String status;
    private String harga;
    private String jumlah;
    private String url;
    private String id;
    private String kategori;
    private String id_item;


    public ItemData() {
        // Diperlukan konstruktor kosong untuk deserialisasi Firebase
    }

    public ItemData(String nama, String status, String harga, String jumlah, String url, String id, String kategori,String id_item) {
        this.nama = nama;
        this.status = status;
        this.harga = harga;
        this.jumlah = jumlah;
        this.url = url;
        this.id = id;
        this.id_item= id_item;
        this.kategori = kategori;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getId_item() {
        return id_item;
    }

    public void setId_item(String id_item) {
        this.id_item = id_item;
    }
}
