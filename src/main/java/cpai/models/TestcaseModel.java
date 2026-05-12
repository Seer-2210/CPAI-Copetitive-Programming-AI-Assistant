package cpai.models;

import java.sql.Timestamp;

public class TestcaseModel {
    private int id;
    private int problemId;
    private String inputPath;
    private String outputPath;
    private Timestamp createdAt;

    public TestcaseModel() {}

    public TestcaseModel(int id, int problemId, String inputPath, String outputPath, Timestamp createdAt) {
        this.id = id;
        this.problemId = problemId;
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProblemId() { return problemId; }
    public void setProblemId(int problemId) { this.problemId = problemId; }

    public String getInputPath() { return inputPath; }
    public void setInputPath(String inputPath) { this.inputPath = inputPath; }

    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
