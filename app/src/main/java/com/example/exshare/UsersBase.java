package com.example.exshare;

import java.util.HashMap;

public class UsersBase {

    private HashMap<String, String> users;

    UsersBase() {
        users = new HashMap<>();
        users.put("marta@gmail.com", "marta");
        users.put("kacper@gmail.com", "kacper");
        users.put("gevorg@gmail.com", "gevorg");
        users.put("a", "a");
    }

    public boolean validate(String email, String password) {
        if (users.containsKey(email)) {
            return users.get(email).equals(password);
        } else {
            return false;
        }
    }
}
