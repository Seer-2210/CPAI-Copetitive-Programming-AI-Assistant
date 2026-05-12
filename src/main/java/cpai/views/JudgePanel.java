package cpai.views;

import cpai.models.ProblemModel;
import cpai.services.TestcaseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JudgePanel extends JPanel {

    private JLabel lblProblemId;
    private JTextArea checkerCodeArea;
    private JTextArea userCodeArea;
    private JButton submitButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    private ProblemModel currentProblem;

    public JudgePanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel: Checker Code & User Code Input
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("Nộp bài (Judge) & Custom Checker");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        checkerCodeArea = new JTextArea(15, 50);
        checkerCodeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane checkerScroll = new JScrollPane(checkerCodeArea);
        checkerScroll.setBorder(BorderFactory.createTitledBorder("Nhập Checker Code (Tùy chọn. Bỏ trống = Khớp chuỗi)"));

        userCodeArea = new JTextArea(15, 50);
        userCodeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane userScroll = new JScrollPane(userCodeArea);
        userScroll.setBorder(BorderFactory.createTitledBorder("Nhập User Code C++ của bạn vào đây:"));

        JSplitPane codeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, checkerScroll, userScroll);
        codeSplitPane.setResizeWeight(0.5);
        topPanel.add(codeSplitPane, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        lblProblemId = new JLabel("Problem ID: Chưa chọn");
        lblProblemId.setForeground(Color.BLUE);
        controlPanel.add(lblProblemId);

        submitButton = new JButton("Chấm Bài (Submit)");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(new Color(40, 167, 69));
        submitButton.setForeground(Color.WHITE);
        controlPanel.add(submitButton);

        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // Bottom Panel: Results Table
        String[] columnNames = {"Testcase", "Status", "Time (ms)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resultTable.setRowHeight(25);
        
        // Custom cell renderer for colors
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 1) {
                    String status = (String) value;
                    if (status.startsWith("AC")) c.setForeground(new Color(40, 167, 69));
                    else if (status.startsWith("WA")) c.setForeground(Color.RED);
                    else if (status.startsWith("TLE")) c.setForeground(Color.ORANGE);
                    else if (status.startsWith("OLE")) c.setForeground(Color.MAGENTA);
                    else c.setForeground(Color.DARK_GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        resultTable.getColumnModel().getColumn(1).setCellRenderer(renderer);

        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Kết quả chấm"));

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Events
        submitButton.addActionListener(e -> judgeUserCode());
    }

    public void setProblem(ProblemModel p) {
        this.currentProblem = p;
        lblProblemId.setText("Problem ID: " + p.getId());
        if (p.getCheckerCode() != null) {
            checkerCodeArea.setText(p.getCheckerCode());
        } else {
            checkerCodeArea.setText("");
        }
        tableModel.setRowCount(0);
    }

    private void judgeUserCode() {
        if (currentProblem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài toán ở cột trái!");
            return;
        }
        String code = userCodeArea.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng dán code cần chấm!");
            return;
        }
        String checkerCode = checkerCodeArea.getText().trim();

        // Cập nhật Checker Code vào Model và Database
        currentProblem.setCheckerCode(checkerCode);
        new cpai.services.ProblemDAO().updateCheckerCode(currentProblem.getId(), checkerCode);

        submitButton.setEnabled(false);
        submitButton.setText("Đang chấm...");
        tableModel.setRowCount(0);

        int probId = currentProblem.getId();

        SwingWorker<List<TestcaseService.JudgeResult>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TestcaseService.JudgeResult> doInBackground() throws Exception {
                return TestcaseService.judgeCode(probId, code, checkerCode);
            }

            @Override
            protected void done() {
                submitButton.setEnabled(true);
                submitButton.setText("Chấm Bài (Submit)");
                try {
                    List<TestcaseService.JudgeResult> results = get();
                    for (TestcaseService.JudgeResult res : results) {
                        tableModel.addRow(new Object[]{
                            "Testcase " + res.testcaseName,
                            res.status,
                            res.timeMs + " ms"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(JudgePanel.this, "Lỗi khi chấm bài: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
