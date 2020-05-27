package pl.edu.mimuw.exshare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {
    UserData userData;
    private AppBarConfiguration mAppBarConfiguration;
    private static int presentCourseID = -1;
    private static String presentTestName = "";
    private static String presentFolderName = "";
    private static int presentExerciseNumber = -1;
    private static boolean testAlreadyCreated = false;

    public int getPresentCourseID() {
        return presentCourseID;
    }

    public void setPresentCourseID(int newID) {
        presentCourseID = newID;
    }

    public String getPresentTestName() {
        return presentTestName;
    }

    public void setPresentTestName(String newName) {
        presentTestName = newName;
    }

    public int getPresentExerciseNumber() {
        return presentExerciseNumber;
    }

    public void setPresentExerciseNumber(int newNumber) {
        presentExerciseNumber = newNumber;
    }

    public boolean getTestAlreadyCreated() {
        return testAlreadyCreated;
    }

    public void setTestAlreadyCreated(boolean newState) {
        testAlreadyCreated = newState;
    }

    public String getPresentFolderName() {
        return presentFolderName;
    }

    public void setPresentFolderName(String newName) {
        presentFolderName = newName;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            userData = new UserData(account.getDisplayName(), account.getEmail(), account.getId(), account.getIdToken());
        } else {
            throw new AssertionError("Can't find Google account.");
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home,
                R.id.nav_your_courses, R.id.nav_join_course, R.id.nav_create_course)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
