package cpai.models;

import java.sql.Timestamp;

public class SubmissionModel {
    private int id;
    private int problemId;
    private String language;
    private String sourcePath;
    private String status;
    private int executionTime;
    private Timestamp createdAt;

    public SubmissionModel() {}

    public SubmissionModel(int id, int problemId, String language, String sourcePath, String status, int executionTime, Timestamp createdAt) {
        this.id = id;
        this.problemId = problemId;
        this.language = language;
        this.sourcePath = sourcePath;
        this.status = status;
        this.executionTime = executionTime;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProblemId() { return problemId; }
    public void setProblemId(int problemId) { this.problemId = problemId; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getExecutionTime() { return executionTime; }
    public void setExecutionTime(int executionTime) { this.executionTime = executionTime; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
