package com.nhom10.ooad.quanlysinhvien.Model;

public class CTDT {
    private String maCTDT;
    private String tenNganh;
    private String nienKhoa;
    private int tongSoTC;

    public CTDT() {}

    public CTDT(String maCTDT, String tenNganh, String nienKhoa, int tongSoTC) {
        this.maCTDT = maCTDT;
        this.tenNganh = tenNganh;
        this.nienKhoa = nienKhoa;
        this.tongSoTC = tongSoTC;
    }

    // Getters và Setters
    public String getMaCTDT() { return maCTDT; }
    public void setMaCTDT(String maCTDT) { this.maCTDT = maCTDT; }

    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }

    public String getNienKhoa() { return nienKhoa; }
    public void setNienKhoa(String nienKhoa) { this.nienKhoa = nienKhoa; }

    public int getTongSoTC() { return tongSoTC; }
    public void setTongSoTC(int tongSoTC) { this.tongSoTC = tongSoTC; }
}