package cpai.services;

import cpai.models.ProblemModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemDAO {

    public List<ProblemModel> getAllProblems() {
        List<ProblemModel> list = new ArrayList<>();
        String sql = "SELECT * FROM Problems ORDER BY Id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new ProblemModel(
                        rs.getInt("Id"),
                        rs.getString("Title"),
                        rs.getString("Content"),
                        rs.getString("FolderPath"),
                        rs.getTimestamp("CreatedAt"),
                        rs.getString("GeneratorCode"),
                        rs.getString("CheckerCode"),
                        rs.getString("SolutionCode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ProblemModel addProblem(String title, String content) {
        String sql = "INSERT INTO Problems (Title, Content, FolderPath) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, ""); // Tạm thời để trống FolderPath

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        String folderPath = "cp_workspace/problems/" + id;
                        updateFolderPath(id, folderPath);
                        return new ProblemModel(id, title, content, folderPath, new Timestamp(System.currentTimeMillis()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateFolderPath(int id, String folderPath) {
        String sql = "UPDATE Problems SET FolderPath = ? WHERE Id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, folderPath);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateProblem(int id, String title) {
        String sql = "UPDATE Problems SET Title = ? WHERE Id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProblemContent(int id, String content) {
        String sql = "UPDATE Problems SET Content = ? WHERE Id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProblem(int id) {
        String sql = "DELETE FROM Problems WHERE Id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCheckerCode(int id, String checkerCode) {
        String sql = "UPDATE Problems SET CheckerCode = ? WHERE Id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, checkerCode);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveAICodes(int id, String content, String genCode, String checkerCode, String solutionCode) {
        String sql = "UPDATE Problems SET Content = ?, GeneratorCode = ?, CheckerCode = ?, SolutionCode = ? WHERE Id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setString(2, genCode);
            pstmt.setString(3, checkerCode);
            pstmt.setString(4, solutionCode);
            pstmt.setInt(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
