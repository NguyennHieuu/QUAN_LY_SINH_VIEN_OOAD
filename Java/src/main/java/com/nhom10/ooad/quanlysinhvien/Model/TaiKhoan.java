package com.nhom10.ooad.quanlysinhvien.Model;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; // Giáo vụ, Sinh viên, Giảng viên, Kế toán
    private boolean trangThaiHoatDong;

    public TaiKhoan() {}
    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, boolean trangThaiHoatDong) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThaiHoatDong = trangThaiHoatDong;
    }
    // Getters và Setters theo quy tắc camelCase
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    public boolean isTrangThaiHoatDong() { return trangThaiHoatDong; }
    public void setTrangThaiHoatDong(boolean trangThaiHoatDong) { this.trangThaiHoatDong = trangThaiHoatDong; }
}