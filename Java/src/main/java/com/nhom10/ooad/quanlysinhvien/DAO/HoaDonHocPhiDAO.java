package com.nhom10.ooad.quanlysinhvien.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonHocPhi;
import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;

/**
 * HoaDonHocPhiDAO - tang truy xuat du lieu (DAO) cho bang HoaDonHocPhi.
 * Chua cac truy van SQL lien quan den hoa don hoc phi.
 */
public class HoaDonHocPhiDAO {

    /**
     * Dem tong so tin chi 1 sinh vien da dang ky thanh cong trong 1 hoc ky.
     * JOIN: DangKyHoc -> LopHocPhan -> HocPhan, roi SUM(SoTC).
     * (Day la phan "tu dong dem so tin chi" trong nhiem vu Ke toan.)
     */
    public int getTongTinChi(String mssv, String maHocKy) {
        String sql =
            "SELECT ISNULL(SUM(hp.SoTC),0) AS TongTC "
          + "FROM DangKyHoc dk "
          + "JOIN LopHocPhan lhp ON dk.MaLopHP = lhp.MaLopHP "
          + "JOIN HocPhan hp     ON lhp.MaHP = hp.MaHP "
          + "WHERE dk.MSSV = ? AND lhp.MaHocKy = ? AND dk.TrangThai = N'Thành công'";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("TongTC");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Kiem tra 1 sinh vien da co hoa don cua hoc ky nay chua (tranh tao trung). */
    public boolean daCoHoaDon(String mssv, String maHocKy) {
        String sql = "SELECT COUNT(*) FROM HoaDonHocPhi WHERE MSSV = ? AND MaHocKy = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mssv);
            ps.setString(2, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * TINH HOC PHI + TAO HOA DON hang loat cho 1 hoc ky.
     * Quy trinh:
     *   B1: lay danh sach (MSSV, tong TC) cua moi SV co dang ky trong ky -> doc HET vao List.
     *   B2: dong ResultSet, roi lap qua List de INSERT (tranh loi mo 2 cau lenh cung luc - MARS).
     *   B3: SV nao da co hoa don thi bo qua.
     * @return so hoa don MOI duoc tao.
     */
    public int tinhVaTaoHoaDon(String maHocKy, long donGia) {
        String sqlChon =
            "SELECT dk.MSSV, SUM(hp.SoTC) AS TongTC "
          + "FROM DangKyHoc dk "
          + "JOIN LopHocPhan lhp ON dk.MaLopHP = lhp.MaLopHP "
          + "JOIN HocPhan hp     ON lhp.MaHP = hp.MaHP "
          + "WHERE lhp.MaHocKy = ? AND dk.TrangThai = N'Thành công' "
          + "GROUP BY dk.MSSV";

        // B1: doc het danh sach vao bo nho
        List<String[]> dsSV = new ArrayList<>(); // moi phan tu: [mssv, soTC]
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlChon)) {
            ps.setString(1, maHocKy);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsSV.add(new String[]{ rs.getString("MSSV"),
                                           String.valueOf(rs.getInt("TongTC")) });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        // B2 + B3: lap va chen
        String sqlChen =
            "INSERT INTO HoaDonHocPhi (MaHoaDon, MSSV, MaHocKy, TongTinChi, DonGiaTinChi, TongTienNop) "
          + "VALUES (?, ?, ?, ?, ?, ?)";
        int demMoi = 0;
        for (String[] sv : dsSV) {
            String mssv = sv[0];
            int soTC = Integer.parseInt(sv[1]);
            if (daCoHoaDon(mssv, maHocKy)) continue; // da co thi bo qua

            String maHoaDon = "HD-" + maHocKy + "-" + mssv; // ma duy nhat / SV / ky
            long tongTien = (long) soTC * donGia;
            try (Connection con = DataBaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sqlChen)) {
                ps.setString(1, maHoaDon);
                ps.setString(2, mssv);
                ps.setString(3, maHocKy);
                ps.setInt(4, soTC);
                ps.setLong(5, donGia);
                ps.setLong(6, tongTien);
                ps.executeUpdate();
                demMoi++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return demMoi;
    }

    /**
     * Tra cuu/loc hoa don de hien thi (UC07.2).
     * @param keyword   tim theo MSSV hoac Ho ten ("" = tat ca)
     * @param maHocKy   loc theo hoc ky ("" = tat ca)
     * @param trangThai loc theo trang thai ("" = tat ca)
     * Cot "DaNop" duoc tinh bang subquery -> chi 1 cau lenh, khong loi MARS.
     */
    public List<HoaDonView> timHoaDon(String keyword, String maHocKy, String trangThai) {
        List<HoaDonView> ds = new ArrayList<>();
        String sql =
            "SELECT hd.MaHoaDon, hd.MSSV, sv.HoTen, lh.TenLop, hd.MaHocKy, hd.TongTinChi, "
          + "       hd.DonGiaTinChi, hd.TongTienNop, hd.TrangThaiThanhToan, "
          + "       ISNULL((SELECT SUM(ls.SoTienNop) FROM LichSuThanhToan ls "
          + "               WHERE ls.MaHoaDon = hd.MaHoaDon),0) AS DaNop "
          + "FROM HoaDonHocPhi hd "
          + "JOIN SinhVien sv ON hd.MSSV = sv.MSSV "
          + "JOIN LopHoc   lh ON sv.MaLopQuanLy = lh.MaLopQuanLy "
          + "WHERE (sv.MSSV LIKE ? OR sv.HoTen LIKE ?) "
          + "  AND (? = '' OR hd.MaHocKy = ?) "
          + "  AND (? = '' OR hd.TrangThaiThanhToan = ?) "
          + "ORDER BY hd.MaHoaDon";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String kw = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, maHocKy == null ? "" : maHocKy);
            ps.setString(4, maHocKy == null ? "" : maHocKy);
            ps.setString(5, trangThai == null ? "" : trangThai);
            ps.setString(6, trangThai == null ? "" : trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ds.add(docHoaDonView(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    /** Lay 1 hoa don theo ma (dung khi thanh toan). */
    public HoaDonHocPhi getHoaDonById(String maHoaDon) {
        String sql = "SELECT * FROM HoaDonHocPhi WHERE MaHoaDon = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new HoaDonHocPhi(
                        rs.getString("MaHoaDon"), rs.getString("MSSV"), rs.getString("MaHocKy"),
                        rs.getInt("TongTinChi"), rs.getLong("DonGiaTinChi"), rs.getLong("TongTienNop"),
                        rs.getString("TrangThaiThanhToan"), String.valueOf(rs.getDate("NgayLapHoaDon")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Tong so tien da nop cua 1 hoa don. */
    public long tongDaNop(String maHoaDon) {
        String sql = "SELECT ISNULL(SUM(SoTienNop),0) FROM LichSuThanhToan WHERE MaHoaDon = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Cap nhat trang thai thanh toan cua hoa don. */
    public void capNhatTrangThai(String maHoaDon, String trangThai) {
        String sql = "UPDATE HoaDonHocPhi SET TrangThaiThanhToan = ? WHERE MaHoaDon = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setString(2, maHoaDon);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * LOGIC CHAN NO: sinh vien co dang con no hoc phi khong?
     * Tra ve true neu con it nhat 1 hoa don CHUA o trang thai 'Da nop'.
     * (Day la nen tang cho ham contract kiemTraSinhVienNoHocPhi ben Controller.)
     */
    public boolean coNoHocPhi(String mssv) {
        String sql = "SELECT COUNT(*) FROM HoaDonHocPhi "
                   + "WHERE MSSV = ? AND TrangThaiThanhToan <> N'Đã nộp'";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mssv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Doc 1 dong ResultSet thanh HoaDonView (dung lai cho nhieu truy van). */
    static HoaDonView docHoaDonView(ResultSet rs) throws Exception {
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
