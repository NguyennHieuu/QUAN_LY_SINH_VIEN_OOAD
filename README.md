# 🏫 DỰ ÁN BÀI TẬP LỚN MÔN OOAD - HỆ THỐNG QUẢN LÝ SINH VIÊN (NHÓM 10)

Hệ thống quản lý đào tạo, đăng ký tín chỉ, nhập điểm và tính học phí theo kiến trúc **MVC (Model - View - Controller)** kết nối cơ sở dữ liệu **SQL Server**.

Dự án sử dụng cấu trúc độc lập IDE quản lý bằng **Maven**, cho phép các thành viên sử dụng linh hoạt **NetBeans, VS Code hoặc Eclipse** mà không gây xung đột cấu trúc.

---

## 🛠️ CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)

Mã nguồn được tổ chức phân lớp rõ ràng theo đúng thiết kế hướng đối tượng của hệ thống:

```text
📁 QUAN_LY_SINH_VIEN_OOAD (Thư mục gốc Repository)
├── 📄 .gitignore
│   └── Rào chắn ngăn chặn đẩy file rác của IDE lên GitHub
│
├── 📄 README.md
│   └── Tài liệu hướng dẫn và mô tả dự án
│
├── 📁 DataBase
│   └── 📄 ScriptSystem.sql
│       └── Kịch bản tạo bảng cấu trúc hệ thống và dữ liệu mẫu
│
└── 📁 Java
    ├── 📄 pom.xml
    │   └── Quản lý thư viện Maven
    │       (mssql-jdbc-12.2.0.jre11)
    │
    └── 📁 src/main/java/com/nhom10/ooad/quanlysinhvien
        │
        ├── 📄 QuanLySinhVien.java
        │   └── File khởi chạy chính của ứng dụng
        │
        ├── 📁 DataBase
        │   └── 📄 DataBaseConnection.java
        │       └── Mở/đóng kết nối SQL Server
        │
        ├── 📁 Model
        │   └── Tầng dữ liệu (các lớp thực thể)
        │
        ├── 📁 DAO
        │   └── Tầng truy vấn dữ liệu (Data Access Object)
        │
        ├── 📁 Controller
        │   └── Tầng xử lý logic nghiệp vụ
        │
        └── 📁 View
            └── Tầng giao diện người dùng (GUI)
```

---

## ⚠️ LƯU Ý QUAN TRỌNG VỀ CÁC PACKAGE MVC

Hiện tại, một số file lớp (Class) trống được tạo sẵn bên trong các thư mục:

- `Model`
- `DAO`
- `Controller`
- `View`

Mục đích của các file này chỉ là để Git nhận diện và giữ lại cấu trúc khung bộ package (do Git mặc định sẽ xóa hoặc bỏ qua các thư mục trống).

Khi bắt tay vào làm việc:

- Thành viên được phân công phần nào sẽ tự tạo file Class cụ thể của phần đó.
- File Class cần được đặt đúng package tương ứng.
- Không tự ý thay đổi cấu trúc MVC chung của dự án nếu chưa thống nhất với nhóm.
