package com.nhom10.ooad.quanlysinhvien.Controller;

import com.nhom10.ooad.quanlysinhvien.DAO.SinhVienDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.GiangVienDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.TaiKhoanDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.LopHocDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.LopHocPhanDAO; // 🌟 THÊM: Import DAO Lớp học phần
import com.nhom10.ooad.quanlysinhvien.DataBase.DataBaseConnection;
import com.nhom10.ooad.quanlysinhvien.Model.SinhVien;
import com.nhom10.ooad.quanlysinhvien.Model.GiangVien;
import com.nhom10.ooad.quanlysinhvien.Model.TaiKhoan;
import com.nhom10.ooad.quanlysinhvien.Model.LopHoc; 
import com.nhom10.ooad.quanlysinhvien.Model.LopHocPhan; // 🌟 THÊM: Import Model Lớp học phần
import com.nhom10.ooad.quanlysinhvien.Model.LopHPView;   // 🌟 THÊM: Import DTO LopHPView
import java.sql.*;
import java.util.List;

public class QuanLyHoSoController {
    private final SinhVienDAO svDAO = new SinhVienDAO();
    private final GiangVienDAO gvDAO = new GiangVienDAO();
    private final TaiKhoanDAO tkDAO = new TaiKhoanDAO(); 
    private final LopHocDAO lopHocDAO = new LopHocDAO(); 
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO(); // 🌟 THÊM: Đối tượng thực thi dữ liệu lớp HP

    // ====================================================================
    // PHÂN HỆ QUẢN LÝ LỚP HỌC PHẦN (🌟 KHU VỰC TÍCH HỢP MỚI CHO GIAO DIỆN)
    // ====================================================================

    /**
     * Nghiệp vụ lấy toàn bộ danh sách lớp học phần hiện hành dưới dạng View gộp
     * Phục vụ nút "Cập nhật bảng" đổ lên JTable chính
     */
    public List<LopHPView> getAllLopHocPhan() {
        return lopHocPhanDAO.getAllLopHPView();
    }

    /**
     * Nghiệp vụ Mở lớp học phần mới vĩnh viễn xuống SQL Server
     */
    public boolean themLopHocPhan(LopHocPhan lhp) {
        return lopHocPhanDAO.insertLopHocPhan(lhp);
    }

    /**
     * Nghiệp vụ Hủy / Xóa một lớp học phần khỏi Database
     */
    public boolean xoaLopHocPhan(String maLopHP) {
        return lopHocPhanDAO.deleteLopHocPhan(maLopHP);
    }

    /**
     * 🌟 THÊM MỚI: Ủy quyền gọi xuống DAO để kiểm tra trùng thời gian biểu đứng lớp của Giảng viên
     * @return true nếu BỊ TRÙNG LỊCH, false nếu lịch TRỐNG (Hợp lệ)
     */
    public boolean checkTrungLichGV(String maGV, String maHocKy, String lichHoc) {
        return lopHocPhanDAO.kiemTraTrungLichGiangVien(maGV, maHocKy, lichHoc);
    }


    // ====================================================================
    // PHÂN HỆ QUẢN LÝ HỒ SƠ SINH VIÊN (STUDENT MANAGEMENT)
    // ====================================================================

    public boolean themSinhVien(SinhVien sv) {
        return svDAO.insert(sv);
    }

    public boolean suaSinhVien(SinhVien sv) {
        return svDAO.update(sv);
    }

    public boolean xoaSinhVien(String mssv, String maLop) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); 

            System.out.println("-> [Transaction] Đang dọn sạch ràng buộc khóa ngoại cho SV: " + mssv);

            String sql1 = "DELETE FROM BangDiem WHERE MSSV = ?";
            ps = conn.prepareStatement(sql1);
            ps.setString(1, mssv);
            ps.executeUpdate();
            ps.close();

            String sql2 = "DELETE FROM DangKyHoc WHERE MSSV = ?";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, mssv);
            ps.executeUpdate();
            ps.close();

            String sql3 = "DELETE FROM LichSuThanhToan WHERE MaHoaDon IN (SELECT MaHoaDon FROM HoaDonHocPhi WHERE MSSV = ?)";
            ps = conn.prepareStatement(sql3);
            ps.setString(1, mssv);
            ps.executeUpdate();
            ps.close();

            String sql4 = "DELETE FROM HoaDonHocPhi WHERE MSSV = ?";
            ps = conn.prepareStatement(sql4);
            ps.setString(1, mssv);
            ps.executeUpdate();
            ps.close();

            boolean isDeletedSV = svDAO.delete(conn, mssv); 

            if (isDeletedSV) {
                String sql6 = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?";
                ps = conn.prepareStatement(sql6);
                ps.setString(1, mssv);
                ps.executeUpdate();
                ps.close();

                boolean isUpdatedSiSo = lopHocDAO.giamSiSoLop(conn, maLop);
                
                if (isUpdatedSiSo) {
                    conn.commit(); 
                    System.out.println("-> Hệ thống: Đã xóa sạch dữ liệu liên quan đến sinh viên " + mssv + " thành công.");
                    return true;
                }
            }
            
            if (conn != null) conn.rollback();
            
        } catch (SQLException e) {
            try { 
                if (conn != null) {
                    conn.rollback(); 
                    System.err.println("-> Hệ thống: Phát hiện lỗi ràng buộc SQL, đã khôi phục (Rollback) dữ liệu Sinh viên!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }

    // ====================================================================
    // PHÂN HỆ QUẢN LÝ HỒ SƠ GIẢNG VIÊN
    // ====================================================================

    public boolean themGiangVien(GiangVien gv) {
        return gvDAO.insert(gv);
    }

    public boolean suaGiangVien(GiangVien gv) {
        return gvDAO.update(gv);
    }

    public boolean xoaGiangVien(String maGv) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DataBaseConnection.getConnection();
            conn.setAutoCommit(false); 

            System.out.println("-> [Transaction] Đang gỡ bỏ ràng buộc phân công lớp cho GV: " + maGv);

            String sql1 = "UPDATE LopHoc SET MaGV = NULL WHERE MaGV = ?";
            ps = conn.prepareStatement(sql1);
            ps.setString(1, maGv);
            ps.executeUpdate();
            ps.close();

            String sql2 = "UPDATE LopHocPhan SET MaGV = NULL WHERE MaGV = ?";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, maGv);
            ps.executeUpdate();
            ps.close();
            
            String sql3 = "UPDATE BangDiem SET MaGV = NULL WHERE MaGV = ?";
            ps = conn.prepareStatement(sql3);
            ps.setString(1, maGv);
            ps.executeUpdate();
            ps.close();

            boolean isDeletedGV = gvDAO.delete(conn, maGv);

            if (isDeletedGV) {
                String sql5 = "DELETE FROM TaiKhoan WHERE TenDangNhap = ?";
                ps = conn.prepareStatement(sql5);
                ps.setString(1, maGv);
                ps.executeUpdate();
                ps.close();

                conn.commit(); 
                System.out.println("-> Hệ thống: Đã xóa hoàn toàn cán bộ " + maGv + " và tài khoản đi kèm vĩnh viễn.");
                return true;
            }

            if (conn != null) conn.rollback();

        } catch (SQLException e) {
            try { 
                if (conn != null) {
                    conn.rollback(); 
                    System.err.println("-> Hệ thống: Phát hiện lỗi SQL, đã khôi phục (Rollback) trạng thái dữ liệu Giảng viên!");
                }
            } catch (SQLException ex) { 
                ex.printStackTrace(); 
            }
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }

    // ====================================================================
    // PHÂN HỆ QUẢN LÝ TÀI KHOẢN VÀ BẢO MẬT
    // ====================================================================

    public List<TaiKhoan> timKiemTaiKhoan(String keyword) {
        return tkDAO.search(keyword);
    }

    // ====================================================================
    // PHÂN HỆ QUẢN LÝ LỚP HỌC HÀNH CHÍNH (KẾT NỐI REAL-TIME THÀNH CÔNG)
    // ====================================================================

    public List<LopHoc> timKiemLopHoc(String keyword) {
        return lopHocDAO.search(keyword);
    }

    public boolean themLopHoc(LopHoc lh) {
        return lopHocDAO.insert(lh); 
    }

    public boolean suaLopHoc(LopHoc lh) {
        return lopHocDAO.update(lh);
    }

    public String xoaLopHoc(String maLop) {
        boolean ketQuaXoa = lopHocDAO.delete(maLop);
        if (ketQuaXoa) {
            return "OK";
        } else {
            return "Không thể xóa lớp học này! Vui lòng kiểm tra lại sĩ số lớp hoặc liên kết dữ liệu.";
        }
    }
}