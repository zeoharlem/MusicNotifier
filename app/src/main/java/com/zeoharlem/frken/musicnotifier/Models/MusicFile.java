package com.zeoharlem.frken.musicnotifier.Models;

public class MusicFile {

    private String id;
    private String createdAt;
    private String filename;
    private String folderPath;
    private String status;
    private String timeSet;
    private int intentReqCode;

    public int getIntentReqCode() {
        return intentReqCode;
    }

    public void setIntentReqCode(int intentReqCode) {
        this.intentReqCode = intentReqCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeSet() {
        return timeSet;
    }

    public void setTimeSet(String timeSet) {
        this.timeSet = timeSet;
    }
}
