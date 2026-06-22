package com.nhom10.ooad.quanlysinhvien.Model;

public class BangDiem {
    private String maBangDiem;
    private String mssv;
    private String maLopHP;
    private String maGV;
    private double diemQT;
    private double diemCK;
    private double diemTongKet;
    private String trangThai;

    public BangDiem() {
    }

    public BangDiem(String maBangDiem, String mssv, String maLopHP, String maGV, double diemQT, double diemCK, double diemTongKet, String trangThai) {
        this.maBangDiem = maBangDiem;
        this.mssv = mssv;
        this.maLopHP = maLopHP;
        this.maGV = maGV;
        this.diemQT = diemQT;
        this.diemCK = diemCK;
        this.diemTongKet = diemTongKet;
        this.trangThai = trangThai;
    }

    public String getMaBangDiem() { return maBangDiem; }
    public void setMaBangDiem(String maBangDiem) { this.maBangDiem = maBangDiem; }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public String getMaLopHP() { return maLopHP; }
    public void setMaLopHP(String maLopHP) { this.maLopHP = maLopHP; }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public double getDiemQT() { return diemQT; }
    public void setDiemQT(double diemQT) { this.diemQT = diemQT; }

    public double getDiemCK() { return diemCK; }
    public void setDiemCK(double diemCK) { this.diemCK = diemCK; }

    public double getDiemTongKet() { return diemTongKet; }
    public void setDiemTongKet(double diemTongKet) { this.diemTongKet = diemTongKet; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}