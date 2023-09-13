package com.project.growthshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GTS_Halaman_Awal extends AppCompatActivity {
    private Button lanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halaman_awal);

        lanjut = findViewById(R.id.lanjut);
        lanjut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Halaman_Awal.this,GTS_Login_User.class);
                startActivity(intent);
            }
        });
    }
}