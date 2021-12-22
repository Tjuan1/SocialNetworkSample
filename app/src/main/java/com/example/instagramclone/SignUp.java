package com.example.instagramclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends AppCompatActivity {

    private EditText edtEmail, edtUsername, edtPassword;
    private Button btnSignUp, btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail = findViewById(R.id.edtEnterEmail);
        edtPassword = findViewById(R.id.edtEnterPassword);

        btnSignUp = findViewById(R.id.btnSignUp);

//      this is as if user presses sign up but with the enter key in the keypad instead after
//        entering the password
        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onSignUpClicked(btnSignUp);
                }
                return false;
            }
        });
        edtUsername = findViewById(R.id.edtEnterUsername);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        if(ParseUser.getCurrentUser() != null) {
            transitionToSocialMediaActivity();
        }

    }

    public void onSignUpClicked(View view) {
        if(edtEmail.getText().toString().equals("")
                || edtUsername.getText().toString().equals("")
                || edtPassword.getText().toString().equals("")) {
            Toast.makeText(SignUp.this, "Email, Username and Password are required", Toast.LENGTH_LONG).show();
        } else  {
            final ParseUser appUser = new ParseUser();
            appUser.setEmail(edtEmail.getText().toString());
            appUser.setUsername(edtUsername.getText().toString());
            appUser.setPassword(edtPassword.getText().toString());

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing up " + edtUsername.getText().toString());
            progressDialog.show();

            appUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        Toast.makeText(SignUp.this, appUser.getUsername() + " is signed successfully!", Toast.LENGTH_LONG).show();
                        transitionToSocialMediaActivity();
                    } else {
                        ParseUser.logOut();
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            progressDialog.dismiss();
        }

    }

    public void onLoginClicked(View view) {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }

//    this hides the keyboard if we tap on the background
    public void onRootLayoutTapped(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hideBackground(View view) {
        UserInput hideUsrInput = new UserInput(SignUp.this);
        hideUsrInput.onRootLayoutTapped(view);
    }

    private void transitionToSocialMediaActivity() {
        Intent intent = new Intent(SignUp.this, SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }

}