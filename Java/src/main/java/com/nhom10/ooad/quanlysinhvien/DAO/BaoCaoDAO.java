package com.nhom10.ooad.quanlysinhvien.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.ThongKeDoanhThu;
import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;

/**
 * BaoCaoDAO - DAO phuc vu BAO CAO TAI CHINH (doanh thu, cong no).
 *
 * Ky thuat chong dem trung: dung bang dan xuat (derived table) "t" tinh san
 * so tien da nop CUA TUNG hoa don bang subquery, roi moi SUM o ngoai.
 */
public class BaoCaoDAO {

    /** Doanh thu cua 1 hoc ky. */
    public ThongKeDoanhThu doanhThuHocKy(String maHocKy) {
        if (maHocKy == null || maHocKy.trim().isEmpty()) {
            return new ThongKeDoanhThu("", 0, 0, 0);
        }
        
        String sql =
            "SELECT t.MaHocKy, COUNT(*) AS SoHoaDon, "
          + "       SUM(t.TongTienNop) AS TongPhaiThu, SUM(t.DaNop) AS DaThu "
          + "FROM ( SELECT hd.MaHoaDon, hd.MaHocKy, hd.TongTienNop, "
          + "              ISNULL((SELECT SUM(ls.SoTienNop) FROM LichSuThanhToan ls "
          + "                      WHERE ls.MaHoaDon = hd.MaHoaDon),0) AS DaNop "
          + "       FROM HoaDonHocPhi hd WHERE hd.MaHocKy = ? ) t "
          + "GROUP BY t.MaHocKy";
          
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHocKy.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ThongKeDoanhThu(
                        maHocKy.trim(), 
                        rs.getInt("SoHoaDon"),
                        rs.getLong("TongPhaiThu"), 
                        rs.getLong("DaThu")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ThongKeDoanhThu(maHocKy.trim(), 0, 0, 0);
    }

    /** Doanh thu tat ca cac hoc ky (moi ky 1 dong). */
    public List<ThongKeDoanhThu> doanhThuTatCaKy() {
        List<ThongKeDoanhThu> ds = new ArrayList<>();
        String sql =
            "SELECT t.MaHocKy, COUNT(*) AS SoHoaDon, "
          + "       SUM(t.TongTienNop) AS TongPhaiThu, SUM(t.DaNop) AS DaThu "
          + "FROM ( SELECT hd.MaHoaDon, hd.MaHocKy, hd.TongTienNop, "
          + "              ISNULL((SELECT SUM(ls.SoTienNop) FROM LichSuThanhToan ls "
          + "                      WHERE ls.MaHoaDon = hd.MaHoaDon),0) AS DaNop "
          + "       FROM HoaDonHocPhi hd ) t "
          + "GROUP BY t.MaHocKy ORDER BY t.MaHocKy";
          
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ds.add(new ThongKeDoanhThu(
                    rs.getString("MaHocKy"), 
                    rs.getInt("SoHoaDon"),
                    rs.getLong("TongPhaiThu"), 
                    rs.getLong("DaThu")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** * Danh sach cong no: cac hoa don chua nop du (trang thai khac 'Da nop') trong 1 ky. 
     * 🌟 ĐÃ SỬA: Tách hàm map dữ liệu nội bộ độc lập để giải phóng lỗi truy cập access modifier chéo gói.
     */
    public List<HoaDonView> danhSachNo(String maHocKy) {
        List<HoaDonView> ds = new ArrayList<>();
        if (maHocKy == null || maHocKy.trim().isEmpty()) return ds;

        String sql =
            "SELECT hd.MaHoaDon, hd.MSSV, sv.HoTen, lh.TenLop, hd.MaHocKy, hd.TongTinChi, "
          + "       hd.DonGiaTinChi, hd.TongTienNop, hd.TrangThaiThanhToan, "
          + "       ISNULL((SELECT SUM(ls.SoTienNop) FROM LichSuThanhToan ls "
          + "               WHERE ls.MaHoaDon = hd.MaHoaDon),0) AS DaNop "
          + "FROM HoaDonHocPhi hd "
          + "JOIN SinhVien sv ON hd.MSSV = sv.MSSV "
          + "LEFT JOIN LopHoc   lh ON sv.MaLopQuanLy = lh.MaLopQuanLy "
          + "WHERE hd.MaHocKy = ? AND hd.TrangThaiThanhToan <> N'Đã nộp' "
          + "ORDER BY hd.MaHoaDon";
          
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHocKy.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(chuyenDoiSangHoaDonView(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** * Hàm tiện ích nội bộ giải bọc dữ liệu ResultSet thành đối tượng HoaDonView an toàn.
     */
    private HoaDonView chuyenDoiSangHoaDonView(ResultSet rs) throws Exception {
        System.out.println("-> [BaoCaoDAO]: Mapping dữ liệu công nợ sinh viên.");
        HoaDonView v = new HoaDonView();
        v.setMaHoaDon(rs.getString("MaHoaDon"));
        v.setMssv(rs.getString("MSSV"));
        v.setHoTen(rs.getString("HoTen"));
        v.setTenLop(rs.getString("TenLop"));
        v.setMaHocKy(rs.getString("MaHocKy"));
        v.setTongTinChi(rs.getInt("TongTinChi"));
        v.setDonGia(rs.getLong("DonGiaTinChi"));
        v.setTongTienNop(rs.getLong("TongTienNop"));
        long daNop = rs.getLong("DaNop");
        v.setDaNop(daNop);
        v.setConNo(rs.getLong("TongTienNop") - daNop);
        v.setTrangThai(rs.getString("TrangThaiThanhToan"));
        return v;
    }
}