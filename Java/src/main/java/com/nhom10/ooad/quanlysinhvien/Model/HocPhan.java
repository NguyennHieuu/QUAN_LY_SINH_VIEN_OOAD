package com.nhom10.ooad.quanlysinhvien.Model;

public class HocPhan {
    private String maHP;
    private String tenHP;
    private int soTC;
    private String loaiHP; // Bắt buộc hoặc Tự chọn
    private String maCTDT; // Thuộc chương trình đào tạo nào

    public HocPhan() {}

    public HocPhan(String maHP, String tenHP, int soTC, String loaiHP, String maCTDT) {
        this.maHP = maHP;
        this.tenHP = tenHP;
        this.soTC = soTC;
        this.loaiHP = loaiHP;
        this.maCTDT = maCTDT;
    }

    // Getters và Setters
    public String getMaHP() { return maHP; }
    public void setMaHP(String maHP) { this.maHP = maHP; }

    public String getTenHP() { return tenHP; }
    public void setTenHP(String tenHP) { this.tenHP = tenHP; }

    public int getSoTC() { return soTC; }
    public void setSoTC(int soTC) { this.soTC = soTC; }

    public String getLoaiHP() { return loaiHP; }
    public void setLoaiHP(String loaiHP) { this.loaiHP = loaiHP; }

    public String getMaCTDT() { return maCTDT; }
    public void setMaCTDT(String maCTDT) { this.maCTDT = maCTDT; }
}