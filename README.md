# CPAI - Competitive Programming AI Assistant

**CPAI** là một ứng dụng Java Swing hiện đại dành cho lập trình viên thi đấu (Competitive Programmers). Đây là một đồ án cuối kỳ, tích hợp sức mạnh của **Google Gemini Vision AI** để tự động đọc đề bài từ hình ảnh, sinh mã nguồn C++ và sở hữu một **hệ thống Judge đa luồng** (Multithreaded Judge System) mô phỏng lại các nền tảng như Codeforces.

## 🌟 Các tính năng nổi bật
- **Quản lý bài toán (CRUD):** Thêm, sửa, xóa các bài toán. Dữ liệu được lưu trữ trong SQL Server.
- **Tích hợp Gemini Vision AI:** Đọc đề bài trực tiếp từ ảnh chụp màn hình, tự động sửa lỗi chính tả và format lại đề bài.
- **AI Auto Code Generation:** AI tự động sinh ra 3 loại mã nguồn C++:
  - `Generator Code`: Mã sinh dữ liệu test ngẫu nhiên (dùng `mt19937`).
  - `Checker Code`: Mã chấm điểm tự động (Custom Checker).
  - `Solution Code`: Mã nguồn giải bài toán chuẩn (Accepted).
- **Hệ thống Judge Đa luồng (Parallel Judging):** Chấm song song hàng chục testcases cùng lúc sử dụng `ExecutorService`, cho tốc độ chấm cực kỳ nhanh chóng.
- **Giám sát Tiến trình (Watchdog):** Tự động phát hiện và ngắt tiến trình nếu quá thời gian (**TLE** - 2s) hoặc in ra file quá lớn (**OLE** - 64MB). Phát hiện lỗi biên dịch (**CE**), Lỗi runtime (**RTE**) và kết quả sai (**WA**).

---

## 📂 Tổ chức dữ liệu
Toàn bộ mã nguồn do AI sinh ra (`.cpp`), các file thực thi (`.exe`), và các bộ test (file `.in` và `.out`) **không lưu trong cơ sở dữ liệu** để tránh nặng máy. Thay vào đó, chúng được lưu trữ và tổ chức khoa học trực tiếp trên thư mục ổ cứng tại:

```text
/cp_workspace/problems/{id_của_bài_toán}/
```

Ví dụ, bài toán ID `1` sẽ có các file nằm trong `/cp_workspace/problems/1/` và bộ testcase lưu trong thư mục con `testcases/`.

---

## 🛠 Yêu cầu hệ thống (Prerequisites)
Để chạy được ứng dụng này, máy tính của bạn cần:
1. **Java Development Kit (JDK):** Phiên bản 17 trở lên.
2. **Maven:** Trình quản lý dự án Java.
3. **Microsoft SQL Server:** Dùng để chạy database lưu trữ.
4. **MinGW (C++ Compiler):** Máy tính **bắt buộc** phải cài đặt `g++` và đã thêm vào biến môi trường (Environment Variables `PATH`).

---

## 🚀 Hướng dẫn cài đặt và Chạy dự án

### Bước 1: Khởi tạo Database (SQL Server)
Mở **SQL Server Management Studio (SSMS)** và chạy lần lượt file `CPManager.sql` và `UpdateDB.sql` để tạo cơ sở dữ liệu `CPManager`.

### Bước 2: Cấu hình Kết nối Cơ sở dữ liệu
Nếu SQL Server của bạn có đặt mật khẩu tài khoản `sa`, hãy mở file `src/main/java/cpai/services/DatabaseHelper.java` và sửa lại chuỗi kết nối (`DB_URL`, `USER`, `PASS`). Nếu bạn dùng Windows Authentication mặc định, mã nguồn đã được cấu hình sẵn.

### ⚠️ Bước 3: Cấu hình API Key (QUAN TRỌNG)
Vì lý do bảo mật, API Key của Google Gemini không được tải lên mã nguồn công khai (GitHub). **Bạn bắt buộc phải nhập API Key của riêng mình trước khi chạy.**
1. Truy cập [Google AI Studio](https://aistudio.google.com/app/apikey) để lấy API Key miễn phí.
2. Mở file `src/main/java/cpai/services/AIService.java`.
3. Dán key vào biến `API_KEY` ở dòng 14:
   ```java
   private static final String API_KEY = "ĐIỀN_KEY_CỦA_BẠN_VÀO_ĐÂY";
   ```

### Bước 4: Tải Dependencies và Biên dịch
Mở terminal/CMD tại thư mục gốc của dự án và chạy:
```bash
mvn clean install
```
*(Lệnh này sẽ tải về các thư viện như `OkHttp`, `org.json`, `mssql-jdbc` và `FlatLaf` giao diện).*

### Bước 5: Chạy Ứng dụng
- **Cách 1:** Mở file `src/main/java/cpai/Main.java` trong IDE (VS Code, IntelliJ) và bấm nút **Run**.
- **Cách 2:** Sử dụng lệnh Maven:
  ```bash
  mvn exec:java -Dexec.mainClass="cpai.Main"
  ```

---

## 💡 Hướng dẫn sử dụng CPAI

### 1. Tab "Quản lý Đề"
- Bấm **"Tạo Problem Mới"** để tạo một bài toán.
- Bấm **"Tải ảnh & AI Phân Tích Đề"** để tải ảnh chụp màn hình đề bài.
- Bấm **"Phân tích AI"**. AI sẽ mất khoảng 10-30s để gửi dữ liệu cho Google Gemini và sinh ra 3 đoạn code C++ (Generator, Checker, Solution).

### 2. Tab "Quản lý Testcase"
- Giao diện tự động lấy `Generator Code`. Bạn có thể sửa thủ công nếu cần.
- Nhập số lượng testcase (ví dụ: `10`), bấm **"Sinh Testcases (.in)"**. Hệ thống sẽ gọi `g++` biên dịch và sinh ra các file `1.in`, `2.in`... lưu vào thư mục `cp_workspace`.

### 3. Tab "Test Lab"
- Hệ thống lấy `Solution Code` chuẩn do AI sinh ra.
- Bấm **"Chạy Code AC & Sinh Outputs"**. Hệ thống chạy qua các file `.in` và đẻ ra các file `.out` tương ứng (Đáp án chuẩn).

### 4. Tab "Judge"
- Hệ thống hỗ trợ 2 chế độ chấm:
  - **Chấm bằng Checker:** Nếu bài có nhiều đáp án hợp lệ, thuật toán `Checker Code` sẽ được gọi.
  - **Chấm bằng Text Matching:** Đối chiếu đáp án trực tiếp.
- Dán code của bạn vào "User Code" và bấm **"Chấm Bài"**. Hệ thống đa luồng sẽ chạy song song các testcase, tự động chặn OLE/TLE và xuất kết quả theo bảng màu chuẩn (AC, WA, TLE, OLE, CE).
