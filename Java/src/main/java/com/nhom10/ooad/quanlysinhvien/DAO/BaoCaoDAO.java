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
 * (Neu JOIN truc tiep LichSuThanhToan se bi nhan ban TongTienNop khi 1 hoa don nop nhieu lan.)
 */
public class BaoCaoDAO {

    /** Doanh thu cua 1 hoc ky. */
    public ThongKeDoanhThu doanhThuHocKy(String maHocKy) {
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
            ps.setString(1, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ThongKeDoanhThu(maHocKy, rs.getInt("SoHoaDon"),
                            rs.getLong("TongPhaiThu"), rs.getLong("DaThu"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // khong co hoa don nao
        return new ThongKeDoanhThu(maHocKy, 0, 0, 0);
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
                ds.add(new ThongKeDoanhThu(rs.getString("MaHocKy"), rs.getInt("SoHoaDon"),
                        rs.getLong("TongPhaiThu"), rs.getLong("DaThu")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Danh sach cong no: cac hoa don chua nop du (trang thai khac 'Da nop') trong 1 ky. */
    public List<HoaDonView> danhSachNo(String maHocKy) {
        List<HoaDonView> ds = new ArrayList<>();
        String sql =
            "SELECT hd.MaHoaDon, hd.MSSV, sv.HoTen, lh.TenLop, hd.MaHocKy, hd.TongTinChi, "
          + "       hd.DonGiaTinChi, hd.TongTienNop, hd.TrangThaiThanhToan, "
          + "       ISNULL((SELECT SUM(ls.SoTienNop) FROM LichSuThanhToan ls "
          + "               WHERE ls.MaHoaDon = hd.MaHoaDon),0) AS DaNop "
          + "FROM HoaDonHocPhi hd "
          + "JOIN SinhVien sv ON hd.MSSV = sv.MSSV "
          + "JOIN LopHoc   lh ON sv.MaLopQuanLy = lh.MaLopQuanLy "
          + "WHERE hd.MaHocKy = ? AND hd.TrangThaiThanhToan <> N'Đã nộp' "
          + "ORDER BY hd.MaHoaDon";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ds.add(HoaDonHocPhiDAO.docHoaDonView(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }
}
