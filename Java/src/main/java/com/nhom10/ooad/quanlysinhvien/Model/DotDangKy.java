package com.nhom10.ooad.quanlysinhvien.Model;

import java.util.Date;

public class DotDangKy {
    private String maDotDangKy;
    private String maHocKy;
    private Date thoiGianMo;
    private Date thoiGianDong;

    public DotDangKy() {
    }

    public DotDangKy(String maDotDangKy, String maHocKy, Date thoiGianMo, Date thoiGianDong) {
        this.maDotDangKy = maDotDangKy;
        this.maHocKy = maHocKy;
        this.thoiGianMo = thoiGianMo;
        this.thoiGianDong = thoiGianDong;
    }

    public String getMaDotDangKy() { return maDotDangKy; }
    public void setMaDotDangKy(String maDotDangKy) { this.maDotDangKy = maDotDangKy; }

    public String getMaHocKy() { return maHocKy; }
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }

    public Date getThoiGianMo() { return thoiGianMo; }
    public void setThoiGianMo(Date thoiGianMo) { this.thoiGianMo = thoiGianMo; }

    public Date getThoiGianDong() { return thoiGianDong; }
    public void setThoiGianDong(Date thoiGianDong) { this.thoiGianDong = thoiGianDong; }
}