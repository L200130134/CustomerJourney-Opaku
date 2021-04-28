package com.rikyahmadfathoni.test.opaku.model;

public class DiscoverProductModel {

    private String idProduk;
    private String namaProduk;
    private String hargaProduk;

    public DiscoverProductModel() {
        super();
    }

    public DiscoverProductModel(String namaProduk, String hargaProduk) {
        this.namaProduk = namaProduk;
        this.hargaProduk = hargaProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public void setHargaProduk(String hargaProduk) {
        this.hargaProduk = hargaProduk;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getHargaProduk() {
        return hargaProduk;
    }

    public String getIdProduk() {
        return idProduk;
    }
}
