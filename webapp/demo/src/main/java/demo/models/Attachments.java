package demo.models;

import javax.persistence.*;

public class Attachments {

    private String id;

    public String fileName;

    public String fileLocation;

    public Attachments() {

    }

    private UserTransaction userTransaction;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public UserTransaction getUserTransaction() {
        return userTransaction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserTransaction(UserTransaction userTransaction) {
        this.userTransaction = userTransaction;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
