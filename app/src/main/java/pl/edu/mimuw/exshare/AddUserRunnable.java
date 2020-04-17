package pl.edu.mimuw.exshare;

import org.json.JSONObject;

public class AddUserRunnable implements Runnable {

    private JSONObject result;
    private String userId;

    @Override
    public void run() {

    }

    AddUserRunnable(String userId) {
        this.userId = userId;
        result = null;
    }
}
