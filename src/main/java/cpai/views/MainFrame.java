package cpai.views;

import cpai.models.ProblemModel;
import cpai.services.ProblemDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private ProblemPanel problemPanel;
    private TestcasePanel testcasePanel;
    private TestLabPanel testLabPanel;
    private JudgePanel judgePanel;

    private JList<ProblemModel> problemList;
    private DefaultListModel<ProblemModel> listModel;
    private ProblemDAO problemDAO;

    public MainFrame() {
        problemDAO = new ProblemDAO();
        setTitle("CPAI - Competitive Programming AI Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        initUI();
        loadProblems();
    }

    private void initUI() {
        // --- Left Panel (Sidebar) ---
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnNewProblem = new JButton("Tạo Problem Mới");
        btnNewProblem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sidebarPanel.add(btnNewProblem, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        problemList = new JList<>(listModel);
        problemList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(problemList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Danh sách bài toán"));
        sidebarPanel.add(listScroll, BorderLayout.CENTER);

        // --- Right Panel (Tabs) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        problemPanel = new ProblemPanel();
        testcasePanel = new TestcasePanel();
        testLabPanel = new TestLabPanel();
        judgePanel = new JudgePanel();

        tabbedPane.addTab("1. Quản lý Đề", problemPanel);
        tabbedPane.addTab("2. Quản lý Testcase", testcasePanel);
        tabbedPane.addTab("3. Test Lab", testLabPanel);
        tabbedPane.addTab("4. Judge", judgePanel);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // --- Main Split Pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, tabbedPane);
        splitPane.setDividerLocation(250); // Sidebar width
        add(splitPane, BorderLayout.CENTER);

        // --- Context Menu for JList (Edit/Delete) ---
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Đổi tên (Edit)");
        JMenuItem deleteItem = new JMenuItem("Xóa (Delete)");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        problemList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    problemList.setSelectedIndex(problemList.locationToIndex(e.getPoint()));
                    if (problemList.getSelectedIndex() != -1) {
                        popupMenu.show(problemList, e.getX(), e.getY());
                    }
                }
            }
        });

        editItem.addActionListener(e -> editProblem());
        deleteItem.addActionListener(e -> deleteProblem());

        // --- Event Listeners ---
        btnNewProblem.addActionListener(e -> createNewProblem());
        
        problemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ProblemModel selected = problemList.getSelectedValue();
                if (selected != null) {
                    problemPanel.setProblem(selected);
                    testcasePanel.setProblem(selected);
                    testLabPanel.setProblem(selected);
                    judgePanel.setProblem(selected);
                }
            }
        });
    }

    private void loadProblems() {
        listModel.clear();
        List<ProblemModel> problems = problemDAO.getAllProblems();
        for (ProblemModel p : problems) {
            listModel.addElement(p);
        }
    }

    private void createNewProblem() {
        String title = JOptionPane.showInputDialog(this, "Nhập tên bài toán mới:", "Tạo Problem", JOptionPane.PLAIN_MESSAGE);
        if (title != null && !title.trim().isEmpty()) {
            ProblemModel newProblem = problemDAO.addProblem(title.trim(), "");
            if (newProblem != null) {
                loadProblems();
                // Select the newly created problem
                for (int i = 0; i < listModel.getSize(); i++) {
                    if (listModel.getElementAt(i).getId() == newProblem.getId()) {
                        problemList.setSelectedIndex(i);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(this, "Đã tạo bài toán mới!");
            } else {
                JOptionPane.showMessageDialog(this, "Tạo bài toán thất bại. Vui lòng kiểm tra lại CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editProblem() {
        ProblemModel selected = problemList.getSelectedValue();
        if (selected == null) return;
        
        String newTitle = JOptionPane.showInputDialog(this, "Đổi tên bài toán:", selected.getTitle());
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            if (problemDAO.updateProblem(selected.getId(), newTitle.trim())) {
                loadProblems();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            }
        }
    }

    private void deleteProblem() {
        ProblemModel selected = problemList.getSelectedValue();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa bài toán '" + selected.getTitle() + "'?\nĐiều này cũng sẽ xóa thư mục testcases trong máy tính.", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (problemDAO.deleteProblem(selected.getId())) {
                // Thử xóa thư mục vật lý (optional)
                try {
                    java.io.File dir = new java.io.File(selected.getFolderPath());
                    if (dir.exists()) {
                        deleteDir(dir);
                    }
                } catch (Exception ex) {}

                loadProblems();
                JOptionPane.showMessageDialog(this, "Đã xóa bài toán!");
            }
        }
    }

    private void deleteDir(java.io.File file) {
        java.io.File[] contents = file.listFiles();
        if (contents != null) {
            for (java.io.File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
