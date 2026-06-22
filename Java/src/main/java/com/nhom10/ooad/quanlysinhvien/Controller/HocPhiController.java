package com.nhom10.ooad.quanlysinhvien.Controller;

import java.util.ArrayList; // 🌟 THÊM DÒNG NÀY ĐỂ HẾT LỖI ARRAYLIST
import java.util.List;
import com.nhom10.ooad.quanlysinhvien.DAO.HoaDonHocPhiDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.LichSuThanhToanDAO;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonHocPhi;
import com.nhom10.ooad.quanlysinhvien.Model.LichSuThanhToan;

/**
 * HocPhiController - điều phối nghiệp vụ HỌC PHÍ giữa giao diện (View) và DAO.
 */
public class HocPhiController {

    private final HoaDonHocPhiDAO hoaDonDAO = new HoaDonHocPhiDAO();
    private final LichSuThanhToanDAO lstDAO = new LichSuThanhToanDAO();

    /**
     * UC07.3 - Tính học phí & tạo hóa đơn cho cả học kỳ.
     */
    public int tinhHocPhi(String maHocKy, long donGia) {
        if (maHocKy == null || maHocKy.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mã học kỳ thực tế (ví dụ: 20252).");
        }
        if (donGia <= 0) {
            throw new IllegalArgumentException("Đơn giá định mức của một tín chỉ bắt buộc phải lớn hơn 0 VND.");
        }
        return hoaDonDAO.tinhVaTaoHoaDon(maHocKy.trim(), donGia);
    }

    /** * UC07.2 - Tra cứu / lọc danh sách hóa đơn hiển thị lên JTable.
     */
    public List<HoaDonView> timHoaDon(String keyword, String maHocKy, String trangThai) {
        return hoaDonDAO.timHoaDon(keyword, maHocKy, trangThai);
    }

    /** * Số tiền sinh viên còn phải nộp của một hóa đơn cụ thể.
     */
    public long conNo(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return 0;
        
        HoaDonHocPhi hd = hoaDonDAO.getHoaDonById(maHoaDon.trim());
        if (hd == null) return 0;
        
        long tongPhaiNop = hd.getTongTienNop();
        long daNop = hoaDonDAO.tongDaNop(maHoaDon.trim());
        long duNo = tongPhaiNop - daNop;
        
        return duNo > 0 ? duNo : 0;
    }

    /**
     * UC07.1 - Cập nhật thanh toán: ghi nhận biên lai nộp tiền và tự động cập nhật lại trạng thái hóa đơn.
     */
    public void thanhToan(String maHoaDon, long soTien, String hinhThuc) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi hệ thống: Mã hóa đơn mục tiêu không được để trống!");
        }

        String cleanedMaHD = maHoaDon.trim();
        HoaDonHocPhi hd = hoaDonDAO.getHoaDonById(cleanedMaHD);
        if (hd == null) {
            throw new IllegalArgumentException("Lỗi hệ thống: Không tìm thấy mã hóa đơn " + cleanedMaHD + " trên CSDL!");
        }

        long tongPhaiNop = hd.getTongTienNop();
        long daNopHienTai = hoaDonDAO.tongDaNop(cleanedMaHD);
        long thieuHienTai = tongPhaiNop - daNopHienTai;

        if (soTien <= 0) {
            throw new IllegalArgumentException("Lỗi dữ liệu: Số tiền đóng vào bắt buộc phải lớn hơn 0 VND.");
        }
        if (soTien > thieuHienTai) {
            throw new IllegalArgumentException("Hành động bị chặn: Số tiền nộp vượt quá hạn mức dư nợ còn lại của sinh viên (" + thieuHienTai + " VND).");
        }

        lstDAO.themGiaoDich(cleanedMaHD, soTien, hinhThuc);

        long tongTichLuySauNop = daNopHienTai + soTien;
        String trangThaiMoi = (tongTichLuySauNop >= tongPhaiNop) ? "Đã nộp" : "Chưa nộp";

        hoaDonDAO.capNhatTrangThai(cleanedMaHD, trangThaiMoi);
    }

    /** * Lấy toàn bộ danh sách lịch sử các đợt đóng tiền của một hóa đơn cụ thể (Dùng cho Tab In Biên Lai).
     */
    public List<LichSuThanhToan> getLichSu(String maHoaDon) {
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) return new ArrayList<>(); // 🌟 ĐÃ HẾT LỖI BÁO ĐỎ
        return lstDAO.getLichSuByHoaDon(maHoaDon.trim());
    }

    /**
     * ====== HÀM "CONTRACT" KẾT NỐI LIÊN PHÂN HỆ CHO CẢ NHÓM ======
     * Trả về true nếu sinh viên CÒN NỢ học phí (tồn tại hóa đơn ở trạng thái 'Chưa nộp').
     */
    public boolean kiemTraSinhVienNoHocPhi(String mssv) {
        if (mssv == null || mssv.trim().isEmpty()) return false;
        return hoaDonDAO.coNoHocPhi(mssv.trim());
    }
}