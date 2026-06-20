package com.nhom10.ooad.quanlysinhvien.Model;

public class GiangVien {
    private String maGV;
    private String hoTenGV;
    private String sdt;
    private String email;
    private String donViCongTac;
    private String tenDangNhap; // Khóa ngoại liên kết với lớp TaiKhoan

    public GiangVien() {}

    public GiangVien(String maGV, String hoTenGV, String sdt, String email, String donViCongTac, String tenDangNhap) {
        this.maGV = maGV;
        this.hoTenGV = hoTenGV;
        this.sdt = sdt;
        this.email = email;
        this.donViCongTac = donViCongTac;
        this.tenDangNhap = tenDangNhap;
    }

    // Getters và Setters
    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public String getHoTenGV() { return hoTenGV; }
    public void setHoTenGV(String hoTenGV) { this.hoTenGV = hoTenGV; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDonViCongTac() { return donViCongTac; }
    public void setDonViCongTac(String donViCongTac) { this.donViCongTac = donViCongTac; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
}
