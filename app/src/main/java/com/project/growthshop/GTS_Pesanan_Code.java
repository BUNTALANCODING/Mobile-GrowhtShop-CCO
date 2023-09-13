package com.project.growthshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GTS_Pesanan_Code extends AppCompatActivity {
    private TextView namaCos, codePes;
    private Button cekPes, bliLagi;

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pesanan_code);

        namaCos = findViewById(R.id.namaCos);
        codePes = findViewById(R.id.codePesku);


        String namacos = getIntent().getStringExtra("namaku");
        String codepes = getIntent().getStringExtra("codeku");
        codePes.setText(codepes);
        namaCos.setText(namacos);

        cekPes = findViewById(R.id.btnCekpes);
        cekPes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Pesanan_Code.this,GTS_Riwayat_Pembelian.class);
                startActivity(intent);
            }
        });

        bliLagi = findViewById(R.id.belanjalagi);
        bliLagi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GTS_Pesanan_Code.this,GTS_Halaman_Utama.class);
                startActivity(intent);
            }
        });
    }


}