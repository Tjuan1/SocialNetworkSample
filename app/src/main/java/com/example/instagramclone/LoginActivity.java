package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtLoginUsername, edtLoginPassword;
    private Button btnLoginLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtLoginUsername = findViewById(R.id.edtLoginUsername);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLoginLogin = findViewById(R.id.btnLoginActivity);

        if(ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
    }

    public void onLoginSignUpClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(intent);
    }

    public void onLoginLoginClicked(View view) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        if(edtLoginUsername.getText().toString().equals("")
                || edtLoginPassword.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Username and Password are required", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setMessage("Login in " + edtLoginUsername.getText().toString());
            progressDialog.show();

            ParseUser.logInInBackground(edtLoginUsername.getText().toString(), edtLoginPassword.getText().toString(), (parseUser, e) -> {
                progressDialog.dismiss();
                if (parseUser != null) {
                    Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_LONG).show();
                } else {
                    ParseUser.logOut();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            });
        }
    }

    public void hideBackground(View view) {
        UserInput hideUsrInput = new UserInput(LoginActivity.this);
        hideUsrInput.onRootLayoutTapped(view);
    }

}