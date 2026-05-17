package cpai.views;

import cpai.models.ProblemModel;
import cpai.services.TestcaseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class TestcasePanel extends JPanel {

    private JTextArea generatorCodeArea;
    private JTextField numTestcasesField;
    private JLabel lblProblemId;
    private JButton generateButton;
    private JTable testcaseTable;
    private DefaultTableModel tableModel;
    
    private ProblemModel currentProblem;

    public TestcasePanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel: Generator Code Input
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("Generator Code (C++)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        generatorCodeArea = new JTextArea(15, 50);
        generatorCodeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(generatorCodeArea);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        lblProblemId = new JLabel("Problem ID: Chưa chọn");
        lblProblemId.setForeground(Color.BLUE);
        controlPanel.add(lblProblemId);

        controlPanel.add(new JLabel("Số lượng Testcase:"));
        numTestcasesField = new JTextField("10", 5);
        controlPanel.add(numTestcasesField);

        generateButton = new JButton("Sinh Testcases (Chỉ Input)");
        controlPanel.add(generateButton);

        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // Bottom Panel: Table
        String[] columnNames = {"STT", "File Input", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        testcaseTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(testcaseTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Danh sách Testcase (Input)"));

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        // Events
        generateButton.addActionListener(e -> generateTestcases());
    }

    public void setProblem(ProblemModel p) {
        this.currentProblem = p;
        lblProblemId.setText("Problem ID: " + p.getId());
        if (p.getGeneratorCode() != null) {
            generatorCodeArea.setText(p.getGeneratorCode());
        } else {
            generatorCodeArea.setText("");
        }
        loadTestcaseFiles(p.getId(), 0); // Load existing if any, without enforcing expected number yet
    }

    private void generateTestcases() {
        if (currentProblem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài toán ở cột trái!");
            return;
        }
        String code = generatorCodeArea.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng dán code generator vào đây!");
            return;
        }

        try {
            int numTc = Integer.parseInt(numTestcasesField.getText().trim());
            int probId = currentProblem.getId();

            generateButton.setEnabled(false);
            tableModel.setRowCount(0);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    TestcaseService.generateTestcases(probId, code, numTc);
                    return null;
                }

                @Override
                protected void done() {
                    generateButton.setEnabled(true);
                    loadTestcaseFiles(probId, numTc);
                    JOptionPane.showMessageDialog(TestcasePanel.this, "Đã sinh testcase thành công!");
                }
            };
            worker.execute();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng testcase phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTestcaseFiles(int problemId, int expectedNum) {
        tableModel.setRowCount(0);
        File dir = new File("cp_workspace/problems/" + problemId + "/testcases");
        if (dir.exists() && dir.isDirectory()) {
            // Hiển thị tất cả file .in
            File[] files = dir.listFiles((d, name) -> name.endsWith(".in"));
            if (files != null) {
                int index = 1;
                for (File f : files) {
                    tableModel.addRow(new Object[]{index++, f.getName(), "OK (" + f.length() + " bytes)"});
                }
            }
        }
    }
}
