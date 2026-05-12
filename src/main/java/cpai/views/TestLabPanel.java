package cpai.views;

import cpai.models.ProblemModel;
import cpai.services.TestcaseService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class TestLabPanel extends JPanel {

    private JTextArea acCodeArea;
    private JLabel lblProblemId;
    private JTextField numTestcasesField;
    private JButton generateOutButton;
    private JTextArea logArea;

    private ProblemModel currentProblem;

    public TestLabPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel: AC Code Input
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("AC Solution Code (C++) - Dùng để sinh .out");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        acCodeArea = new JTextArea(15, 50);
        acCodeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(acCodeArea);
        topPanel.add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        lblProblemId = new JLabel("Problem ID: Chưa chọn");
        lblProblemId.setForeground(Color.BLUE);
        controlPanel.add(lblProblemId);

        controlPanel.add(new JLabel("Số lượng Testcase hiện có:"));
        numTestcasesField = new JTextField("10", 5);
        controlPanel.add(numTestcasesField);

        generateOutButton = new JButton("Biên dịch AC & Sinh Output");
        controlPanel.add(generateOutButton);

        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // Bottom Panel: Logs
        logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Console Log"));

        add(topPanel, BorderLayout.NORTH);
        add(logScroll, BorderLayout.CENTER);

        // Events
        generateOutButton.addActionListener(e -> generateOutputs());
    }

    public void setProblem(ProblemModel p) {
        this.currentProblem = p;
        lblProblemId.setText("Problem ID: " + p.getId());
        
        if (p.getSolutionCode() != null) {
            acCodeArea.setText(p.getSolutionCode());
        } else {
            acCodeArea.setText("");
        }

        // Auto count existing .in files
        File dir = new File("cp_workspace/problems/" + p.getId() + "/testcases");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".in"));
            if (files != null) {
                numTestcasesField.setText(String.valueOf(files.length));
            }
        } else {
            numTestcasesField.setText("0");
        }
    }

    private void generateOutputs() {
        if (currentProblem == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài toán ở cột trái!");
            return;
        }
        String code = acCodeArea.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng dán code AC!");
            return;
        }

        try {
            int numTc = Integer.parseInt(numTestcasesField.getText().trim());
            int probId = currentProblem.getId();

            generateOutButton.setEnabled(false);
            logArea.setText("Đang biên dịch và chạy code AC...\n");

            SwingWorker<Void, String> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    publish("Bắt đầu gọi TestcaseService...");
                    TestcaseService.generateOutputsWithAC(probId, code, numTc);
                    publish("Hoàn thành quá trình sinh output.");
                    return null;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    for (String msg : chunks) {
                        logArea.append(msg + "\n");
                    }
                }

                @Override
                protected void done() {
                    generateOutButton.setEnabled(true);
                    logArea.append("Done!\n");
                    
                    File dir = new File("cp_workspace/problems/" + probId + "/testcases");
                    if (dir.exists() && dir.isDirectory()) {
                        File[] files = dir.listFiles((d, name) -> name.endsWith(".out"));
                        if (files != null) {
                            logArea.append("Số file .out hiện có: " + files.length + "\n");
                        }
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
