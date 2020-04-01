package com.example.exshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private UsersBase usersBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usersBase = new UsersBase();

        final TextView name1 = findViewById(R.id.login_hello1_text);
        final TextView name2 = findViewById(R.id.login_hello2_text);
        final EditText email = findViewById(R.id.sign_in_email);
        final EditText password = findViewById(R.id.sign_in_password);
        Button buttonLogin = findViewById(R.id.sign_in_button);
        final TextView incorrectLogin = findViewById(R.id.incorrect_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersBase.validate(
                        email.getText().toString(),
                        password.getText().toString())) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    incorrectLogin.setText("Niepoprawny email lub hasło.");
                }
            }
        });

    }
}

