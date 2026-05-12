# CPAI - Competitive Programming AI Assistant

**CPAI** là một ứng dụng Java Swing hiện đại dành cho lập trình viên thi đấu (Competitive Programmers). Ứng dụng tích hợp sức mạnh của **Google Gemini Vision AI** để tự động đọc đề bài từ hình ảnh, sinh code tạo bộ test (Testcase Generator), sinh đáp án chuẩn (Solution) và tự động chấm điểm bài làm (Judge System).

## 🌟 Các tính năng nổi bật
- **Quản lý bài toán (CRUD):** Thêm, sửa, xóa các bài toán. Dữ liệu được lưu trữ trong SQL Server.
- **Tích hợp Gemini Vision AI:** Đọc đề bài trực tiếp từ ảnh chụp màn hình, tự động sửa lỗi chính tả và format lại đề bài.
- **AI Auto Code Generation:** AI tự động sinh ra 3 loại mã nguồn C++:
  - `Generator Code`: Mã sinh dữ liệu test ngẫu nhiên (dùng `mt19937`).
  - `Checker Code`: Mã chấm điểm tự động (Custom Checker).
  - `Solution Code`: Mã nguồn giải bài toán chuẩn (Accepted).
- **Trình quản lý Testcase:** Tự động biên dịch `g++`, sinh ra hàng loạt file `.in` và `.out`.
- **Hệ thống Judge Tự động:** Cho phép dán code của bạn vào để chấm. Hỗ trợ so khớp chuỗi (String Matching) hoặc sử dụng Custom Checker. Hiển thị kết quả trực quan: `AC`, `WA`, `TLE`, `CE` cùng thời gian chạy tính bằng milliseconds.

---

## 🛠 Yêu cầu hệ thống (Prerequisites)
Để chạy được ứng dụng này, máy tính của bạn cần cài đặt các phần mềm sau:

1. **Java Development Kit (JDK):** Phiên bản 17 trở lên.
2. **Maven:** Trình quản lý dự án Java.
3. **Microsoft SQL Server:** Dùng để chạy database lưu trữ bài toán.
4. **MinGW (C++ Compiler):** Máy tính **bắt buộc** phải cài đặt `g++` và đã thêm vào đường dẫn biến môi trường (Environment Variables `PATH`). Để kiểm tra, mở CMD và gõ `g++ --version`.

---

## 🚀 Hướng dẫn cài đặt và Chạy dự án

### Bước 1: Khởi tạo Database (SQL Server)
Mở **SQL Server Management Studio (SSMS)** và chạy lần lượt file `CPManager.sql` và `UpdateDB.sql` để tạo cơ sở dữ liệu `CPManager`.

*(Nếu bạn đã chạy `CPManager.sql` và `UpdateDB.sql` trước đó thì có thể bỏ qua bước này).*

### Bước 2: Cấu hình Kết nối Cơ sở dữ liệu
Nếu SQL Server của bạn có đặt mật khẩu tài khoản `sa`, hãy mở file `src/main/java/cpai/services/DatabaseHelper.java` và cấu hình lại chuỗi kết nối (`DB_URL`, `USER`, `PASS`). Nếu bạn dùng Windows Authentication mặc định, mã nguồn hiện tại đã được cấu hình sẵn.

### Bước 3: Tải Dependencies và Biên dịch
Mở terminal/CMD tại thư mục gốc của dự án (`d:\Java\cpai`) và chạy lệnh Maven:
```bash
mvn clean install
```
*(Lệnh này sẽ tải về các thư viện như `OkHttp`, `org.json`, `mssql-jdbc` và `FlatLaf` giao diện).*

### Bước 4: Chạy Ứng dụng
Bạn có thể chạy dự án bằng một trong các cách sau:
- **Cách 1 (Từ IDE như VS Code, IntelliJ, Eclipse):** Mở file `src/main/java/cpai/Main.java` và bấm nút **Run**.
- **Cách 2 (Sử dụng Maven Command):**
```bash
mvn exec:java -Dexec.mainClass="cpai.Main"
```

---

## 💡 Hướng dẫn sử dụng CPAI

### 1. Tab "Quản lý Đề"
- Bấm **"Tạo Problem Mới"** ở cột bên trái để tạo một không gian làm việc mới.
- Bấm nút **"Tải ảnh & AI Phân Tích Đề"**, chọn 1 bức ảnh chụp đề bài. AI sẽ mất khoảng vài giây để dịch ảnh thành văn bản format Markdown.
- Bấm **"Phân tích AI"**. Hệ thống sẽ tự gửi đề bài cho Google Gemini. Đợi 10-30 giây, AI sẽ nhả về 3 đoạn code C++: Generator, Checker và Solution.

### 2. Tab "Quản lý Testcase"
- Giao diện sẽ tự động lấy mã `Generator Code` do AI sinh ra. Bạn có thể tự chỉnh sửa nếu muốn.
- Nhập số lượng testcase cần sinh (ví dụ: `10`), bấm **"Sinh Testcases (.in)"**. Hệ thống sẽ gọi `g++` biên dịch và đẻ ra các file `1.in`, `2.in`...

### 3. Tab "Test Lab"
- Giao diện sẽ tự động điền mã `Solution Code` (Code chuẩn) do AI sinh ra.
- Bấm **"Chạy Code AC & Sinh Outputs"**. Hệ thống sẽ chạy qua 10 file `.in` ở bước trước và đẻ ra 10 file `.out` tương ứng.

### 4. Tab "Judge"
- Hệ thống hỗ trợ 2 chế độ chấm:
  - **Chấm bằng Checker:** Nếu đề bài có nhiều đáp án, hệ thống sẽ bốc `Checker Code` vào ô trên.
  - **Chấm bằng chuỗi (String Matching):** Nếu ô Checker bị bỏ trống, hệ thống sẽ đối chiếu text trực tiếp.
- Dán mã nguồn bài làm của bạn vào ô "User Code".
- Bấm **"Chấm Bài"** để xem hệ thống chạy tự động và báo lỗi bằng bảng màu xanh/đỏ chuẩn quốc tế cực xịn!

---

*Lưu ý: Bạn có thể click chuột phải vào bài toán ở cột danh sách bên trái để đổi tên hoặc xóa toàn bộ thư mục dữ liệu của bài đó ra khỏi máy tính.*
