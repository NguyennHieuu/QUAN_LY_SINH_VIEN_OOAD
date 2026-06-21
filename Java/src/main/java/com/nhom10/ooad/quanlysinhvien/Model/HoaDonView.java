package com.nhom10.ooad.quanlysinhvien.Model;

/**
 * HoaDonView - DTO (Data Transfer Object) de HIEN THI hoa don len bang.
 *
 * Vi sao tach rieng khoi entity HoaDonHocPhi?
 *  - Bang luoi can them HoTen (lay tu bang SinhVien) + TenLop (bang LopHoc)
 *    va cot "da nop", "con no" (tinh tu LichSuThanhToan).
 *  - Dung DTO nay, KE TOAN KHONG can import lop SinhVien.java cua ban khac
 *    => khong bi phu thuoc code thanh vien khac, lam doc lap duoc.
 */
public class HoaDonView {
    private String maHoaDon;
    private String mssv;
    private String hoTen;
    private String tenLop;
    private String maHocKy;
    private int    tongTinChi;
    private long   donGia;
    private long   tongTienNop; // tong phai nop
    private long   daNop;       // = SUM(LichSuThanhToan.SoTienNop)
    private long   conNo;       // = tongTienNop - daNop
    private String trangThai;

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }
    public String getMaHocKy() { return maHocKy; }
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
    public int getTongTinChi() { return tongTinChi; }
    public void setTongTinChi(int tongTinChi) { this.tongTinChi = tongTinChi; }
    public long getDonGia() { return donGia; }
    public void setDonGia(long donGia) { this.donGia = donGia; }
    public long getTongTienNop() { return tongTienNop; }
    public void setTongTienNop(long tongTienNop) { this.tongTienNop = tongTienNop; }
    public long getDaNop() { return daNop; }
    public void setDaNop(long daNop) { this.daNop = daNop; }
    public long getConNo() { return conNo; }
    public void setConNo(long conNo) { this.conNo = conNo; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
