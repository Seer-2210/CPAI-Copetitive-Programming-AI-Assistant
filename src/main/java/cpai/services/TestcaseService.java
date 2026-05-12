package cpai.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestcaseService {

    // Đường dẫn gốc lưu trữ workspace
    private static final String WORKSPACE_DIR = "cp_workspace/problems";

    /**
     * Tạo testcases (chỉ phần input) từ đoạn code generator C++
     * @param problemId ID của bài toán
     * @param generatorCode Đoạn code C++ sinh testcase
     * @param numTestcases Số lượng testcase cần sinh
     */
    public static void generateTestcases(int problemId, String generatorCode, int numTestcases) {
        try {
            // Tạo thư mục nếu chưa có
            Path problemDir = Paths.get(WORKSPACE_DIR, String.valueOf(problemId));
            Path testcasesDir = problemDir.resolve("testcases");
            Files.createDirectories(testcasesDir);

            // 1. Lưu code C++ vào file generator.cpp
            Path generatorFile = problemDir.resolve("generator.cpp");
            Files.writeString(generatorFile, generatorCode);
            System.out.println("Đã lưu " + generatorFile.toAbsolutePath());

            // 2. Biên dịch generator.cpp thành generator.exe
            String exeName = "generator.exe"; // Windows
            ProcessBuilder compilePb = new ProcessBuilder("g++", "-O3", "generator.cpp", "-o", exeName);
            compilePb.directory(problemDir.toFile());
            compilePb.redirectErrorStream(true);
            
            Process compileProcess = compilePb.start();
            int compileExitCode = compileProcess.waitFor();
            
            if (compileExitCode != 0) {
                String compileError = new String(compileProcess.getInputStream().readAllBytes());
                System.err.println("Lỗi biên dịch generator: \n" + compileError);
                return;
            }
            System.out.println("Biên dịch generator thành công!");

            // 3. Chạy generator.exe n lần để sinh input (1.in, 2.in, ...)
            for (int i = 1; i <= numTestcases; i++) {
                int seed = (int) (System.currentTimeMillis() + i * 1000); // Tạo seed ngẫu nhiên
                
                ProcessBuilder runPb = new ProcessBuilder(problemDir.resolve(exeName).toAbsolutePath().toString(), String.valueOf(seed));
                runPb.directory(problemDir.toFile());
                
                // Hướng kết quả đầu ra (stdout) thẳng vào file {i}.in
                File inputFile = testcasesDir.resolve(i + ".in").toFile();
                runPb.redirectOutput(inputFile);
                runPb.redirectError(ProcessBuilder.Redirect.INHERIT);
                
                Process runProcess = runPb.start();
                runProcess.waitFor();
                System.out.println("Đã tạo file: " + inputFile.getName());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Chạy code AC (C++) để sinh các file output (.out) từ các file (.in) đã có
     * @param problemId ID của bài toán
     * @param acCode Đoạn code C++ chuẩn (Accepted)
     * @param numTestcases Số lượng testcase hiện có
     */
    public static void generateOutputsWithAC(int problemId, String acCode, int numTestcases) {
        try {
            Path problemDir = Paths.get(WORKSPACE_DIR, String.valueOf(problemId));
            Path testcasesDir = problemDir.resolve("testcases");

            // 1. Lưu code AC
            Path acFile = problemDir.resolve("ac_solution.cpp");
            Files.writeString(acFile, acCode);

            // 2. Biên dịch ac_solution.cpp
            String exeName = "ac_solution.exe";
            ProcessBuilder compilePb = new ProcessBuilder("g++", "-O3", "ac_solution.cpp", "-o", exeName);
            compilePb.directory(problemDir.toFile());
            compilePb.redirectErrorStream(true);
            
            Process compileProcess = compilePb.start();
            if (compileProcess.waitFor() != 0) {
                System.err.println("Lỗi biên dịch code AC!");
                return;
            }
            System.out.println("Biên dịch code AC thành công!");

            // 3. Chạy code AC với từng file input để sinh output
            for (int i = 1; i <= numTestcases; i++) {
                File inputFile = testcasesDir.resolve(i + ".in").toFile();
                File outputFile = testcasesDir.resolve(i + ".out").toFile();

                if (!inputFile.exists()) continue;

                ProcessBuilder runPb = new ProcessBuilder(problemDir.resolve(exeName).toAbsolutePath().toString());
                runPb.directory(problemDir.toFile());
                
                // Đưa 1.in vào luồng stdin, kết xuất stdout ra 1.out
                runPb.redirectInput(inputFile);
                runPb.redirectOutput(outputFile);
                runPb.redirectError(ProcessBuilder.Redirect.INHERIT);
                
                Process runProcess = runPb.start();
                runProcess.waitFor();
                System.out.println("Đã tạo file: " + outputFile.getName());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class JudgeResult {
        public String testcaseName;
        public String status;
        public long timeMs;
        public JudgeResult(String t, String s, long ms) {
            this.testcaseName = t; this.status = s; this.timeMs = ms;
        }
    }

    /**
     * Chấm bài (Judge) với tùy chọn Checker
     */
    public static java.util.List<JudgeResult> judgeCode(int problemId, String userCode, String checkerCode) {
        java.util.List<JudgeResult> results = new java.util.ArrayList<>();
        try {
            Path problemDir = Paths.get(WORKSPACE_DIR, String.valueOf(problemId));
            Path testcasesDir = problemDir.resolve("testcases");

            // 1. Lưu user code
            Path userFile = problemDir.resolve("user_solution.cpp");
            Files.writeString(userFile, userCode);

            // 2. Biên dịch user code
            String exeName = "user_solution.exe";
            ProcessBuilder compilePb = new ProcessBuilder("g++", "-O3", "user_solution.cpp", "-o", exeName);
            compilePb.directory(problemDir.toFile());
            compilePb.redirectErrorStream(true);
            
            Process compileProcess = compilePb.start();
            if (compileProcess.waitFor() != 0) {
                results.add(new JudgeResult("Compile", "CE (Compile Error)", 0));
                return results;
            }

            // 3. Chuẩn bị Checker (Nếu có)
            boolean useChecker = checkerCode != null && !checkerCode.trim().isEmpty();
            String checkerExeName = "checker.exe";
            if (useChecker) {
                Path checkerFile = problemDir.resolve("checker.cpp");
                Files.writeString(checkerFile, checkerCode);
                ProcessBuilder checkerCompilePb = new ProcessBuilder("g++", "-O3", "checker.cpp", "-o", checkerExeName);
                checkerCompilePb.directory(problemDir.toFile());
                if (checkerCompilePb.start().waitFor() != 0) {
                    results.add(new JudgeResult("Checker", "CE (Checker Compile Error)", 0));
                    return results;
                }
            }

            // 3. Chạy các testcase
            File dir = testcasesDir.toFile();
            if (!dir.exists() || !dir.isDirectory()) {
                results.add(new JudgeResult("System", "No Testcases Found", 0));
                return results;
            }

            File[] inFiles = dir.listFiles((d, name) -> name.endsWith(".in"));
            if (inFiles == null || inFiles.length == 0) {
                results.add(new JudgeResult("System", "No Testcases Found", 0));
                return results;
            }

            java.util.Arrays.sort(inFiles, (f1, f2) -> {
                try {
                    int n1 = Integer.parseInt(f1.getName().replace(".in", ""));
                    int n2 = Integer.parseInt(f2.getName().replace(".in", ""));
                    return Integer.compare(n1, n2);
                } catch (Exception e) { return f1.getName().compareTo(f2.getName()); }
            });

            int numThreads = Runtime.getRuntime().availableProcessors();
            java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(numThreads);
            java.util.List<java.util.concurrent.Callable<JudgeResult>> tasks = new java.util.ArrayList<>();

            for (File inFile : inFiles) {
                tasks.add(() -> {
                    String baseName = inFile.getName().replace(".in", "");
                    File outFile = testcasesDir.resolve(baseName + ".out").toFile();
                    if (!outFile.exists()) {
                        return new JudgeResult(baseName, "Skipped (No .out)", 0);
                    }

                    File userOutFile = problemDir.resolve("user_" + baseName + ".out").toFile();

                    ProcessBuilder runPb = new ProcessBuilder(problemDir.resolve(exeName).toAbsolutePath().toString());
                    runPb.directory(problemDir.toFile());
                    runPb.redirectInput(inFile);
                    runPb.redirectOutput(userOutFile);
                    runPb.redirectError(ProcessBuilder.Redirect.INHERIT);

                    long startTime = System.currentTimeMillis();
                    Process runProcess = runPb.start();
                    
                    // Watchdog thread to monitor Output Limit Exceeded (OLE)
                    long maxOutputSize = 64 * 1024 * 1024; // 64MB limit
                    boolean[] isOle = {false};
                    Thread watchdog = new Thread(() -> {
                        while (runProcess.isAlive()) {
                            if (userOutFile.exists() && userOutFile.length() > maxOutputSize) {
                                isOle[0] = true;
                                runProcess.destroyForcibly();
                                break;
                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    });
                    watchdog.start();

                    boolean finished = runProcess.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                    watchdog.interrupt(); // Stop watchdog if process finished early
                    
                    long endTime = System.currentTimeMillis();
                    long timeTaken = endTime - startTime;

                    if (isOle[0]) {
                        return new JudgeResult(baseName, "OLE (Output Limit Exceeded)", timeTaken);
                    }

                    if (!finished) {
                        runProcess.destroyForcibly();
                        return new JudgeResult(baseName, "TLE (Time Limit Exceeded)", timeTaken);
                    }

                    if (runProcess.exitValue() != 0) {
                        return new JudgeResult(baseName, "RTE (Runtime Error)", timeTaken);
                    }

                    // So sánh output hoặc dùng checker
                    if (useChecker) {
                        // checker.exe <input_file> <expected_out_file> <user_out_file>
                        ProcessBuilder checkerRunPb = new ProcessBuilder(problemDir.resolve(checkerExeName).toAbsolutePath().toString(), 
                            inFile.getAbsolutePath(), outFile.getAbsolutePath(), userOutFile.getAbsolutePath());
                        checkerRunPb.directory(problemDir.toFile());
                        Process checkerProc = checkerRunPb.start();
                        boolean checkerFinished = checkerProc.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
                        
                        if (!checkerFinished) {
                            checkerProc.destroyForcibly();
                            return new JudgeResult(baseName, "WA (Checker TLE)", timeTaken);
                        } else if (checkerProc.exitValue() == 0) {
                            return new JudgeResult(baseName, "AC (Accepted)", timeTaken);
                        } else {
                            return new JudgeResult(baseName, "WA (Wrong Answer)", timeTaken);
                        }
                    } else {
                        // String matching mặc định
                        String expected = Files.readString(outFile.toPath()).trim().replace("\r\n", "\n");
                        String actual = Files.readString(userOutFile.toPath()).trim().replace("\r\n", "\n");

                        if (expected.equals(actual)) {
                            return new JudgeResult(baseName, "AC (Accepted)", timeTaken);
                        } else {
                            return new JudgeResult(baseName, "WA (Wrong Answer)", timeTaken);
                        }
                    }
                });
            }

            try {
                java.util.List<java.util.concurrent.Future<JudgeResult>> futures = executor.invokeAll(tasks);
                for (java.util.concurrent.Future<JudgeResult> future : futures) {
                    results.add(future.get());
                }
            } finally {
                executor.shutdown();
            }

        } catch (Exception e) {
            e.printStackTrace();
            results.add(new JudgeResult("System", "Error: " + e.getMessage(), 0));
        }
        return results;
    }
}
