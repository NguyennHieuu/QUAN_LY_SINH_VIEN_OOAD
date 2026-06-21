package com.nhom10.ooad.quanlysinhvien.Model;

/**
 * HoaDonHocPhi - thuc the (Entity) anh xa bang HoaDonHocPhi.
 * Tien (VND) dung kieu long, anh xa tu DECIMAL(18,0) trong CSDL.
 */
public class HoaDonHocPhi {
    private String maHoaDon;
    private String mssv;
    private String maHocKy;
    private int    tongTinChi;
    private long   donGiaTinChi;
    private long   tongTienNop;       // tong phai nop = tongTinChi * donGiaTinChi
    private String trangThaiThanhToan; // 'Chua nop' / 'Con no' / 'Da nop'
    private String ngayLapHoaDon;

    public HoaDonHocPhi() {}

    public HoaDonHocPhi(String maHoaDon, String mssv, String maHocKy, int tongTinChi,
                        long donGiaTinChi, long tongTienNop,
                        String trangThaiThanhToan, String ngayLapHoaDon) {
        this.maHoaDon = maHoaDon;
        this.mssv = mssv;
        this.maHocKy = maHocKy;
        this.tongTinChi = tongTinChi;
        this.donGiaTinChi = donGiaTinChi;
        this.tongTienNop = tongTienNop;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.ngayLapHoaDon = ngayLapHoaDon;
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }
    public String getMaHocKy() { return maHocKy; }
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
    public int getTongTinChi() { return tongTinChi; }
    public void setTongTinChi(int tongTinChi) { this.tongTinChi = tongTinChi; }
    public long getDonGiaTinChi() { return donGiaTinChi; }
    public void setDonGiaTinChi(long donGiaTinChi) { this.donGiaTinChi = donGiaTinChi; }
    public long getTongTienNop() { return tongTienNop; }
    public void setTongTienNop(long tongTienNop) { this.tongTienNop = tongTienNop; }
    public String getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(String t) { this.trangThaiThanhToan = t; }
    public String getNgayLapHoaDon() { return ngayLapHoaDon; }
    public void setNgayLapHoaDon(String ngayLapHoaDon) { this.ngayLapHoaDon = ngayLapHoaDon; }
}
