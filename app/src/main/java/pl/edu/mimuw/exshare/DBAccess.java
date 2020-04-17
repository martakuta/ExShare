package pl.edu.mimuw.exshare;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DBAccess {

    private static class AddUserRunnable implements Runnable {
        private String userId;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(null, new byte[]{});
            Request request = new okhttp3.Request.Builder()
                    .put(body)
                    .url("http://exshare.herokuapp.com/addUser/" + userId)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                System.out.println("Resp code: " + response.code());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AddUserRunnable(String userId) {
            this.userId = userId;
        }
    }

    static void addUser(String userId) {
        Thread t = new Thread(new AddUserRunnable(userId));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class UserExistsRunnable implements Runnable {
        String userId;
        int exists; //1 - true, 0 - false, 2 - error

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new okhttp3.Request.Builder()
                    .url("http://exshare.herokuapp.com/userExists/" + userId)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() != 200 || response.body() == null) {
                    //System.out.println("Resp code:  " + response.code());
                    exists = 2;
                } else {
                    String respBodyString = response.body().string();
                    if (respBodyString.equals("false")) {
                        exists = 0;
                    } else if (respBodyString.equals("true")){
                        exists = 1;
                    } else {
                        exists = 2;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        UserExistsRunnable(String userId) {
            this.userId = userId;
            exists = 2;
        }

        int getExists() {
            return exists;
        }
    }

    static int userExists(String userId) {
        UserExistsRunnable runnable = new UserExistsRunnable(userId);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return runnable.getExists();
    }

    private static class AssignUserToCourseRunnable implements Runnable {
        String userId;
        int courseId;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(null, new byte[]{});
            Request request = new okhttp3.Request.Builder()
                    .put(body)
                    .url("http://exshare.herokuapp.com/assignUserToCourse/" + userId + "/" + courseId)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                System.out.println("Resp code: " + response.code());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AssignUserToCourseRunnable(String userId, int courseId) {
            this.userId = userId;
            this.courseId = courseId;
        }
    }

    static void assignUserToCourse(String userId, int courseId) {
        Thread t = new Thread(new AssignUserToCourseRunnable(userId, courseId));
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class GetUserCoursesRunnable implements Runnable {
        private String userId;
        private JSONArray result;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new okhttp3.Request.Builder()
                    .url("http://exshare.herokuapp.com/userCourses/" + userId)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                result = new JSONArray(response.body().string());
            } catch (IOException | JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        GetUserCoursesRunnable(String userId) {
            this.userId = userId;
            result = null;
        }

        JSONArray getResult() {
            return result;
        }
    }

    static JSONArray getUserCourses(String userId) {
        GetUserCoursesRunnable runnable = new GetUserCoursesRunnable(userId);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return runnable.getResult();
    }
}
