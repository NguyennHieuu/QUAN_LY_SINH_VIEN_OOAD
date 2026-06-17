/* ======================================================================
   HỆ THỐNG QUẢN LÝ HỌC VỤ VÀ HỌC PHÍ SINH VIÊN
   Script tạo cơ sở dữ liệu trên Microsoft SQL Server - Nhóm 10
   Phù hợp cho kiến trúc MVC xử lý Logic hoàn toàn tại ứng dụng (NetBeans)
   ====================================================================== */

USE master;
GO

-- Kiểm tra và xóa Database cũ nếu tồn tại để làm sạch môi trường
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QUAN_LY_SINH_VIEN')
BEGIN
    ALTER DATABASE QUAN_LY_SINH_VIEN SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QUAN_LY_SINH_VIEN;
END
GO

-- Tạo mới database theo định dạng tên chuẩn không dấu
CREATE DATABASE QUAN_LY_SINH_VIEN;
GO

USE QUAN_LY_SINH_VIEN;
GO

/* ======================================================================
   1. NHÓM BẢNG DANH MỤC (Master Data)
   ====================================================================== */

-- Bảng TÀI KHOẢN: Phục vụ xác thực và phân quyền hệ thống [cite: 139, 349]
CREATE TABLE TaiKhoan (
    TenDangNhap         VARCHAR(50)     NOT NULL,
    MatKhau             VARCHAR(255)    NOT NULL,           -- Chuỗi mật khẩu đã mã hóa (Hash)
    VaiTro              NVARCHAR(20)    NOT NULL,           -- Sinh viên / Giảng viên / Giáo vụ / Tài chính [cite: 139]
    TrangThaiHoatDong   BIT             NOT NULL DEFAULT 1, -- 1: Hoạt động, 0: Khóa [cite: 139]
    CONSTRAINT PK_TaiKhoan PRIMARY KEY (TenDangNhap),
    CONSTRAINT CK_TaiKhoan_VaiTro CHECK (VaiTro IN (N'Sinh viên', N'Giảng viên', N'Giáo vụ', N'Tài chính'))
);
GO

-- Bảng GIẢNG VIÊN: Lưu trữ hồ sơ giảng viên [cite: 139, 349]
CREATE TABLE GiangVien (
    MaGV            VARCHAR(20)     NOT NULL,
    HoTenGV         NVARCHAR(100)   NOT NULL,
    SDT             VARCHAR(15)     NULL,
    Email           VARCHAR(100)    NULL,
    DonViCongTac    NVARCHAR(100)   NULL,
    TenDangNhap     VARCHAR(50)     NOT NULL,
    CONSTRAINT PK_GiangVien PRIMARY KEY (MaGV),
    CONSTRAINT FK_GiangVien_TaiKhoan FOREIGN KEY (TenDangNhap)
        REFERENCES TaiKhoan(TenDangNhap)
        ON UPDATE NO ACTION ON DELETE NO ACTION, -- Logic xóa/đồng bộ do Java DAO điều khiển
    CONSTRAINT UQ_GiangVien_TenDangNhap UNIQUE (TenDangNhap),
    CONSTRAINT UQ_GiangVien_Email UNIQUE (Email)
);
GO

-- Bảng LỚP HỌC: Danh sách các lớp hành chính [cite: 141, 349]
CREATE TABLE LopHoc (
    MaLopQuanLy     VARCHAR(20)     NOT NULL,
    TenLop          NVARCHAR(100)   NOT NULL,
    SiSo            INT             NOT NULL DEFAULT 0,
    MaGV            VARCHAR(20)     NULL,                   -- Giáo viên chủ nhiệm lớp [cite: 141]
    CONSTRAINT PK_LopHoc PRIMARY KEY (MaLopQuanLy),
    CONSTRAINT FK_LopHoc_GiangVien FOREIGN KEY (MaGV)
        REFERENCES GiangVien(MaGV)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_LopHoc_SiSo CHECK (SiSo >= 0)
);
GO

-- Bảng SINH VIÊN: Lưu trữ thông tin định danh người học [cite: 139, 349]
CREATE TABLE SinhVien (
    MSSV            VARCHAR(20)     NOT NULL,
    HoTen           NVARCHAR(100)   NOT NULL,
    NgaySinh        DATE            NULL,
    GioiTinh        NVARCHAR(10)    NULL,                   -- Nam / Nữ / Khác [cite: 139]
    MaLopQuanLy     VARCHAR(20)     NOT NULL,
    TenDangNhap     VARCHAR(50)     NOT NULL,
    CONSTRAINT PK_SinhVien PRIMARY KEY (MSSV),
    CONSTRAINT FK_SinhVien_LopHoc FOREIGN KEY (MaLopQuanLy)
        REFERENCES LopHoc(MaLopQuanLy)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_SinhVien_TaiKhoan FOREIGN KEY (TenDangNhap)
        REFERENCES TaiKhoan(TenDangNhap)
        ON UPDATE NO ACTION ON DELETE NO ACTION, 
    CONSTRAINT UQ_SinhVien_TenDangNhap UNIQUE (TenDangNhap),
    CONSTRAINT CK_SinhVien_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác'))
);
GO

-- Bảng CHƯƠNG TRÌNH ĐÀO TẠO [cite: 141, 349]
CREATE TABLE CTDT (
    MaCTDT      VARCHAR(20)     NOT NULL,
    TenNganh    NVARCHAR(100)   NOT NULL,
    NienKhoa    VARCHAR(20)     NULL,
    TongSoTC    INT             NOT NULL DEFAULT 0,
    CONSTRAINT PK_CTDT PRIMARY KEY (MaCTDT),
    CONSTRAINT CK_CTDT_TongSoTC CHECK (TongSoTC >= 0)
);
GO

-- Bảng HỌC PHẦN: Danh mục môn học tĩnh ban hành [cite: 141, 349]
CREATE TABLE HocPhan (
    MaHP        VARCHAR(20)     NOT NULL,
    TenHP       NVARCHAR(150)   NOT NULL,
    SoTC        INT             NOT NULL,
    LoaiHP      NVARCHAR(20)    NOT NULL,                   -- Bắt buộc / Tự chọn [cite: 93]
    MaCTDT      VARCHAR(20)     NOT NULL,
    CONSTRAINT PK_HocPhan PRIMARY KEY (MaHP),
    CONSTRAINT FK_HocPhan_CTDT FOREIGN KEY (MaCTDT)
        REFERENCES CTDT(MaCTDT)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_HocPhan_SoTC CHECK (SoTC > 0),
    CONSTRAINT CK_HocPhan_LoaiHP CHECK (LoaiHP IN (N'Bắt buộc', N'Tự chọn'))
);
GO

-- Bảng HỌC KỲ: Bổ sung để lưu thông tin học kỳ chuẩn hóa [cite: 349]
CREATE TABLE HocKy (
    MaHocKy     VARCHAR(20)     NOT NULL,
    TenHocKy    NVARCHAR(50)    NOT NULL,
    NamHoc      VARCHAR(20)     NULL,
    CONSTRAINT PK_HocKy PRIMARY KEY (MaHocKy)
);
GO

-- Bảng ĐIỀU KIỆN HỌC PHẦN: Quản lý môn tiên quyết (Mối quan hệ đệ quy) [cite: 141]
CREATE TABLE DieuKienHocPhan (
    MaHP            VARCHAR(20)     NOT NULL,   -- Mã học phần chính
    MaHPDieuKien    VARCHAR(20)     NOT NULL,   -- Mã học phần điều kiện
    LoaiDieuKien    NVARCHAR(30)    NULL,       -- Tiên quyết / Học trước
    CONSTRAINT PK_DieuKienHocPhan PRIMARY KEY (MaHP, MaHPDieuKien),
    CONSTRAINT FK_DKHP_HocPhanChinh FOREIGN KEY (MaHP)
        REFERENCES HocPhan(MaHP)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_DKHP_HocPhanDieuKien FOREIGN KEY (MaHPDieuKien)
        REFERENCES HocPhan(MaHP)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_DKHP_KhacNhau CHECK (MaHP <> MaHPDieuKien)
);
GO

/* ======================================================================
   2. NHÓM BẢNG GIAO DỊCH / NGHIỆP VỤ
   ====================================================================== */

-- Bảng ĐỢT ĐĂNG KÝ: Cấu hình thời gian đóng/mở cổng đăng ký [cite: 143, 349]
CREATE TABLE DotDangKy (
    MaDotDangKy     VARCHAR(20)     NOT NULL,
    MaHocKy         VARCHAR(20)     NOT NULL,
    ThoiGianMo      DATETIME        NOT NULL,
    ThoiGianDong    DATETIME        NOT NULL,
    CONSTRAINT PK_DotDangKy PRIMARY KEY (MaDotDangKy),
    CONSTRAINT FK_DotDangKy_HocKy FOREIGN KEY (MaHocKy)
        REFERENCES HocKy(MaHocKy)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_DotDangKy_ThoiGian CHECK (ThoiGianDong > ThoiGianMo)
);
GO

-- Bảng LỚP HỌC PHẦN: Lớp mở theo từng kỳ thực tế [cite: 143, 349]
CREATE TABLE LopHocPhan (
    MaLopHP         VARCHAR(20)     NOT NULL,
    MaHP            VARCHAR(20)     NOT NULL,
    MaHocKy         VARCHAR(20)     NOT NULL,
    MaGV            VARCHAR(20)     NULL,                   -- Giảng viên phụ trách [cite: 143]
    TrongSoQT       DECIMAL(4,2)    NOT NULL DEFAULT 0.3,   -- Trọng số điểm quá trình [cite: 143]
    TrongSoCK       DECIMAL(4,2)    NOT NULL DEFAULT 0.7,   -- Trọng số điểm cuối kỳ [cite: 143]
    SiSoToiDa       INT             NOT NULL DEFAULT 0,
    SiSoHienTai     INT             NOT NULL DEFAULT 0,
    CONSTRAINT PK_LopHocPhan PRIMARY KEY (MaLopHP),
    CONSTRAINT FK_LopHocPhan_HocPhan FOREIGN KEY (MaHP)
        REFERENCES HocPhan(MaHP)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_LopHocPhan_HocKy FOREIGN KEY (MaHocKy)
        REFERENCES HocKy(MaHocKy)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_LopHocPhan_GiangVien FOREIGN KEY (MaGV)
        REFERENCES GiangVien(MaGV)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_LopHocPhan_TrongSo CHECK (TrongSoQT + TrongSoCK = 1),
    CONSTRAINT CK_LopHocPhan_SiSo CHECK (SiSoHienTai >= 0 AND SiSoHienTai <= SiSoToiDa)
);
GO

-- Bảng HÓA ĐƠN HỌC PHÍ: Quản lý công nợ sinh viên theo kỳ [cite: 145, 349]
CREATE TABLE HoaDonHocPhi (
    MaHoaDon                VARCHAR(20)     NOT NULL,
    MSSV                    VARCHAR(20)     NOT NULL,
    MaHocKy                 VARCHAR(20)     NOT NULL,
    DonGiaTinChi            DECIMAL(18,2)   NOT NULL,
    TongTienNop             DECIMAL(18,2)   NOT NULL DEFAULT 0,
    TrangThaiThanhToan      NVARCHAR(20)    NOT NULL DEFAULT N'Chưa nộp', -- Chưa nộp / Đã nộp [cite: 145]
    NgayLapHoaDon           DATE            NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    CONSTRAINT PK_HoaDonHocPhi PRIMARY KEY (MaHoaDon),
    CONSTRAINT FK_HoaDon_SinhVien FOREIGN KEY (MSSV)
        REFERENCES SinhVien(MSSV)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_HoaDon_HocKy FOREIGN KEY (MaHocKy)
        REFERENCES HocKy(MaHocKy)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_HoaDon_TrangThai CHECK (TrangThaiThanhToan IN (N'Chưa nộp', N'Đã nộp')),
    CONSTRAINT CK_HoaDon_SoTien CHECK (TongTienNop >= 0 AND DonGiaTinChi >= 0),
    CONSTRAINT UQ_HoaDon_SinhVien_HocKy UNIQUE (MSSV, MaHocKy)
);
GO

/* ======================================================================
   3. NHÓM BẢNG CHI TIẾT VÀ LIÊN KẾT (N-N)
   ====================================================================== */

-- Bảng ĐĂNG KÝ HỌC: Chi tiết danh sách ghi danh của sinh viên [cite: 143, 349]
CREATE TABLE DangKyHoc (
    MaDangKy        VARCHAR(20)     NOT NULL,
    MSSV            VARCHAR(20)     NOT NULL,
    MaLopHP         VARCHAR(20)     NOT NULL,
    ThoiGianDKi     DATETIME        NOT NULL DEFAULT GETDATE(),
    TrangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Thành công', -- Thành công / Đã hủy [cite: 143]
    CONSTRAINT PK_DangKyHoc PRIMARY KEY (MaDangKy),
    CONSTRAINT FK_DangKyHoc_SinhVien FOREIGN KEY (MSSV)
        REFERENCES SinhVien(MSSV)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_DangKyHoc_LopHocPhan FOREIGN KEY (MaLopHP)
        REFERENCES LopHocPhan(MaLopHP)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_DangKyHoc_TrangThai CHECK (TrangThai IN (N'Thành công', N'Đã hủy')),
    CONSTRAINT UQ_DangKyHoc_SinhVien_Lop UNIQUE (MSSV, MaLopHP)
);
GO

-- Bảng BẢNG ĐIỂM: Lưu trữ kết quả học tập [cite: 143, 349]
CREATE TABLE BangDiem (
    MaBangDiem      VARCHAR(20)     NOT NULL,
    MSSV            VARCHAR(20)     NOT NULL,
    MaLopHP         VARCHAR(20)     NOT NULL,
    DiemQT          DECIMAL(4,2)    NULL,
    DiemCK          DECIMAL(4,2)    NULL,
    DiemTongKet     DECIMAL(4,2)    NULL,
    TrangThai       NVARCHAR(20)    NOT NULL DEFAULT N'Nháp',  -- Nháp / Chính thức [cite: 143]
    MaGV            VARCHAR(20)     NULL,                      -- Giảng viên nhập điểm [cite: 143]
    CONSTRAINT PK_BangDiem PRIMARY KEY (MaBangDiem),
    CONSTRAINT FK_BangDiem_SinhVien FOREIGN KEY (MSSV)
        REFERENCES SinhVien(MSSV)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_BangDiem_LopHocPhan FOREIGN KEY (MaLopHP)
        REFERENCES LopHocPhan(MaLopHP)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT FK_BangDiem_GiangVien FOREIGN KEY (MaGV)
        REFERENCES GiangVien(MaGV)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_BangDiem_Diem CHECK (
        (DiemQT IS NULL OR (DiemQT BETWEEN 0 AND 10)) AND
        (DiemCK IS NULL OR (DiemCK BETWEEN 0 AND 10)) AND
        (DiemTongKet IS NULL OR (DiemTongKet BETWEEN 0 AND 10))
    ),
    CONSTRAINT CK_BangDiem_TrangThai CHECK (TrangThai IN (N'Nháp', N'Chính thức')),
    CONSTRAINT UQ_BangDiem_SinhVien_Lop UNIQUE (MSSV, MaLopHP)
);
GO

-- Bảng LỊCH SỬ THANH TOÁN: Chi tiết dòng tiền nộp của hóa đơn học phí [cite: 145, 349]
CREATE TABLE LichSuThanhToan (
    MaGiaoDich          VARCHAR(20)     NOT NULL,
    MaHoaDon            VARCHAR(20)     NOT NULL,
    NgayGioThanhToan    DATETIME        NOT NULL DEFAULT GETDATE(),
    SoTienNop           DECIMAL(18,2)   NOT NULL,
    HinhThuc            NVARCHAR(30)    NOT NULL,           -- Tiền mặt / Chuyển khoản [cite: 145]
    CONSTRAINT PK_LichSuThanhToan PRIMARY KEY (MaGiaoDich),
    CONSTRAINT FK_LichSuThanhToan_HoaDon FOREIGN KEY (MaHoaDon)
        REFERENCES HoaDonHocPhi(MaHoaDon)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT CK_LichSuThanhToan_SoTien CHECK (SoTienNop > 0)
);
GO

/* ======================================================================
   4. CHỈ MỤC (INDEX) HỖ TRỢ TRUY VẤN TỐC ĐỘ CAO CHO LỚP JAVA DAO [cite: 277]
   ====================================================================== */
CREATE INDEX IX_SinhVien_MaLopQuanLy        ON SinhVien(MaLopQuanLy);
CREATE INDEX IX_HocPhan_MaCTDT              ON HocPhan(MaCTDT);
CREATE INDEX IX_LopHocPhan_MaHP             ON LopHocPhan(MaHP);
CREATE INDEX IX_LopHocPhan_MaHocKy          ON LopHocPhan(MaHocKy);
CREATE INDEX IX_LopHocPhan_MaGV             ON LopHocPhan(MaGV);
CREATE INDEX IX_DangKyHoc_MSSV              ON DangKyHoc(MSSV);
CREATE INDEX IX_DangKyHoc_MaLopHP           ON DangKyHoc(MaLopHP);
CREATE INDEX IX_BangDiem_MSSV               ON BangDiem(MSSV);
CREATE INDEX IX_BangDiem_MaLopHP            ON BangDiem(MaLopHP);
CREATE INDEX IX_HoaDonHocPhi_MSSV           ON HoaDonHocPhi(MSSV);
CREATE INDEX IX_HoaDonHocPhi_MaHocKy        ON HoaDonHocPhi(MaHocKy);
CREATE INDEX IX_LichSuThanhToan_MaHoaDon    ON LichSuThanhToan(MaHoaDon);
GO

PRINT N'Cơ sở dữ liệu QUAN_LY_SINH_VIEN (Bản cấu hình thô phục vụ NetBeans MVC DAO) đã tạo sẵn sàng!';
GO


-- DATABASE MẪU
USE QUAN_LY_SINH_VIEN;
GO

/* ======================================================================
   1. CHÈN DỮ LIỆU BẢNG TÀI KHOẢN (Tạo tài khoản trước cho tất cả các bên)
   ====================================================================== */
INSERT INTO TaiKhoan (TenDangNhap, MatKhau, VaiTro, TrangThaiHoatDong) VALUES
('giaovu01', '123456', N'Giáo vụ', 1),
('ketoan01', '123456', N'Tài chính', 1),
('gv.thoa', '123456', N'Giảng viên', 1),
('20234009', '123456', N'Sinh viên', 1),
('20233991', '123456', N'Sinh viên', 1),
('20234048', '123456', N'Sinh viên', 1),
('20234047', '123456', N'Sinh viên', 1);
GO

/* ======================================================================
   2. CHÈN DỮ LIỆU BẢNG GIẢNG VIÊN 
   ====================================================================== */
INSERT INTO GiangVien (MaGV, HoTenGV, SDT, Email, DonViCongTac, TenDangNhap) VALUES
('GV001', N'Nguyễn Thị Kim Thoa', '0912345678', 'thoa.nguyenthikim@hust.edu.vn', N'Trường Điện - Điện tử', 'gv.thoa');
GO

/* ======================================================================
   3. CHÈN DỮ LIỆU BẢNG LỚP HỌC (Lớp hành chính)
   ====================================================================== */
INSERT INTO LopHoc (MaLopQuanLy, TenLop, SiSo, MaGV) VALUES
('IoT-01', N'Thông minh và IoT 01', 4, 'GV001');
GO

/* ======================================================================
   4. CHÈN DỮ LIỆU BẢNG SINH VIÊN (Thành viên nhóm 10)
   ====================================================================== */
INSERT INTO SinhVien (MSSV, HoTen, NgaySinh, GioiTinh, MaLopQuanLy, TenDangNhap) VALUES
('20234009', N'Nguyễn Trung Hiếu', '2005-04-12', N'Nam', 'IoT-01', '20234009'),
('20233991', N'Dương Ngọc Hoài Anh', '2005-08-23', N'Nữ', 'IoT-01', '20233991'),
('20234048', N'Hoàng Quốc Việt', '2005-11-02', N'Nam', 'IoT-01', '20234048'),
('20234047', N'Hoàng Đình Việt', '2005-01-15', N'Nam', 'IoT-01', '20234047');
GO

/* ======================================================================
   5. CHÈN DỮ LIỆU BẢNG CHƯƠNG TRÌNH ĐÀO TẠO & HỌC PHẦN
   ====================================================================== */
INSERT INTO CTDT (MaCTDT, TenNganh, NienKhoa, TongSoTC) VALUES
('CTDT-IoT', N'Hệ thống nhúng thông minh và IoT', '2023-2027', 135);

INSERT INTO HocPhan (MaHP, TenHP, SoTC, LoaiHP, MaCTDT) VALUES
('IT3011', N'Phân tích thiết kế hướng đối tượng', 3, N'Bắt buộc', 'CTDT-IoT'),
('EE3110', N'Cơ sở hệ thống nhúng', 3, N'Bắt buộc', 'CTDT-IoT'),
('IT2000', N'Tin học đại cương', 2, N'Bắt buộc', 'CTDT-IoT');

-- Thêm điều kiện môn tiên quyết (Tin học đại cương là môn học trước của OOAD)
INSERT INTO DieuKienHocPhan (MaHP, MaHPDieuKien, LoaiDieuKien) VALUES
('IT3011', 'IT2000', N'Học trước');
GO

/* ======================================================================
   6. CHÈN DỮ LIỆU NGHIỆP VỤ ĐÀO TẠO (Học kỳ, Đợt đăng ký, Lớp học phần)
   ====================================================================== */
INSERT INTO HocKy (MaHocKy, TenHocKy, NamHoc) VALUES
('20252', N'Học kỳ 2025.2', '2025-2026');

INSERT INTO DotDangKy (MaDotDangKy, MaHocKy, ThoiGianMo, ThoiGianDong) VALUES
('DK-20252', '20252', '2026-01-01 08:00:00', '2026-01-15 17:00:00');

-- Mở 2 lớp học phần cho kỳ này
INSERT INTO LopHocPhan (MaLopHP, MaHP, MaHocKy, MaGV, TrongSoQT, TrongSoCK, SiSoToiDa, SiSoHienTai) VALUES
('LHP-IT3011-01', 'IT3011', '20252', 'GV001', 0.30, 0.70, 40, 4),
('LHP-EE3110-01', 'EE3110', '20252', 'GV001', 0.40, 0.60, 40, 4);
GO

/* ======================================================================
   7. CHÈN DỮ LIỆU ĐĂNG KÝ HỌC & BẢNG ĐIỂM (Cả nhóm đăng ký học)
   ====================================================================== */
-- Đăng ký lớp OOAD
INSERT INTO DangKyHoc (MaDangKy, MSSV, MaLopHP, ThoiGianDKi, TrangThai) VALUES
('DK01', '20234009', 'LHP-IT3011-01', '2026-01-02 09:15:00', N'Thành công'),
('DK02', '20233991', 'LHP-IT3011-01', '2026-01-02 10:20:00', N'Thành công'),
('DK03', '20234048', 'LHP-IT3011-01', '2026-01-03 14:05:00', N'Thành công'),
('DK04', '20234047', 'LHP-IT3011-01', '2026-01-03 15:30:00', N'Thành công');

-- Khởi tạo luôn khung bảng điểm ở trạng thái "Nháp" (Giảng viên đang chấm, chưa chốt)
INSERT INTO BangDiem (MaBangDiem, MSSV, MaLopHP, DiemQT, DiemCK, DiemTongKet, TrangThai, MaGV) VALUES
('BD01', '20234009', 'LHP-IT3011-01', 9.0, 8.5, 8.65, N'Nháp', 'GV001'),
('BD02', '20233991', 'LHP-IT3011-01', 9.5, 9.0, 9.15, N'Nháp', 'GV001'),
('BD03', '20234048', 'LHP-IT3011-01', 8.5, 8.0, 8.15, N'Nháp', 'GV001'),
('BD04', '20234047', 'LHP-IT3011-01', 9.0, 7.5, 7.95, N'Nháp', 'GV001');
GO

/* ======================================================================
   8. CHÈN DỮ LIỆU TÀI CHÍNH (Hóa đơn và Lịch sử nộp học phí)
   ====================================================================== */
-- Giả sử mỗi bạn đăng ký 3 tín chỉ môn OOAD, đơn giá 500k/tín -> Tổng 1tr500k
INSERT INTO HoaDonHocPhi (MaHoaDon, MSSV, MaHocKy, DonGiaTinChi, TongTienNop, TrangThaiThanhToan, NgayLapHoaDon) VALUES
('HD01', '20234009', '20252', 500000, 1500000, N'Đã nộp', '2026-02-15'),
('HD02', '20233991', '20252', 500000, 1500000, N'Đã nộp', '2026-02-15'),
('HD03', '20234048', '20252', 500000, 0, N'Chưa nộp', '2026-02-15'),
('HD04', '20234047', '20252', 500000, 0, N'Chưa nộp', '2026-02-15');

-- Ghi nhận lịch sử giao dịch cho các bạn đã nộp tiền (Hiếu và Anh)
INSERT INTO LichSuThanhToan (MaGiaoDich, MaHoaDon, NgayGioThanhToan, SoTienNop, HinhThuc) VALUES
('GD001', 'HD01', '2026-02-20 10:00:00', 1500000, N'Chuyển khoản'),
('GD002', 'HD02', '2026-02-21 14:30:00', 1500000, N'Chuyển khoản');
GO

PRINT N'Đã chèn toàn bộ dữ liệu mẫu bản chuẩn cho hệ thống thành công!';
GO