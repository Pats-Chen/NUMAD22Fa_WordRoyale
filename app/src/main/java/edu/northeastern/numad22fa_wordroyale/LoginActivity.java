package edu.northeastern.numad22fa_wordroyale;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private TextInputLayout loginTextUsername;
    private TextInputLayout loginTextPassword;
    private Button loginButtonLogin;
    private Button loginButtonSignup;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTextUsername = findViewById(R.id.loginTextUsername);
        loginTextPassword = findViewById(R.id.loginTextPassword);
        loginButtonLogin = findViewById(R.id.loginButtonLogin);
        loginButtonSignup = findViewById(R.id.loginButtonSignup);
    }

    public void signup(View v) {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        String username = Objects.requireNonNull(loginTextUsername.getEditText()).getText().toString();
        String password = Objects.requireNonNull(loginTextPassword.getEditText()).getText().toString();

        User newUser = new User(username, password);

        reference.setValue(newUser);
    }

    public void login(View v) {

    }
}
