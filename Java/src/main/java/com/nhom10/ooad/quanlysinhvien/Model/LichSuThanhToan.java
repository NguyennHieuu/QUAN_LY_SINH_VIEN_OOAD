package com.nhom10.ooad.quanlysinhvien.Model;

/**
 * LichSuThanhToan - thuc the anh xa bang LichSuThanhToan (moi lan nop tien la 1 dong).
 */
public class LichSuThanhToan {
    private String maGiaoDich;
    private String maHoaDon;
    private String ngayGioThanhToan;
    private long   soTienNop;
    private String hinhThuc;

    public LichSuThanhToan() {}

    public LichSuThanhToan(String maGiaoDich, String maHoaDon, String ngayGioThanhToan,
                           long soTienNop, String hinhThuc) {
        this.maGiaoDich = maGiaoDich;
        this.maHoaDon = maHoaDon;
        this.ngayGioThanhToan = ngayGioThanhToan;
        this.soTienNop = soTienNop;
        this.hinhThuc = hinhThuc;
    }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public String getNgayGioThanhToan() { return ngayGioThanhToan; }
    public void setNgayGioThanhToan(String ngayGioThanhToan) { this.ngayGioThanhToan = ngayGioThanhToan; }
    public long getSoTienNop() { return soTienNop; }
    public void setSoTienNop(long soTienNop) { this.soTienNop = soTienNop; }
    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }
}
