package com.nhom10.ooad.quanlysinhvien.Model;

/**
 * DiemSoView - DTO tổng hợp dữ liệu từ 3 bảng (BangDiem, LopHocPhan, HocPhan)
 * Giúp tầng View hiển thị trực tiếp mà không cần chạy vòng lặp tra cứu DAO.
 */
public class DiemSoView {
    private String maHocPhan;
    private String maLopHP;
    private String tenHocPhan;
    private int soTinChi;
    private String trongSo; // Ghép chuỗi dạng "0.3 - 0.7" từ DB
    private Double diemQT;
    private Double diemCK;
    private Double diemTongKet;
    private String trangThai;

    // Getters và Setters
    public String getMaHocPhan() { return maHocPhan; }
    public void setMaHocPhan(String maHocPhan) { this.maHocPhan = maHocPhan; }
    public String getMaLopHP() { return maLopHP; }
    public void setMaLopHP(String maLopHP) { this.maLopHP = maLopHP; }
    public String getTenHocPhan() { return tenHocPhan; }
    public void setTenHocPhan(String tenHocPhan) { this.tenHocPhan = tenHocPhan; }
    public int getSoTinChi() { return soTinChi; }
    public void setSoTinChi(int soTinChi) { this.soTinChi = soTinChi; }
    public String getTrongSo() { return trongSo; }
    public void setTrongSo(String trongSo) { this.trongSo = trongSo; }
    public Double getDiemQT() { return diemQT; }
    public void setDiemQT(Double diemQT) { this.diemQT = diemQT; }
    public Double getDiemCK() { return diemCK; }
    public void setDiemCK(Double diemCK) { this.diemCK = diemCK; }
    public Double getDiemTongKet() { return diemTongKet; }
    public void setDiemTongKet(Double diemTongKet) { this.diemTongKet = diemTongKet; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}