package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.sql.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    Button btnLogin;
    private TextView txtClickMe;
    private EditText editTextEmail, editTextPass;
    public DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        databaseHelper = new DatabaseHelper(this);
        btnLogin = findViewById(R.id.btn_Login);
        editTextEmail = findViewById(R.id.editText_Email);
        editTextPass = findViewById(R.id.editText_Password);
        editTextEmail.setText( "jaimin@gmail.com" );
        editTextPass.setText( "1234567" );
        findViewById(R.id.btn_Login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String pass = editTextPass.getText().toString();
                if (!isValidEmail(email)) {
                    editTextEmail.setError("Invalid Email");
                } else if (!isValidPass(pass)) {
                    editTextPass.setError("Invalid Password");
                } else {
                    User user = new User();
                    user.setEmail(editTextEmail.getText().toString());
                    user.setPassword(editTextPass.getText().toString());
                    if (databaseHelper.checkUserForLogin(email , pass)){
                        storeEmailtoSharedPref(email, true);
                        Intent intent = new Intent(SignInActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        Toast.makeText(SignInActivity.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignInActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtClickMe = findViewById(R.id.txt_ClickMe);
        txtClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPass(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        } else {
            return false;
        }
    }

    private void storeEmailtoSharedPref(String email, boolean sessionLogin) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("session_login", sessionLogin);
        editor.putString("email", email);
        editor.apply();
    }
}
