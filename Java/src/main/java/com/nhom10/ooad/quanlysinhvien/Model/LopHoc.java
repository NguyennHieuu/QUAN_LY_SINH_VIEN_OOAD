package com.nhom10.ooad.quanlysinhvien.Model;

public class LopHoc {
    private String maLopQuanLy;
    private String tenLop;
    private int siSo;
    private String maGV; // Giáo viên chủ nhiệm/quản lý lớp

    public LopHoc() {}

    public LopHoc(String maLopQuanLy, String tenLop, int siSo, String maGV) {
        this.maLopQuanLy = maLopQuanLy;
        this.tenLop = tenLop;
        this.siSo = siSo;
        this.maGV = maGV;
    }

    // Getters và Setters theo quy tắc camelCase
    public String getMaLopQuanLy() { return maLopQuanLy; }
    public void setMaLopQuanLy(String maLopQuanLy) { this.maLopQuanLy = maLopQuanLy; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public int getSiSo() { return siSo; }
    public void setSiSo(int siSo) { this.siSo = siSo; }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }
}