package com.nhom10.ooad.quanlysinhvien.Model;

public class LopHocPhan {
    private String maLopHP;
    private String maHP;
    private String maHocKy;
    private String maGV;
    private int siSoHienTai; // 🌟 Đã sửa từ siSo thành siSoHienTai
    private int siSoToiDa;   // 🌟 Đã sửa từ siSoToiDa (chữ t thường) thành siSoToiDa (chữ T hoa)
    private double trongSoQT;
    private double trongSoCK;

    public LopHocPhan() {
    }

    public LopHocPhan(String maLopHP, String maHP, String maHocKy, String maGV, int siSoHienTai, int siSoToiDa, double trongSoQT, double trongSoCK) {
        this.maLopHP = maLopHP;
        this.maHP = maHP;
        this.maHocKy = maHocKy;
        this.maGV = maGV;
        this.siSoHienTai = siSoHienTai;
        this.siSoToiDa = siSoToiDa;
        this.trongSoQT = trongSoQT;
        this.trongSoCK = trongSoCK;
    }

    // Getter và Setter đã được chuẩn hóa theo tên mới
    public String getMaLopHP() { return maLopHP; }
    public void setMaLopHP(String maLopHP) { this.maLopHP = maLopHP; }

    public String getMaHP() { return maHP; }
    public void setMaHP(String maHP) { this.maHP = maHP; }

    public String getMaHocKy() { return maHocKy; }
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public int getSiSoHienTai() { return siSoHienTai; }
    public void setSiSoHienTai(int siSoHienTai) { this.siSoHienTai = siSoHienTai; }

    public int getSiSoToiDa() { return siSoToiDa; }
    public void setSiSoToiDa(int siSoToiDa) { this.siSoToiDa = siSoToiDa; }

    public double getTrongSoQT() { return trongSoQT; }
    public void setTrongSoQT(double trongSoQT) { this.trongSoQT = trongSoQT; }

    public double getTrongSoCK() { return trongSoCK; }
    public void setTrongSoCK(double trongSoCK) { this.trongSoCK = trongSoCK; }
}