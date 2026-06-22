package com.nhom10.ooad.quanlysinhvien.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.nhom10.ooad.quanlysinhvien.Model.LichSuThanhToan;
import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;

/**
 * LichSuThanhToanDAO - DAO cho bang LichSuThanhToan.
 * 🌟 ĐÃ CẬP NHẬT: Tối ưu hóa chuẩn hóa chuỗi trim() phòng chống lệch mã hóa đơn khi nộp tiền.
 */
public class LichSuThanhToanDAO {

    /**
     * Sinh ma giao dich tiep theo dang GD000001, GD000002...
     * Lay MAX hien tai, cat phan so, +1, dem 0 cho du 6 chu so.
     */
    private String sinhMaGiaoDich(Connection con) throws Exception {
        String sql = "SELECT MAX(MaGiaoDich) FROM LichSuThanhToan";
        int tiep = 1;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String max = rs.getString(1);          // vd "GD000007" hoac null
                if (max != null && max.trim().length() > 2) {
                    tiep = Integer.parseInt(max.trim().substring(2)) + 1;
                }
            }
        }
        return String.format("GD%06d", tiep);
    }

    /**
     * Them 1 giao dich nop tien. Ngay gio do CSDL tu dien (DEFAULT GETDATE()).
     * @return ma giao dich vua tao, hoac null neu loi.
     */
    public String themGiaoDich(String maHoaDon, long soTien, String hinhThuc) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return null;

        String sql = "INSERT INTO LichSuThanhToan (MaGiaoDich, MaHoaDon, SoTienNop, HinhThuc) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection con = DataBaseConnection.getConnection()) {
            String maGD = sinhMaGiaoDich(con);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maGD);
                ps.setString(2, maHoaDon.trim()); // 🌟 ĐÃ SỬA: Cắt khoảng trắng an toàn
                ps.setLong(3, soTien);
                ps.setString(4, hinhThuc != null ? hinhThuc.trim() : "Tiền mặt"); // 🌟 ĐÃ SỬA: Tránh null hình thức
                ps.executeUpdate();
            }
            return maGD;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Lay lich su nop tien cua 1 hoa don (de xem chi tiet). */
    public List<LichSuThanhToan> getLichSuByHoaDon(String maHoaDon) {
        List<LichSuThanhToan> ds = new ArrayList<>();
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return ds;

        String sql = "SELECT MaGiaoDich, MaHoaDon, NgayGioThanhToan, SoTienNop, HinhThuc "
                   + "FROM LichSuThanhToan WHERE MaHoaDon = ? ORDER BY MaGiaoDich";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon.trim()); // 🌟 ĐÃ SỬA: Cắt khoảng trắng an toàn
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new LichSuThanhToan(
                        rs.getString("MaGiaoDich"), 
                        rs.getString("MaHoaDon"),
                        String.valueOf(rs.getTimestamp("NgayGioThanhToan")),
                        rs.getLong("SoTienNop"), 
                        rs.getString("HinhThuc")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}