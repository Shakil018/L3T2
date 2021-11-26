package SERVER;

class MyFile{
    private String fileName, fileType;
    private int uploaderId;

    public MyFile(String fileName, String fileType, int uploaderId) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.uploaderId = uploaderId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

}
