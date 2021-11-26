package SERVER;

public class Request {
    private String fileName, description, statusUpdate;
    int requesterID;

    public Request(String fileName, String description, int requesterID) {
        this.fileName = fileName;
        this.description = description;
        this.requesterID = requesterID;
        this.statusUpdate = "";
    }

    public String getFileName() {
        return fileName;
    }



    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(int uploaderId) {
        this.requesterID = uploaderId;
    }

    public String getStatusUpdate() {
        return statusUpdate;
    }

    public void setStatusUpdate(String statusUpdate) {
        this.statusUpdate = statusUpdate;
    }
}
