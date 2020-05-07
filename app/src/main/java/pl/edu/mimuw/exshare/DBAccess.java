package pl.edu.mimuw.exshare;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class DBAccess {

    /**
     * Runnable class for adding user functionality.
     */
    private static class AddUserRunnable implements Runnable {
        private String userId;
        private boolean success = false;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(null, new byte[]{});
            Request request = new okhttp3.Request.Builder()
                    .put(body)
                    .url("http://exshare.herokuapp.com/addUser/" + userId)
                    .build();
            try {
                httpClient.newCall(request).execute();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AddUserRunnable(String userId) {
            this.userId = userId;
        }

        boolean isSuccess() {
            boolean res = success;
            success = false;
            return res;
        }
    }

    /**
     * Creates new thread running @see AddUserRunnable.
     * @param userId User identifier.
     * @return true if adding succeeded, false otherwise.
     */
    static boolean addUser(String userId) {
        AddUserRunnable runnable = new AddUserRunnable(userId);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.isSuccess();
    }

    /**
     * Runnable class for adding user presence in database.
     */
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
                    exists = 2;
                } else {
                    String respBodyString = response.body().string();
                    if (respBodyString.equals("false")) {
                        exists = 0;
                    } else if (respBodyString.equals("true")) {
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

    /**
     * @param userId User identifier.
     * @return 0 - user does not exists, 1 - user exists, 2 - error in request.
     */
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

    /**
     * Assigning user to course runnable.
     */
    private static class AssignUserToCourseRunnable implements Runnable {
        String userId;
        int courseId;
        int result;

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
                if (response.code() != 200) {
                    if (response.message().equals("No such course")) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                } else {
                    result = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getResult() {
            return result;
        }

        AssignUserToCourseRunnable(String userId, int courseId) {
            this.userId = userId;
            this.courseId = courseId;
            result = -1;
        }
    }

    /**
     * Assigns user to course using another thread and waits for result.
     * @param userId User identifier.
     * @param courseId Course identifier.
     * @return -1 in case of failure. 0 in case of non-existent course assignment, 1 on success.
     */
    static int assignUserToCourse(String userId, int courseId) {
        AssignUserToCourseRunnable runnable = new AssignUserToCourseRunnable(userId, courseId);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.getResult();
    }

    /**
     * Runnable for getting user's courses.
     */
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
                if (response.body() != null) {
                    result = new JSONArray(response.body().string());
                } else {
                    result = null;
                }
            } catch (IOException | JSONException e) {
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

    /**
     * @param userId User identifier.
     * @return @see JSONArray with user courses identifiers.
     */
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

    /**
     * Runnable for getting new course identifier.
     */
    private static class GetNewCourseRunnable implements Runnable {
        private String userId;
        private String courseName;
        private int result;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new okhttp3.Request.Builder()
                    .url("http://exshare.herokuapp.com/getNewCourse/" + userId + "/" + courseName)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.body() != null && response.code() == 200) {
                    result = Integer.parseInt(response.body().string());
                } else {
                    result = -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GetNewCourseRunnable(String userId, String courseName) {
            this.userId = userId;
            this.courseName = courseName;
            result = -1;
        }

        int getResult() {
            int res =  result;
            result = -1;
            return res;
        }
    }

    /**
     * @param userId User identifier.
     * @param courseName Created course name.
     * @return Integer with new course identifier. -1 in case of failure.
     */
    static int getNewCourse(String userId, String courseName) {
        GetNewCourseRunnable runnable = new GetNewCourseRunnable(userId, courseName);
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
