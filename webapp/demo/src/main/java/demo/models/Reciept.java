package demo.models;


import javax.persistence.*;


@Entity
@Table
public class Reciept {
    @Id
    @Column(name = "id", length = 40)
    private String id;
    @Column(name = "url", length = 255)
    private String url;

    public Reciept() {

    }
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserTransaction ut;

    public UserTransaction getUt() {
        return ut;
    }

    public void setUt(UserTransaction ut) {
        this.ut = ut;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
