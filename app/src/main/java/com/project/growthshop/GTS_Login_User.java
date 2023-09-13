package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GTS_Login_User extends AppCompatActivity {
    private EditText pass, user;
    private ImageButton btnTogglePassword;
    private Button register;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        inisialisasiFirebaseAuth();
        inisialisasiTampilan();
        aturTogglePasswordListener();
        auturLoginButtonListener();
        aturRegisterButtonListener();
    }

    private void inisialisasiFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void inisialisasiTampilan() {
        pass = findViewById(R.id.etPassword);
        user = findViewById(R.id.etUsername);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        register = findViewById(R.id.btnregister);
    }


    private void aturTogglePasswordListener() {
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (pass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            pass.setInputType(InputType.TYPE_CLASS_TEXT);
            btnTogglePassword.setImageResource(R.drawable.show);
        } else {
            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.hide);
        }
        pass.setSelection(pass.getText().length());
    }

    private void auturLoginButtonListener() {
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    showAlert1();
                } else {
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(GTS_Login_User.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            if (currentUser != null) {
                                String usersession = currentUser.getDisplayName();
                                showAlert7(usersession);
                            }
                        } else {
                            showAlert6();
                        }
                    }
                });
    }

    private void aturRegisterButtonListener() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Login_User.this, GTS_Register_User.class);
                startActivity(intent);
            }
        });
    }

    private void showAlert1() {
        final alert1 alert1 = new alert1(GTS_Login_User.this);
        alert1.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert1.dismis();
            }
        }, 1600);
    }

    private void showAlert6() {
        final alert6 alert6 = new alert6(GTS_Login_User.this);
        alert6.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert6.dismis();
            }
        }, 1600);
    }

    private void showAlert7(String usersession) {
        final alert7 alert7 = new alert7(GTS_Login_User.this);
        alert7.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert7.dismis();
                Intent intent = new Intent(GTS_Login_User.this, GTS_Halaman_Utama.class);
                intent.putExtra("userId", usersession);
                startActivity(intent);
                finish();
            }
        }, 1600);
    }
}
