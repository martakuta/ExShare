package pl.edu.mimuw.exshare;

public class UserData {
    private String name;
    private String mail;
    private String id;

    UserData(String name, String mail, String id) {
        this.name = name;
        this.mail = mail;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getId() {
        return id;
    }
}
