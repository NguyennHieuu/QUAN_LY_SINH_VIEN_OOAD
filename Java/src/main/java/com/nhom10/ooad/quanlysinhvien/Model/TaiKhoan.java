package com.nhom10.ooad.quanlysinhvien.Model;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro; // "Giáo vụ", "Giảng viên", "Sinh viên", "Kế toán"
    private int trangThaiHoatDong; // 1 là đang hoạt động, 0 là bị khóa

    // Constructor rỗng
    public TaiKhoan() {}

    // Constructor đầy đủ 4 tham số
    public TaiKhoan(String tenDangNhap, String matKhau, String vaiTro, int trangThaiHoatDong) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    // ====================================================================
    // HỆ THỐNG GETTER VÀ SETTER ĐẦY ĐỦ 4 THUỘC TÍNH
    // ====================================================================
    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public int getTrangThaiHoatDong() { return trangThaiHoatDong; }
    public void setTrangThaiHoatDong(int trangThaiHoatDong) { this.trangThaiHoatDong = trangThaiHoatDong; }
}