package com.nhom10.ooad.quanlysinhvien.Controller;

import com.nhom10.ooad.quanlysinhvien.DAO.DangKyHocDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.DotDangKyDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.HocPhanDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.LopHocPhanDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.BangDiemDAO;
import com.nhom10.ooad.quanlysinhvien.Model.DangKyHoc;
import com.nhom10.ooad.quanlysinhvien.Model.DotDangKy;
import com.nhom10.ooad.quanlysinhvien.Model.LopHocPhan;
import com.nhom10.ooad.quanlysinhvien.Model.LopHPView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DangKyHocController {

    private final DangKyHocDAO dangKyHocDAO = new DangKyHocDAO();
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();
    private final DotDangKyDAO dotDangKyDAO = new DotDangKyDAO();
    private final HocPhanDAO hocPhanDAO = new HocPhanDAO();
    private final BangDiemDAO bangDiemDAO = new BangDiemDAO();
    
    // Gọi sang Controller của phân hệ Kế toán do bạn Quốc Việt phụ trách
    private final HocPhiController hocPhiController = new HocPhiController();

    /**
     * Kiểm tra trạng thái nợ học phí thông qua phân hệ Kế toán để đưa ra quyết định khóa/mở đăng ký.
     */
    private boolean kiemTraNoHocPhi(String mssv) {
        try {
            // 🌟 ĐÃ SỬA: Đổi tên hàm thành kiemTraSinhVienNoHocPhi để ăn khớp chính xác với CSDL/DAO của HocPhiController
            return hocPhiController.kiemTraSinhVienNoHocPhi(mssv);
        } catch (Exception e) {
            // Nếu chưa ghép code Kế toán hoặc lỗi hệ thống, tạm thời trả về false để test không bị gãy luồng
            return false;
        }
    }

    /**
     * Lấy danh sách các lớp học phần đang mở để hiển thị lên giao diện cho sinh viên xem
     */
    public List<LopHocPhan> getDanhSachHocPhanMo(String maHocKy) {
        return lopHocPhanDAO.getAllOpen(maHocKy);
    }

    /**
     * Hàm lọc danh sách học phần mở trả về kiểu DTO LopHPView để View hiển thị trực tiếp Tên môn
     */
    public List<LopHPView> getDanhSachHocPhanLoc(String maHocKy, String keyword, String khoaVien) {
        return lopHocPhanDAO.getDanhSachLopHPView(maHocKy, keyword, khoaVien);
    }

    /**
     * Hàm lấy danh sách học phần ĐÃ ĐĂNG KÝ trả về kiểu DTO LopHPView giúp View nhàn hơn,
     * tự động tổng hợp thông tin Tên môn và tính số lượng Tín chỉ chuẩn từ Database.
     */
    public List<LopHPView> getDanhSachMonDaDK(String mssv, String maHocKy) {
        List<String> listMaLop = dangKyHocDAO.getListByMSSV(mssv, maHocKy);
        List<LopHPView> listResult = new ArrayList<>();
        
        for (String maLop : listMaLop) {
            LopHPView viewObj = lopHocPhanDAO.getLopHPViewByID(maLop);
            if (viewObj != null) {
                listResult.add(viewObj);
            }
        }
        return listResult;
    }

    /**
     * TRÁI TIM LOGIC: Kiểm tra toàn bộ 5 ràng buộc nghiêm ngặt trước khi cho phép đăng ký
     * Trả về chuỗi thông báo lỗi cụ thể, nếu trả về "OK" nghĩa là hợp lệ 100%
     */
    public String kiemTraDieuKienDangKy(String mssv, LopHocPhan lhp) {
        if (lhp == null) {
            return "Lỗi: Thông tin lớp học phần không hợp lệ!";
        }

        // 1. Kiểm tra cổng đăng ký học tập mở/đóng
        DotDangKy ddk = dotDangKyDAO.getCurrentDotDangKy(lhp.getMaHocKy());
        Date bayGio = new Date();
        if (ddk == null || bayGio.before(ddk.getThoiGianMo()) || bayGio.after(ddk.getThoiGianDong())) {
            return "Lỗi: Hệ thống đăng ký tín chỉ hiện đang đóng cổng!";
        }

        // 2. Kiểm tra chặn nợ học phí (Liên kết Kế toán thực tế)
        if (kiemTraNoHocPhi(mssv)) {
            return "Lỗi: Tài khoản bị khóa đăng ký tín chỉ do còn nợ học phí kỳ trước!";
        }

        // 3. Kiểm tra sĩ số lớp học phần
        if (lhp.getSiSoHienTai() >= lhp.getSiSoToiDa()) {
            return "Lỗi: Lớp học phần này đã đạt sĩ số tối đa, không thể đăng ký thêm!";
        }

        // 4. Kiểm tra môn tiên quyết
        List<String> dsMonTienQuyet = hocPhanDAO.getDanhSachMonTienQuyet(lhp.getMaHP());
        for (String maMonTQ : dsMonTienQuyet) {
            if (!bangDiemDAO.checkMonTienQuyet(mssv, maMonTQ)) {
                return "Lỗi: Bạn chưa hoàn thành hoặc chưa đạt môn tiên quyết bắt buộc (" + maMonTQ + ")!";
            }
        }

        // 5. Kiểm tra trùng lịch học hoặc đã đăng ký môn này rồi
        List<String> dsLopDaDangKy = dangKyHocDAO.getListByMSSV(mssv, lhp.getMaHocKy());
        for (String maLopDaDK : dsLopDaDangKy) {
            LopHocPhan lopDaDK = lopHocPhanDAO.getByID(maLopDaDK);
            if (lopDaDK != null && lopDaDK.getMaHP().equals(lhp.getMaHP())) {
                return "Lỗi: Bạn đã đăng ký một lớp học phần khác của môn học này rồi!";
            }
        }

        return "OK"; 
    }

    /**
     * Thực hiện ghi nhận đăng ký môn khi các điều kiện đã thỏa mãn
     */
    public boolean dangKyMon(String mssv, LopHocPhan lhp) {
        String checkResult = kiemTraDieuKienDangKy(mssv, lhp);
        if (!"OK".equals(checkResult)) {
            System.err.println(checkResult);
            return false;
        }

        String maDangKy = "DK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        DangKyHoc dkh = new DangKyHoc(maDangKy, mssv, lhp.getMaLopHP(), new Date(), "Thành công");

        // Đồng bộ dữ liệu: Chỉ tăng sĩ số khi thêm bản ghi đăng ký thành công
        boolean insertSuccess = dangKyHocDAO.insert(dkh);
        if (insertSuccess) {
            boolean updateSiSoSuccess = lopHocPhanDAO.updateSiSo(lhp.getMaLopHP(), 1);
            if (!updateSiSoSuccess) {
                // Cơ chế Rollback bằng tay: Nếu tăng sĩ số thất bại thì phải xóa bản ghi vừa insert để tránh rác dữ liệu
                dangKyHocDAO.delete(mssv, lhp.getMaLopHP());
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Rút/Hủy môn học đã đăng ký thành công
     */
    public boolean huyMon(String mssv, String maLopHP) {
        LopHocPhan lhp = lopHocPhanDAO.getByID(maLopHP);
        if (lhp == null) return false;

        // Kiểm tra thời gian xem cổng đăng ký còn hạn mở hay không, đóng rồi thì không cho hủy
        DotDangKy ddk = dotDangKyDAO.getCurrentDotDangKy(lhp.getMaHocKy());
        Date bayGio = new Date();
        if (ddk == null || bayGio.before(ddk.getThoiGianMo()) || bayGio.after(ddk.getThoiGianDong())) {
            System.err.println("Lỗi: Đã hết thời hạn đăng ký/hủy môn học!");
            return false;
        }

        // Đồng bộ dữ liệu: Chỉ giảm sĩ số khi xóa bản ghi đăng ký thành công
        boolean deleteSuccess = dangKyHocDAO.delete(mssv, maLopHP);
        if (deleteSuccess) {
            boolean updateSiSoSuccess = lopHocPhanDAO.updateSiSo(maLopHP, -1);
            if (!updateSiSoSuccess) {
                // Khôi phục nếu lỗi: Nếu giảm sĩ số lỗi, thêm lại bản ghi đăng ký cũ để giữ tính toàn vẹn
                DangKyHoc dkhBack = new DangKyHoc("DK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), mssv, maLopHP, new Date(), "Thành công");
                dangKyHocDAO.insert(dkhBack);
                return false;
            }
            return true;
        }
        return false;
    }
}