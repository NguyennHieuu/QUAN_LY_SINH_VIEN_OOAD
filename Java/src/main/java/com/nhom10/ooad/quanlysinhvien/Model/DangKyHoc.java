package com.nhom10.ooad.quanlysinhvien.Model;

import java.util.Date;

public class DangKyHoc {
    private String maDangKy;
    private String mssv;
    private String maLopHP;
    private Date thoiGianDangKy;
    private String trangThai;

    // Constructor rỗng
    public DangKyHoc() {
    }

    // Constructor đầy đủ tham số
    public DangKyHoc(String maDangKy, String mssv, String maLopHP, Date thoiGianDangKy, String trangThai) {
        this.maDangKy = maDangKy;
        this.mssv = mssv;
        this.maLopHP = maLopHP;
        this.thoiGianDangKy = thoiGianDangKy;
        this.trangThai = trangThai;
    }

    // Getter và Setter
    public String getMaDangKy() { return maDangKy; }
    public void setMaDangKy(String maDangKy) { this.maDangKy = maDangKy; }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public String getMaLopHP() { return maLopHP; }
    public void setMaLopHP(String maLopHP) { this.maLopHP = maLopHP; }

    public Date getThoiGianDangKy() { return thoiGianDangKy; }
    public void setThoiGianDangKy(Date thoiGianDangKy) { this.thoiGianDangKy = thoiGianDangKy; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}