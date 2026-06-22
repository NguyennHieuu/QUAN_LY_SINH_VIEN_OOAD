package com.nhom10.ooad.quanlysinhvien.Model;

/**
 * LopHPView - DTO gom dữ liệu từ bảng LopHocPhan và HocPhan.
 * Phục vụ hiển thị danh sách môn mở và môn đã đăng ký kèm Tên học phần, Số TC chuẩn xác.
 */
public class LopHPView {
    private String maLopHP;
    private String maHP;
    private String tenHP;
    private int soTC;
    private String maHocKy;
    private String maGV;
    private int siSoHienTai;
    private int siSoToiDa;

    // Getters và Setters
    public String getMaLopHP() { return maLopHP; }
    public void setMaLopHP(String maLopHP) { this.maLopHP = maLopHP; }
    public String getMaHP() { return maHP; }
    public void setMaHP(String maHP) { this.maHP = maHP; }
    public String getTenHP() { return tenHP; }
    public void setTenHP(String tenHP) { this.tenHP = tenHP; }
    public int getSoTC() { return soTC; }
    public void setSoTC(int soTC) { this.soTC = soTC; }
    public String getMaHocKy() { return maHocKy; }
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }
    public int getSiSoHienTai() { return siSoHienTai; }
    public void setSiSoHienTai(int siSoHienTai) { this.siSoHienTai = siSoHienTai; }
    public int getSiSoToiDa() { return siSoToiDa; }
    public void setSiSoToiDa(int siSoToiDa) { this.siSoToiDa = siSoToiDa; }
}