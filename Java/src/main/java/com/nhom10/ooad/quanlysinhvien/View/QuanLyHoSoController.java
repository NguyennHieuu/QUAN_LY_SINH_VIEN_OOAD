package com.nhom10.ooad.quanlysinhvien.View;

import com.nhom10.ooad.quanlysinhvien.Model.SinhVien;
import com.nhom10.ooad.quanlysinhvien.Model.LopHoc;
import java.util.List;

/**
 * Thẻ CRC ID: 12 - Điều phối nghiệp vụ Quản lý hồ sơ
 */
public class QuanLyHoSoController {
    
    // Logic: Ghi nhận sinh viên mới (UC01.1) [7]
    public boolean themSinhVien(SinhVien sv) {
        // TODO: Gọi SinhVienDAO để kiểm tra trùng MSSV và Insert vào DB
        return false; 
    }

    // Logic: Cập nhật thông tin sinh viên (UC01.3) [8]
    public boolean capNhatSinhVien(SinhVien sv) {
        // TODO: Kiểm tra tính hợp lệ và gọi DAO cập nhật
        return false;
    }

    // Logic: Xóa hồ sơ và cập nhật giảm sĩ số lớp quản lý (UC01.4) [9]
    public boolean xoaSinhVien(String mssv) {
        // TODO: Thực hiện xóa và gọi logic cập nhật sĩ số lớp liên quan
        return false;
    }

    // Logic: Thêm mới lớp học hành chính (UC02.1) [10]
    public boolean themLopHoc(LopHoc lop) {
        // TODO: Kiểm tra không để trống, mã lớp không được trùng
        return false;
    }

    // Logic: Xóa lớp học - Chỉ xóa nếu sĩ số = 0 (UC02.3) [11]
    public String xoaLopHoc(String maLop) {
        // Kiểm tra sĩ số trước khi xóa theo đúng báo cáo
        return "Chưa kiểm tra sĩ số";
    }

    // Logic: Tìm kiếm sinh viên theo MSSV hoặc Họ tên (UC01.2) [12]
    public List<SinhVien> timKiemSinhVien(String tuKhoa) {
        // TODO: Gọi DAO truy xuất dữ liệu
        return null;
    }
}