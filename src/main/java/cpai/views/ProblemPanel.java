package cpai.views;

import cpai.models.ProblemModel;
import cpai.services.AIService;
import cpai.services.AIProcessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class ProblemPanel extends JPanel {

    private JLabel lblCurrentProblem;
    private JTextArea problemTextArea;
    private JTextArea aiResultArea;
    private JButton ocrButton;
    private JButton aiAnalyzeButton;
    
    private AIService aiService;
    private ProblemModel currentProblem;

    public ProblemPanel() {
        aiService = new AIService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel: Title and Actions
        JPanel topPanel = new JPanel(new BorderLayout());
        lblCurrentProblem = new JLabel("Chưa chọn bài toán");
        lblCurrentProblem.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCurrentProblem.setForeground(Color.BLUE);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ocrButton = new JButton("Tải ảnh & AI Phân Tích Đề");
        aiAnalyzeButton = new JButton("Phân tích AI");
        actionPanel.add(ocrButton);
        actionPanel.add(aiAnalyzeButton);

        topPanel.add(lblCurrentProblem, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        // Center Panel: Split Pane for Problem Text and AI Result
        problemTextArea = new JTextArea();
        problemTextArea.setLineWrap(true);
        problemTextArea.setWrapStyleWord(true);
        problemTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane problemScroll = new JScrollPane(problemTextArea);
        problemScroll.setBorder(BorderFactory.createTitledBorder("Nội dung đề bài"));

        aiResultArea = new JTextArea();
        aiResultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        aiResultArea.setEditable(false);
        JScrollPane aiScroll = new JScrollPane(aiResultArea);
        aiScroll.setBorder(BorderFactory.createTitledBorder("Kết quả AI (Gen/Checker/Solution)"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, problemScroll, aiScroll);
        splitPane.setResizeWeight(0.4);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Event Listeners
        ocrButton.addActionListener(e -> performOCR());
        aiAnalyzeButton.addActionListener(e -> performAIAnalysis());
    }

    public void setProblem(ProblemModel p) {
        this.currentProblem = p;
        lblCurrentProblem.setText("Bài toán: " + p.getId() + " - " + p.getTitle());
        problemTextArea.setText(p.getContent() != null ? p.getContent() : "");
        
        StringBuilder sb = new StringBuilder();
        if (p.getGeneratorCode() != null && !p.getGeneratorCode().isEmpty()) {
            sb.append("=== GENERATOR CODE ===\n").append(p.getGeneratorCode()).append("\n\n");
        }
        if (p.getCheckerCode() != null && !p.getCheckerCode().isEmpty()) {
            sb.append("=== CHECKER CODE ===\n").append(p.getCheckerCode()).append("\n\n");
        }
        if (p.getSolutionCode() != null && !p.getSolutionCode().isEmpty()) {
            sb.append("=== SOLUTION CODE ===\n").append(p.getSolutionCode());
        }
        aiResultArea.setText(sb.toString());
        if(sb.length() > 0) aiResultArea.setCaretPosition(0);
    }

    private void performOCR() {
        if (currentProblem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài toán ở cột trái trước!");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            problemTextArea.setText("Đang dùng Gemini AI để trích xuất và định dạng lại đề bài, vui lòng đợi...");
            
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    return aiService.extractTextFromImage(selectedFile);
                }
                @Override
                protected void done() {
                    try {
                        String text = get();
                        problemTextArea.setText(text);
                        // Tự động lưu đề bài vào Database nếu có
                        currentProblem.setContent(text);
                        cpai.services.ProblemDAO dao = new cpai.services.ProblemDAO();
                        dao.updateProblemContent(currentProblem.getId(), text);
                    } catch (Exception ex) {
                        problemTextArea.setText("Lỗi kết nối AI: " + ex.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }

    private void performAIAnalysis() {
        if (currentProblem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài toán ở cột trái trước!");
            return;
        }
        String content = problemTextArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung đề bài!");
            return;
        }

        aiResultArea.setText("Đang gọi AI... Có thể mất khoảng 10-30 giây.");
        aiAnalyzeButton.setEnabled(false);

        SwingWorker<AIProcessor.GeneratorResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected AIProcessor.GeneratorResponse doInBackground() throws Exception {
                return aiService.analyzeProblemForGenerator(content);
            }
            @Override
            protected void done() {
                try {
                    AIProcessor.GeneratorResponse res = get();
                    if (res != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("=== GENERATOR CODE ===\n").append(res.generatorCode).append("\n\n");
                        sb.append("=== CHECKER CODE ===\n").append(res.checkerCode).append("\n\n");
                        sb.append("=== SOLUTION CODE ===\n").append(res.solutionCode);
                        aiResultArea.setText(sb.toString());
                        // Có thể scroll về đầu cho dễ nhìn
                        aiResultArea.setCaretPosition(0);

                        // Lưu vào DB
                        cpai.services.ProblemDAO dao = new cpai.services.ProblemDAO();
                        dao.saveAICodes(currentProblem.getId(), content, res.generatorCode, res.checkerCode, res.solutionCode);
                        
                        // Cập nhật model hiện tại
                        currentProblem.setContent(content);
                        currentProblem.setGeneratorCode(res.generatorCode);
                        currentProblem.setCheckerCode(res.checkerCode);
                        currentProblem.setSolutionCode(res.solutionCode);

                    } else {
                        aiResultArea.setText("AI trả về kết quả rỗng hoặc lỗi parse JSON.");
                    }
                } catch (Exception ex) {
                    aiResultArea.setText("Lỗi kết nối AI: " + ex.getMessage());
                } finally {
                    aiAnalyzeButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
