package pl.edu.mimuw.exshare;

public class UserData {
    private String name;
    private String mail;
    private String id;
    private String idToken;

    UserData(String name, String mail, String id, String idToken) {
        this.name = name;
        this.mail = mail;
        this.id = id;
        this.idToken = idToken;
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

    public String getIdToken() {
        return idToken;
    }
}
