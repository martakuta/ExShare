package pl.edu.mimuw.exshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 0;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    SignInButton google_sign_in;

    void logIn(UserData data) {
        // 'data' zawiera dane użytkownika
        Toast.makeText(this, "Signed in as " + data.getName(), Toast.LENGTH_SHORT).show();
        Log.i("Login","Signed user id: " + data.getId());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        Bundle b = new Bundle();
        b.putString("user_id", data.getId());
        b.putString("user_name", data.getName());
        b.putString("user_mail", data.getMail());
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView name1 = findViewById(R.id.login_hello1_text);
        final TextView name2 = findViewById(R.id.login_hello2_text);
        final TextView incorrectLogin = findViewById(R.id.incorrect_login);


        mAuth=FirebaseAuth.getInstance();
        // Tworzę GoogleSignInOptions Objekt
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Tworzę GoogleSignInClient z opcjami w gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_sign_in = findViewById(R.id.google_sign_in);
        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.google_sign_in:
                        signIn();
                        break;
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if (account != null) {
                    if (DBAccess.userExists(account.getId()) == 0) {
                        Log.i("DB", "User " + account.getId() + " is missing. Adding");
                        DBAccess.addUser(account.getId());
                    }
                    Toast.makeText(this, "Sign in success", Toast.LENGTH_SHORT).show();
                    logIn(new UserData(account.getDisplayName(),account.getEmail(), account.getId()));
                }
                else {
                    Toast.makeText(this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                }

        } catch (ApiException e) {
            Toast.makeText(this, "signInResult:failed code=" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.w("Sign in Error", "signInResult:failed code=" + e.getStatusCode());
            // TODO:: przyda się jakaś wspołna funkcja do obsługi błędnych prób loginu
            final TextView incorrectLogin = findViewById(R.id.incorrect_login);
            incorrectLogin.setText("Błąd przy logowaniu z Google");
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Sprawdź istniejące konto Google do logowania, jeśli użytkownik jest już zalogowany
        // to GoogleSignInAccount ma wartości nie null-ową.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Już jest zalowogany przez Google
        if(account==null)
            Toast.makeText(this, "Please Sign in", Toast.LENGTH_SHORT).show();
        if(account!=null) {
            if (DBAccess.userExists(account.getId()) != 1) {
                //TODO: Log-in error. User absent in database.
                DBAccess.addUser(account.getId());
            }
            logIn(new UserData(account.getDisplayName(),account.getEmail(),account.getId()));
        }
    }
    // TODO:: funkcja dla wylogowania się
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> changeToLoginUI()); {}
    }
    private void changeToLoginUI() {
        setContentView(R.layout.activity_login);
    }

}

