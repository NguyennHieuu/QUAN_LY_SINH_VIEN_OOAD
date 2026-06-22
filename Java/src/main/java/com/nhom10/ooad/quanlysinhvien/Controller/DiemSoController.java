package com.nhom10.ooad.quanlysinhvien.Controller;

import com.nhom10.ooad.quanlysinhvien.DAO.BangDiemDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.DangKyHocDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.LopHocPhanDAO; // 🌟 THÊM MỚI: Import DAO lớp học phần
import com.nhom10.ooad.quanlysinhvien.Model.BangDiem;
import com.nhom10.ooad.quanlysinhvien.Model.DiemSoView;
import java.util.ArrayList;
import java.util.List;

public class DiemSoController {

    private final BangDiemDAO bangDiemDAO = new BangDiemDAO();
    private final DangKyHocDAO dangKyHocDAO = new DangKyHocDAO();
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO(); // 🌟 THÊM MỚI: Instance DAO phục vụ lọc lớp

    // Giả lập kết nối sang phân hệ Kế toán để chặn xem điểm nếu nợ học phí
    private boolean kiemTraNoHocPhi(String mssv) {
        return false; 
    }

    /**
     * Trả về danh sách bảng điểm chi tiết của sinh viên dạng DTO DiemSoView
     * (Ẩn điểm Nháp bằng cách gán NULL để View hiện ô trống, chặn nợ học phí)
     */
    public List<DiemSoView> xemDiemCaNhan(String mssv, String maHocKy) {
        // 1. Nghiệp vụ chặn xem điểm nếu nợ tiền học phí
        if (kiemTraNoHocPhi(mssv)) {
            System.err.println("Chặn quyền: Sinh viên phải hoàn thành học phí để xem điểm kì này!");
            return null;
        }

        // 2. Kéo thẳng danh sách dữ liệu nâng cao (Đã JOIN sẵn từ CSDL) thông qua DAO
        List<DiemSoView> rawList = bangDiemDAO.getDiemSinhVienNangCao(mssv, maHocKy);
        List<DiemSoView> fillterList = new ArrayList<>();

        // 3. Logic xử lý nghiệp vụ bảo mật thông tin điểm Nháp
        for (DiemSoView dsv : rawList) {
            // Nếu trạng thái là Nháp, làm rỗng các đầu điểm để giao diện JTable hiển thị ô trống
            if (dsv.getTrangThai() != null && dsv.getTrangThai().equals("Nháp")) {
                dsv.setDiemQT(null);
                dsv.setDiemCK(null);
                dsv.setDiemTongKet(null);
            }
            fillterList.add(dsv);
        }
        return fillterList;
    }

    /**
     * Tự động tính toán điểm trung bình tích lũy hệ 10 (GPA) dựa trên DTO dữ liệu sạch
     */
    public double tinhGPA(String mssv, String maHocKy) {
        List<DiemSoView> dsDiem = xemDiemCaNhan(mssv, maHocKy);
        if (dsDiem == null || dsDiem.isEmpty()) {
            return 0.0;
        }

        double tongDiemNhanTinChi = 0.0;
        int tongSoTinChi = 0;

        for (DiemSoView dsv : dsDiem) {
            if (dsv.getDiemTongKet() != null) {
                int soTinChi = dsv.getSoTinChi();
                
                tongDiemNhanTinChi += dsv.getDiemTongKet() * soTinChi;
                tongSoTinChi += soTinChi;
            }
        }

        if (tongSoTinChi == 0) return 0.0;
        
        return Math.round((tongDiemNhanTinChi / tongSoTinChi) * 100.0) / 100.0;
    }

    // ====================================================================
    // 🌟 VÙNG CODE DÀNH CHO GIẢNG VIÊN (TÍCH HỢP LIÊN KẾT HỆ THỐNG MỚI)
    // ====================================================================

    /**
     * 🌟 THÊM MỚI: Hàm trung gian điều phối lấy danh sách các mã lớp học phần theo mã giảng viên
     * Phục vụ View đổ dữ liệu thật từ DB lên JComboBox khi Giảng viên đăng nhập
     */
    public List<String> layDanhSachLopHPGiaoVien(String maGV) {
        if (maGV == null || maGV.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return lopHocPhanDAO.getMaLopHPByGiangVien(maGV.trim());
    }

    /**
     * UC06.3: Xem danh sách lớp học phần giảng viên đang phụ trách dạy.
     * Hàm này kiểm tra tính hợp lệ cơ bản của mã lớp trước khi gọi DAO.
     */
    public List<Object[]> layDanhSachSinhVienLopDay(String maLopHP) {
        if (maLopHP == null || maLopHP.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return dangKyHocDAO.getDanhSachSinhVienByLopHP(maLopHP.trim());
    }

    /**
     * UC06.2: Nghiệp vụ lưu nháp bảng điểm từ danh sách dữ liệu lưới do giảng viên sửa.
     * Hàm này kiểm tra xem điểm số nhập vào có nằm trong khoảng từ 0 đến 10 hay không,
     * tự động tính toán điểm tổng kết dựa trên trọng số thành phần của đồ án.
     */
    public String luuNhapBangDiem(List<BangDiem> danhSachDiem, double trongSoQT, double trongSoCK) {
        if (danhSachDiem == null || danhSachDiem.isEmpty()) {
            return "Không có dữ liệu điểm để lưu!";
        }

        // Vòng lặp Validate tính hợp lệ của toàn bộ điểm số trước khi lưu xuống DB
        for (BangDiem bd : danhSachDiem) {
            if (bd.getDiemQT() < 0.0 || bd.getDiemQT() > 10.0 || bd.getDiemCK() < 0.0 || bd.getDiemCK() > 10.0) {
                return "Lỗi: Điểm quá trình và điểm cuối kỳ bắt buộc phải nằm trong hệ điểm từ 0 đến 10!";
            }
        }

        // Tiến hành cập nhật tính toán và lưu trữ
        boolean tatCaThanhCong = true;
        for (BangDiem bd : danhSachDiem) {
            // Công thức tính toán điểm tổng kết theo mô tả đặc tả phần mềm
            double diemTK = (bd.getDiemQT() * trongSoQT) + (bd.getDiemCK() * trongSoCK);
            // Làm tròn lấy 1 chữ số thập phân
            diemTK = Math.round(diemTK * 10.0) / 10.0;

            // 🌟 ĐÃ SỬA: Truyền thêm mssv và maLopHP để DAO có thể tự INSERT
            // bản ghi mới nếu đây là lần đầu nhập điểm (chưa có dòng BangDiem nào)
            boolean result = bangDiemDAO.luuNhapDiemChiTiet(
                    bd.getMaBangDiem(), bd.getMssv(), bd.getMaLopHP(),
                    bd.getDiemQT(), bd.getDiemCK(), diemTK);
            if (!result) {
                tatCaThanhCong = false;
            }
        }

        if (tatCaThanhCong) {
            return "OK";
        } else {
            return "Hệ thống ghi nhận lỗi: Có bản ghi điểm chưa thể cập nhật tạm thời.";
        }
    }

    /**
     * UC06.4: Nghiệp vụ chốt điểm lớp học phần công bố chính thức.
     * Kiểm tra toàn vẹn dữ liệu xem lớp đã được nhập đủ điểm chưa, nếu còn ô trống thì chặn lại.
     */
    public String chotBangDiemChinhThuc(String maLopHP) {
        if (maLopHP == null || maLopHP.trim().isEmpty()) {
            return "Mã lớp học phần không hợp lệ!";
        }

        // Lấy danh sách điểm hiện tại trong Database lên để kiểm tra tính toàn vẹn
        List<BangDiem> dsHienTai = bangDiemDAO.getBangDiemByLopHP(maLopHP);
        if (dsHienTai.isEmpty()) {
            return "Lớp học phần chưa có dữ liệu bảng điểm để tiến hành chốt khóa!";
        }

        // Kiểm tra xem có sinh viên nào bị sót chưa nhập điểm hay không
        for (BangDiem bd : dsHienTai) {
            if (bd.getTrangThai() == null || bd.getTrangThai().trim().isEmpty()) {
                return "Không thể chốt! Phát hiện có dòng điểm chưa được lưu nháp hoặc đang bỏ trống dữ liệu.";
            }
        }

        // Thỏa mãn toàn bộ điều kiện, tiến hành chốt chính thức thay đổi trạng thái
        boolean success = bangDiemDAO.chotDiemLopHP(maLopHP);
        if (success) {
            return "OK";
        } else {
            // Trường hợp lỗi kết nối CSDL hoặc cập nhật thất bại
            return "Lỗi hệ thống: Giao dịch chốt điểm thất bại, vui lòng thử lại sau!";
        }
    }
}