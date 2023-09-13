package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GTS_Register_User extends AppCompatActivity {
    private ImageButton btnTogglePassword, btnTogglePassword1;
    private EditText PassConf, Pass, Name, NoHP, Email;
    private ImageView back;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);



        initializeFirebase();
        initializeViews();
        setupTogglePassword();
        setupBackButton();
        setupRegisterButton();

        int passwordMaxLength = 7;
        InputFilter[] passwordFilters = new InputFilter[1];
        passwordFilters[0] = new InputFilter.LengthFilter(passwordMaxLength);
        Name.setFilters(passwordFilters);
    }

    private void initializeViews() {
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnTogglePassword1 = findViewById(R.id.btnTogglePassword1);
        PassConf = findViewById(R.id.etPasswordConf);
        Pass = findViewById(R.id.etPassword);
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        NoHP = findViewById(R.id.nohp);
        back = findViewById(R.id.back);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupTogglePassword() {
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(PassConf, btnTogglePassword);
            }
        });

        btnTogglePassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(Pass, btnTogglePassword1);
            }
        });
    }

    private void togglePasswordVisibility(EditText Pass, ImageButton btnTogglePassword) {
        if (Pass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Show password
            Pass.setInputType(InputType.TYPE_CLASS_TEXT);
            btnTogglePassword.setImageResource(R.drawable.show);
        } else {
            // Hide password
            Pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.hide);
        }
        Pass.setSelection(Pass.getText().length()); // Memastikan kursor tetap di akhir teks
    }

    private void setupBackButton() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Register_User.this, GTS_Login_User.class);
                startActivity(intent);
            }
        });
    }

    private void setupRegisterButton() {
        Button btnRegister = findViewById(R.id.btnregis);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = Name.getText().toString().trim();
                String email = Email.getText().toString().trim();
                String hp = NoHP.getText().toString().trim();
                String password = Pass.getText().toString().trim();
                String passwordConf = PassConf.getText().toString().trim();

                if (isFieldEmpty(name, email, hp, password, passwordConf)) {
                    showAlert1();
                } else if (!isValidEmail(email)) {
                    showAlert8();
                } else if (!isNumeric(hp)) {
                    showAlert9();
                } else if (hp.length() < 10) {
                    showAlert9();
                } else if (password.length() < 6 ) {
                    showAlert3();
                } else if (!password.equals(passwordConf)) {
                    showAlert2();
                } else {
                    registerUser(email, password, name, hp);
                }
            }
        });
    }

    private void registerUser(String email, String password, String name, final String hp) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Pengguna berhasil terdaftar, mendapatkan UID pengguna
                        String userId = mAuth.getCurrentUser().getUid();
                        showAlert5();
                        clearForm();
                        // Simpan data pengguna ke Realtime Database
                        DatabaseReference userRef = mDatabase.child("pengguna").child(userId);
                        userRef.child("name").setValue(name);
                        userRef.child("email").setValue(email);
                        userRef.child("No_HP").setValue(hp);

                    } else {
                        // Penanganan kesalahan saat pendaftaran pengguna
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            showAlert4();
                        } else {
                            Log.e("RegisterActivity", "Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void clearForm() {
        Name.setText("");
        Email.setText("");
        Pass.setText("");
        PassConf.setText("");
        NoHP.setText("");
        Name.requestFocus();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // Pattern untuk format email
        return email.matches(emailPattern);
    }

    private boolean isNumeric(String hp) {
        return hp.matches("-?\\d+(\\.\\d+)?"); // Mengecek apakah string hanya berisi angka
    }

    private boolean isFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void showAlert1() {
        final alert1 alert1 = new alert1(GTS_Register_User.this);
        alert1.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert1.dismis();
            }
        }, 1600);
    }

    private void showAlert2() {
        final alert2 alert2 = new alert2(GTS_Register_User.this);
        alert2.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert2.dismis();
            }
        }, 1600);
    }

    private void showAlert3() {
        final alert3 alert3 = new alert3(GTS_Register_User.this);
        alert3.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert3.dismis();
            }
        }, 1600);
    }

    private void showAlert4() {
        final alert4 alert4 = new alert4(GTS_Register_User.this);
        alert4.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert4.dismis();
            }
        }, 1600);
    }

    private void showAlert5() {
        final alert5 alert5 = new alert5(GTS_Register_User.this);
        alert5.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert5.dismis();
            }
        }, 1600);
    }

    private void showAlert8() {
        final alert8 alert8 = new alert8(GTS_Register_User.this);
        alert8.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert8.dismis();
            }
        }, 1600);
    }

    private void showAlert9() {
        final alert9 alert9 = new alert9(GTS_Register_User.this);
        alert9.startLoding();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert9.dismis();
            }
        }, 1600);
    }
}
