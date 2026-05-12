package cpai.models;

import java.sql.Timestamp;

public class ProblemModel {
    private int id;
    private String title;
    private String content;
    private String folderPath;
    private Timestamp createdAt;

    private String generatorCode;
    private String checkerCode;
    private String solutionCode;

    public ProblemModel() {}

    public ProblemModel(int id, String title, String content, String folderPath, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.folderPath = folderPath;
        this.createdAt = createdAt;
    }

    public ProblemModel(int id, String title, String content, String folderPath, Timestamp createdAt, 
                        String generatorCode, String checkerCode, String solutionCode) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.folderPath = folderPath;
        this.createdAt = createdAt;
        this.generatorCode = generatorCode;
        this.checkerCode = checkerCode;
        this.solutionCode = solutionCode;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFolderPath() { return folderPath; }
    public void setFolderPath(String folderPath) { this.folderPath = folderPath; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getGeneratorCode() { return generatorCode; }
    public void setGeneratorCode(String generatorCode) { this.generatorCode = generatorCode; }

    public String getCheckerCode() { return checkerCode; }
    public void setCheckerCode(String checkerCode) { this.checkerCode = checkerCode; }

    public String getSolutionCode() { return solutionCode; }
    public void setSolutionCode(String solutionCode) { this.solutionCode = solutionCode; }

    @Override
    public String toString() {
        return this.title;
    }
}