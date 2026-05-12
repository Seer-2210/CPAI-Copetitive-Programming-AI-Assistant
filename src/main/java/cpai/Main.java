package cpai;

import com.formdev.flatlaf.FlatLightLaf;
import cpai.views.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Cài đặt giao diện FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // Khởi chạy GUI trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
