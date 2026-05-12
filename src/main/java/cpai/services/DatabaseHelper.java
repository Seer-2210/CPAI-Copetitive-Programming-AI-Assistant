package cpai.services;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    
    // Thay đổi thông tin phù hợp với máy của bạn
    private static final String SERVER_NAME = "localhost";
    private static final String DB_NAME = "CPManager";
    private static final String PORT = "1433"; 

    /**
     * Kết nối bằng SQL Server Account (Khuyên dùng vì ổn định hơn trên VS Code)
     */
    public static Connection getConnection() throws SQLException {
        String user = "sa";
        String pass = "fromseerwithlove";
        
        String url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=true;", 
                                    SERVER_NAME, PORT, DB_NAME, user, pass);
        
        return DriverManager.getConnection(url);
    }

    /**
     * Kết nối bằng Windows Authentication 
     * Lưu ý: Cần file sqljdbc_auth.dll trong thư viện hệ thống
     */
    public static Connection getWindowsConnection() throws SQLException {
        String url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;integratedSecurity=true;encrypt=true;trustServerCertificate=true;", 
                                    SERVER_NAME, PORT, DB_NAME);
        
        return DriverManager.getConnection(url);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Kết nối SQL Server thành công!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}