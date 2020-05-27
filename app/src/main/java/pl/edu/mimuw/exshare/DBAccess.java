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
     *
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
     *
     * @param userId   User identifier.
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
            int res = result;
            result = -1;
            return res;
        }
    }

    /**
     * @param userId     User identifier.
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

    /**
     * Runnable for getting course name.
     */
    private static class GetCourseNameRunnable implements Runnable {
        private int courseId;
        private String result;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new okhttp3.Request.Builder()
                    .url("http://exshare.herokuapp.com/getCourseName/" + courseId)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() == 200) {
                    result = response.body().string();
                } else {
                    result = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GetCourseNameRunnable(int courseId) {
            this.courseId = courseId;
            this.result = null;
        }

        String getResult() {
            String res = result;
            result = null;
            return res;
        }
    }

    /**
     * @param courseId Course identifier.
     * @return Course name. Null if there is no such course.
     */
    static String getCourseName(int courseId) {
        GetCourseNameRunnable runnable = new GetCourseNameRunnable(courseId);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.getResult();
    }

    private static class GetCourseTestsRunnable implements Runnable {
        private int courseId;
        private JSONArray result;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new okhttp3.Request.Builder()
                    .url("http://exshare.herokuapp.com/getCourseTests/" + courseId)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() == 200) {
                    result = new JSONArray(response.body().string());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        public GetCourseTestsRunnable(int courseId) {
            this.courseId = courseId;
        }

        JSONArray getResult() {
            JSONArray res = result;
            result = null;
            return res;
        }
    }

    static JSONArray getCourseTests(int courseId) {
        GetCourseTestsRunnable runnable = new GetCourseTestsRunnable(courseId);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.getResult();
    }

    private static class AddCourseTestsRunnable implements Runnable {
        int courseId;
        String testName;
        boolean result;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(null, new byte[]{});
            Request request = new okhttp3.Request.Builder()
                    .put(body)
                    .url("http://exshare.herokuapp.com/addTestToCourse/" + courseId + "/" + testName)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() != 200) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean getResult() {
            boolean res = result;
            result = false;
            return res;
        }

        public AddCourseTestsRunnable(int courseId, String testName) {
            this.courseId = courseId;
            this.testName = testName;
            this.result = false;
        }
    }

    static boolean addCourseTest(int courseId, String testName) {
        AddCourseTestsRunnable runnable = new AddCourseTestsRunnable(courseId, testName);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.getResult();
    }

    private static class GetTestExercisesRunnable implements Runnable {
        private int courseId;
        private String testName;
        private JSONArray result;

        @Override
        public void run() {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new okhttp3.Request.Builder()
                    .url("http://exshare.herokuapp.com/getTestTasks/" + courseId + "/" + testName)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() == 200) {
                    result = new JSONArray(response.body().string());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        public GetTestExercisesRunnable(int courseId, String testName) {
            this.courseId = courseId;
            this.testName = testName;
            this.result = null;
        }

        JSONArray getResult() {
            JSONArray res = result;
            result = null;
            return res;
        }
    }

    static JSONArray getTestExercises(int courseId, String testName) {
        GetTestExercisesRunnable runnable = new GetTestExercisesRunnable(courseId, testName);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.getResult();
    }

    private static class AddTestExerciseRunnable implements Runnable {
        int courseId;
        String testName;
        int taskNum;
        boolean result;

        @Override
        public void run() {
            System.out.println("http://exshare.herokuapp.com/addTestTask/" + courseId + "/" + testName + "/" + taskNum);
            OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create(null, new byte[]{});
            Request request = new okhttp3.Request.Builder()
                    .put(body)
                    .url("http://exshare.herokuapp.com/addTestTask/" + courseId + "/" + testName + "/" + taskNum)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.code() != 200) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean getResult() {
            boolean res = result;
            result = false;
            return res;
        }

        public AddTestExerciseRunnable(int courseId, String testName, int taskNum) {
            this.courseId = courseId;
            this.testName = testName;
            this.taskNum = taskNum;
            result = false;
        }
    }

    static boolean addTestExercise(int courseId, String testName, int taskNum) {
        System.out.println("Jestem tutaj");
        AddTestExerciseRunnable runnable = new AddTestExerciseRunnable(courseId, testName, taskNum);
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return runnable.getResult();
    }

    static JSONArray getCourseFolders(int courseID) {
        String[] ans = new String[3];
        ans[0] = "folder 1";
        ans[1] = "folder 2";
        ans[2] = "folder 3";
        try {
            return new JSONArray(ans);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static JSONArray getFolderTests(int courseID, String folderName) {
        String[] ans = new String[6];
        ans[0] = "zielony";
        ans[1] = "niebieski";
        ans[2] = "zolty";
        ans[3] = "czerwony";
        ans[4] = "czarny";
        ans[5] = "bialy";
        try {
            return new JSONArray(ans);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void addFolder(int courseID, JSONArray jsonFolderArray) {
        // w pierwszym polu jest nazwa folderu, a w pozostałych nazwy sprawdzianów do niego należących
        try {
            System.out.println("Dodano folder o nazwie " + jsonFolderArray.get(0) + " do kursu " + courseID);
            System.out.println("Jego elementy to (1 to nazwa folderu): " + jsonFolderArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}