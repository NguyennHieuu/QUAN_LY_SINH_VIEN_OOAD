package com.nhom10.ooad.quanlysinhvien.Model;

import java.util.Date;

/**
 * Thẻ CRC ID: 1 - Lớp thực thể sinhVien
 */
public class SinhVien {
    private String mssv;
    private String hoTen;
    private Date ngaySinh; // dOB trong thẻ CRC
    private String gioiTinh;
    private String maLopQuanLy; // Khóa ngoại liên kết với lớp LopHoc
    private String tenDangNhap; // Khóa ngoại liên kết với lớp TaiKhoan

    public SinhVien() {}

    public SinhVien(String mssv, String hoTen, Date ngaySinh, String gioiTinh, String maLopQuanLy, String tenDangNhap) {
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.maLopQuanLy = maLopQuanLy;
        this.tenDangNhap = tenDangNhap;
    }

    // Getters và Setters tuân thủ camelCase [5]
    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getMaLopQuanLy() { return maLopQuanLy; }
    public void setMaLopQuanLy(String maLopQuanLy) { this.maLopQuanLy = maLopQuanLy; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
}