package edu.northeastern.numad22fa_wordroyale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button loginButtonLogin;
    private Button loginButtonSignup;
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTextUserEmail = findViewById(R.id.loginTextUserEmail);
        loginTextPassword = findViewById(R.id.loginTextPassword);
        loginButtonLogin = findViewById(R.id.loginButtonLogin);
        loginButtonSignup = findViewById(R.id.loginButtonSignup);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if(userAuth.getCurrentUser() != null){
//            finish();
//            return;
//        }
//    }
//

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
                            // TODO: prompt user already exists.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        rootRef.child("users").child("000000").child("nextUserID").get().addOnCompleteListener(task -> {
//            if (!task.isSuccessful()) {
//                Log.e(TAG, "Error getting data", task.getException());
//            } else {
//                Log.d(TAG, String.valueOf(task.getResult().getValue()));
//                String newUserID = String.valueOf(task.getResult().getValue());

//                User newUser = new User(newUserID, userEmail, password);
//                rootRef.child("users").child(newUserID).setValue(newUser);

//                int newUserIDPlus = Integer.parseInt(newUserID) + 1;
//                String newUserIDPlusStr = Integer.toString(newUserIDPlus);
//                String nextUserID = String.format("%1$" + 6 + "s", newUserIDPlusStr).replace(' ', '0');
//                rootRef.child("users").child("000000").child("nextUserID").setValue(nextUserID);
//            }
//        });
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
                            // TODO: prompt password failure or user does not exist.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
