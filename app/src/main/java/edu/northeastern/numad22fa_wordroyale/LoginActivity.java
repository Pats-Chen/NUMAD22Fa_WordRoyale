package edu.northeastern.numad22fa_wordroyale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private TextInputLayout loginTextUserEmail;
    private TextInputLayout loginTextPassword;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTextUserEmail = findViewById(R.id.loginTextUserEmail);
        loginTextPassword = findViewById(R.id.loginTextPassword);
    }

    public void signup(View v) {
        String userEmail = Objects.requireNonNull(loginTextUserEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(loginTextPassword.getEditText()).getText().toString();

        userAuth.createUserWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "createUserWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
                            User newUser = new User(userEmail, password);
                            newUser.setUserUID(userAuth.getCurrentUser().getUid());

                            rootRef.child("users")
                                    .child(Objects.requireNonNull(userAuth.getCurrentUser()).getUid())
                                    .setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showMainActivity();
                                        }
                                    });
                        } else {
                            switch (task.getException().getMessage()) {
                                case "The email address is already in use by another account.":
                                    Toast.makeText(LoginActivity.this, "Email is used!", Toast.LENGTH_SHORT).show();
                                    break;
                                case "The email address is badly formatted.":
                                    Toast.makeText(LoginActivity.this, "Incorrect Email address!", Toast.LENGTH_SHORT).show();
                                    break;
                                case "The given password is invalid. [ Password should be at least 6 characters ]":
                                    Toast.makeText(LoginActivity.this, "Password should be at least 6 characters.", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void login(View v) {
        String userEmail = Objects.requireNonNull(loginTextUserEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(loginTextPassword.getEditText()).getText().toString();

        userAuth.signInWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Authentication succeeded.", Toast.LENGTH_SHORT).show();
                            showMainActivity();
                        } else {
                            switch (task.getException().getMessage()) {
                                case "The password is invalid or the user does not have a password.":
                                    Toast.makeText(LoginActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                    break;
                                case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                    Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
