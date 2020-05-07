package pl.edu.mimuw.exshare;

import org.json.JSONArray;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBAccessTest {
    @Test
    public void addUserTest1() {
        try {
            //no result == true assertion here - possibly such user exists.
            DBAccess.addUser("dummy1");
            try {
                assertEquals(1, DBAccess.userExists("dummy1"));
            } catch (Exception e) {
                fail("Exception should not be thrown");
            }
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }

    }

    @Test
    public void addUserTest2() {
        try {
            //no result == true assertion here - possibly such user exists.
            DBAccess.addUser("dummy2");
            try {
                assertEquals(1, DBAccess.userExists("dummy2"));
            } catch (Exception e) {
                fail("Exception should not be thrown");
            }
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void createNewCourseTest() {
        try {
            int courseID = DBAccess.getNewCourse("dummy1", "example course");
            if (courseID == -1) {
                fail("An error occurred while creating a new course.");
            }
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void addUserToCourseTest() {
        try {
            int courseID = DBAccess.getNewCourse("dummy1", "another example course");
            int ans = DBAccess.assignUserToCourse("dummy1", courseID);
            if (ans != 1) {
                fail("An error occurred while adding user to the course.");
            }
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    public void userCoursesTest() {
        try {
            JSONArray array = DBAccess.getUserCourses("dummy1");
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}
