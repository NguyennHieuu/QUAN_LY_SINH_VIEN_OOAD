package com.nhom10.ooad.quanlysinhvien.DAO;

import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.LopHoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tầng DAO cho thực thể Lớp học hành chính (LopHoc)
 * Phục vụ nhóm chức năng quản lý lớp của Giáo vụ (UC02)
 *
 * 🌟 ĐÃ SỬA QUAN TRỌNG: Vì DataBaseConnection dùng 1 Connection SINGLETON
 * cho toàn bộ ứng dụng, các hàm bên dưới KHÔNG còn dùng try-with-resources
 * trên Connection (tránh tự động đóng connection chung giữa app).
 * Chỉ đóng PreparedStatement/ResultSet. Đồng thời recalculateSiSo() ép
 * setAutoCommit(true) để đảm bảo UPDATE sĩ số luôn được commit ngay,
 * không bị rollback ngầm do connection đang ở transaction chưa commit
 * từ nơi gọi trước đó (ví dụ SinhVienDAO.insert()).
 */
public class LopHocDAO {

    /**
     * UC02.1: Thêm mới một lớp hành chính vào hệ thống
     */
    public boolean insert(LopHoc lop) {
        String sql = "INSERT INTO LopHoc (MaLopQuanLy, TenLop, SiSo, MaGV) VALUES (?, ?, ?, ?)";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true); // 🌟 đảm bảo commit ngay, không phụ thuộc transaction trước đó

            ps.setString(1, lop.getMaLopQuanLy());
            ps.setString(2, lop.getTenLop());
            ps.setInt(3, lop.getSiSo());
            ps.setString(4, lop.getMaGV());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC02.2: Cập nhật thông tin lớp học (Tên lớp, Giáo viên quản lý)
     * Giữ nguyên Khóa chính MaLopQuanLy và Sĩ số lớp
     */
    public boolean update(LopHoc lop) {
        String sql = "UPDATE LopHoc SET TenLop = ?, MaGV = ? WHERE MaLopQuanLy = ?";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);

            ps.setString(1, lop.getTenLop());
            ps.setString(2, lop.getMaGV());
            ps.setString(3, lop.getMaLopQuanLy());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UC02.3: Xóa lớp hành chính khỏi hệ thống
     * ⚠️ RÀNG BUỘC NGHIỆP VỤ BẢO MẬT: Chỉ thực thi lệnh DELETE nếu sĩ số lớp = 0
     */
    public boolean delete(String maLopQuanLy) {
        String sqlCheck = "SELECT SiSo FROM LopHoc WHERE MaLopQuanLy = ?";
        String sqlDelete = "DELETE FROM LopHoc WHERE MaLopQuanLy = ?";

        Connection conn = DataBaseConnection.getConnection();
        try {
            conn.setAutoCommit(true);

            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setString(1, maLopQuanLy);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        int siSoHienTai = rs.getInt("SiSo");
                        if (siSoHienTai > 0) {
                            System.out.println("-> Chặn hành động: Lớp " + maLopQuanLy + " hiện có sĩ số > 0, không được xóa!");
                            return false;
                        }
                    }
                }
            }

            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setString(1, maLopQuanLy);
                return psDelete.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tra cứu, lọc danh sách lớp học theo Mã lớp hoặc Tên lớp
     */
    public List<LopHoc> search(String keyword) {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM LopHoc WHERE MaLopQuanLy LIKE ? OR TenLop LIKE ?";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LopHoc lop = new LopHoc();
                    lop.setMaLopQuanLy(rs.getString("MaLopQuanLy"));
                    lop.setTenLop(rs.getString("TenLop"));
                    lop.setSiSo(rs.getInt("SiSo"));
                    lop.setMaGV(rs.getString("MaGV"));
                    list.add(lop);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi thực hiện tìm kiếm lớp học với từ khóa: " + keyword);
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 🌟 NGHIỆP VỤ CORE: Tính toán lại sĩ số dựa trên tổng số sinh viên thực tế đang thuộc lớp.
     * Thích hợp gọi khi thêm, sửa hoặc chuyển lớp sinh viên.
     *
     * 🌟 ĐÃ SỬA: ép setAutoCommit(true) ngay khi vào hàm để đảm bảo UPDATE này
     * luôn được commit độc lập, không bị rollback ngầm bởi transaction
     * (autoCommit=false) mà nơi gọi trước đó (ví dụ SinhVienDAO.insert()) đã thiết lập
     * trên connection singleton dùng chung.
     */
    public boolean recalculateSiSo(String maLop) {
        if (maLop == null || maLop.trim().isEmpty()) return false;

        String sql = "UPDATE LopHoc "
                   + "SET SiSo = (SELECT COUNT(*) FROM SinhVien WHERE MaLopQuanLy = ?) "
                   + "WHERE MaLopQuanLy = ?";

        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true); // 🌟 QUAN TRỌNG: ép commit ngay lập tức

            ps.setString(1, maLop.trim());
            ps.setString(2, maLop.trim());
            int soDongAnhHuong = ps.executeUpdate();

            System.out.println("-> [recalculateSiSo] Lớp " + maLop.trim() + " đã cập nhật sĩ số, số dòng ảnh hưởng = " + soDongAnhHuong);
            return soDongAnhHuong > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi cập nhật lại sĩ số thực tế cho lớp: " + maLop);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 🌟 NGHIỆP VỤ ĐỒNG BỘ HÀNG LOẠT: Tính toán lại sĩ số cho TẤT CẢ các lớp hành chính
     * cùng lúc, dựa trên số lượng sinh viên thực tế trong bảng SinhVien.
     * Dùng để vá lại dữ liệu cũ bị lệch do nhập tay/import trực tiếp vào DB.
     */
    public boolean dongBoLaiSiSoTatCaLop() {
        String sql = "UPDATE LopHoc "
                   + "SET SiSo = (SELECT COUNT(*) FROM SinhVien WHERE SinhVien.MaLopQuanLy = LopHoc.MaLopQuanLy)";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            int soDongAnhHuong = ps.executeUpdate();
            System.out.println("-> Hệ thống: Đã đồng bộ lại sĩ số cho " + soDongAnhHuong + " lớp học hành chính.");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi đồng bộ lại sĩ số toàn bộ các lớp học.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HÀM PHỤC VỤ TRANSACTION: Tự động giảm sĩ số lớp khi xóa sinh viên.
     * Giữ nguyên cơ chế nhận Connection từ ngoài truyền vào (dùng trong transaction
     * xóa sinh viên ở QuanLyHoSoController) — không tự ý setAutoCommit ở đây
     * vì nơi gọi đang tự quản lý transaction riêng.
     */
    public boolean giamSiSoLop(Connection conn, String maLop) throws SQLException {
        String sql = "UPDATE LopHoc SET SiSo = SiSo - 1 WHERE MaLopQuanLy = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLop);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * HÀM DÙNG CHUNG: Tăng hoặc giảm sĩ số lớp linh hoạt theo số lượng
     */
    public boolean updateSiSo(String maLop, int soLuong) {
        String sql = "UPDATE LopHoc SET SiSo = SiSo + ? WHERE MaLopQuanLy = ?";
        Connection conn = DataBaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);

            ps.setInt(1, soLuong);
            ps.setString(2, maLop);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hàm phụ trợ: Lấy danh sách toàn bộ các lớp học hành chính
     */
    public List<LopHoc> getAllLopHoc() {
        List<LopHoc> list = new ArrayList<>();
        String sql = "SELECT * FROM LopHoc";
        Connection conn = DataBaseConnection.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                LopHoc lop = new LopHoc();
                lop.setMaLopQuanLy(rs.getString("MaLopQuanLy"));
                lop.setTenLop(rs.getString("TenLop"));
                lop.setSiSo(rs.getInt("SiSo"));
                lop.setMaGV(rs.getString("MaGV"));
                list.add(lop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}