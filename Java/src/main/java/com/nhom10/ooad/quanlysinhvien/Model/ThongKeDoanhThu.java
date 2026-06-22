package com.nhom10.ooad.quanlysinhvien.Model;

/**
 * ThongKeDoanhThu - DTO ket qua bao cao doanh thu theo hoc ky.
 */
public class ThongKeDoanhThu {
    private String maHocKy;
    private int    soHoaDon;
    private long   tongPhaiThu;  // SUM(TongTienNop)
    private long   daThu;        // SUM(da nop)
    private long   conLai;       // tongPhaiThu - daThu

    public ThongKeDoanhThu() {}

    public ThongKeDoanhThu(String maHocKy, int soHoaDon, long tongPhaiThu, long daThu) {
        this.maHocKy = maHocKy;
        this.soHoaDon = soHoaDon;
        this.tongPhaiThu = tongPhaiThu;
        this.daThu = daThu;
        this.conLai = tongPhaiThu - daThu;
    }

    public String getMaHocKy() { return maHocKy; }
    public void setMaHocKy(String maHocKy) { this.maHocKy = maHocKy; }
    public int getSoHoaDon() { return soHoaDon; }
    public void setSoHoaDon(int soHoaDon) { this.soHoaDon = soHoaDon; }
    public long getTongPhaiThu() { return tongPhaiThu; }
    public void setTongPhaiThu(long tongPhaiThu) { this.tongPhaiThu = tongPhaiThu; }
    public long getDaThu() { return daThu; }
    public void setDaThu(long daThu) { this.daThu = daThu; }
    public long getConLai() { return conLai; }
    public void setConLai(long conLai) { this.conLai = conLai; }
}