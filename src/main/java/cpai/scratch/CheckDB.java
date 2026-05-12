package cpai.scratch;

import cpai.services.DatabaseHelper;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckDB {
    public static void main(String[] args) {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Title, CheckerCode FROM Problems WHERE Title LIKE N'%nguyên tố%'")) {
            while (rs.next()) {
                System.out.println("Title: " + rs.getString("Title"));
                System.out.println("CheckerCode:\n" + rs.getString("CheckerCode"));
                System.out.println("===============================");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
