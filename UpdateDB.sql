USE CPManager;
GO

-- Thêm các cột để lưu trữ code AI
ALTER TABLE Problems
ADD GeneratorCode NVARCHAR(MAX) NULL,
    CheckerCode NVARCHAR(MAX) NULL,
    SolutionCode NVARCHAR(MAX) NULL;
GO
