package demo.models;

import javax.persistence.*;

@Entity
@Table
public class Attachments {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 40)
    private String id;

    @Column(name = "fileName", length = 40)
    public String fileName;

    @Column(name = "fileLocation", length = 255)
    public String fileLocation;

    public Attachments() {

    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
