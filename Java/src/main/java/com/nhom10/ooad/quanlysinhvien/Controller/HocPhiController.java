package com.nhom10.ooad.quanlysinhvien.Controller;

import java.util.List;

import com.nhom10.ooad.quanlysinhvien.DAO.HoaDonHocPhiDAO;
import com.nhom10.ooad.quanlysinhvien.DAO.LichSuThanhToanDAO;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonView;
import com.nhom10.ooad.quanlysinhvien.Model.HoaDonHocPhi;
import com.nhom10.ooad.quanlysinhvien.Model.LichSuThanhToan;

/**
 * HocPhiController - dieu phoi nghiep vu HOC PHI giua giao dien (View) va DAO.
 *
 * Phu trach cac use case:
 *   UC07.3 Tinh hoc phi      -> tinhHocPhi(...)
 *   UC07.2 Tra cuu hoa don   -> timHoaDon(...)
 *   UC07.1 Cap nhat thanh toan -> thanhToan(...)
 *   + Logic CHAN NO          -> kiemTraSinhVienNoHocPhi(...)  (ban 2,3 goi)
 */
public class HocPhiController {

    private final HoaDonHocPhiDAO hoaDonDAO = new HoaDonHocPhiDAO();
    private final LichSuThanhToanDAO lstDAO = new LichSuThanhToanDAO();

    /**
     * UC07.3 - Tinh hoc phi & tao hoa don cho ca hoc ky.
     * @return so hoa don moi tao.
     * @throws IllegalArgumentException neu du lieu nhap khong hop le.
     */
    public int tinhHocPhi(String maHocKy, long donGia) {
        if (maHocKy == null || maHocKy.trim().isEmpty())
            throw new IllegalArgumentException("Vui lòng nhập mã học kỳ (ví dụ HK20251).");
        if (donGia <= 0)
            throw new IllegalArgumentException("Đơn giá tín chỉ phải lớn hơn 0.");
        return hoaDonDAO.tinhVaTaoHoaDon(maHocKy.trim(), donGia);
    }

    /** UC07.2 - Tra cuu / loc danh sach hoa don. */
    public List<HoaDonView> timHoaDon(String keyword, String maHocKy, String trangThai) {
        return hoaDonDAO.timHoaDon(keyword, maHocKy, trangThai);
    }

    /** So tien con phai nop cua 1 hoa don. */
    public long conNo(String maHoaDon) {
        HoaDonHocPhi hd = hoaDonDAO.getHoaDonById(maHoaDon);
        if (hd == null) return 0;
        return hd.getTongTienNop() - hoaDonDAO.tongDaNop(maHoaDon);
    }

    /**
     * UC07.1 - Cap nhat thanh toan: ghi 1 giao dich nop tien va cap nhat trang thai hoa don.
     * Quy tac:
     *   - Hoa don phai ton tai.
     *   - So tien nop > 0 va khong vuot qua so con no.
     *   - Sau khi nop: neu da du -> 'Da nop', con thieu -> 'Con no'.
     * @throws IllegalArgumentException neu vi pham quy tac (View bat de hien thong bao).
     */
    public void thanhToan(String maHoaDon, long soTien, String hinhThuc) {
        HoaDonHocPhi hd = hoaDonDAO.getHoaDonById(maHoaDon);
        if (hd == null)
            throw new IllegalArgumentException("Không tìm thấy hóa đơn: " + maHoaDon);

        long daNop = hoaDonDAO.tongDaNop(maHoaDon);
        long conNo = hd.getTongTienNop() - daNop;

        if (soTien <= 0)
            throw new IllegalArgumentException("Số tiền nộp phải lớn hơn 0.");
        if (soTien > conNo)
            throw new IllegalArgumentException(
                "Số tiền nộp (" + soTien + ") vượt quá số còn nợ (" + conNo + ").");

        lstDAO.themGiaoDich(maHoaDon, soTien, hinhThuc);

        long tongSauNop = daNop + soTien;
        String trangThaiMoi = (tongSauNop >= hd.getTongTienNop()) ? "Đã nộp" : "Còn nợ";
        hoaDonDAO.capNhatTrangThai(maHoaDon, trangThaiMoi);
    }

    /** Lich su nop tien cua 1 hoa don. */
    public List<LichSuThanhToan> getLichSu(String maHoaDon) {
        return lstDAO.getLichSuByHoaDon(maHoaDon);
    }

    /**
     * ====== HAM "CONTRACT" CHO CA NHOM ======
     * Tra ve true neu sinh vien CON NO hoc phi (con hoa don chua nop du).
     *
     * Ban 2 (DangKyHocController) va Ban 3 (DiemSoController) GOI ham nay
     * truoc khi cho dang ky mon / xem diem:
     *
     *     if (new HocPhiController().kiemTraSinhVienNoHocPhi(mssv)) {
     *         // chan: thong bao "Ban dang no hoc phi, vui long nop truoc";
     *     } else {
     *         // cho phep tiep tuc;
     *     }
     */
    public boolean kiemTraSinhVienNoHocPhi(String mssv) {
        return hoaDonDAO.coNoHocPhi(mssv);
    }
}
