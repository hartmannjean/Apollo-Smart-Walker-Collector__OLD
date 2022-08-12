package com.example.service_runner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
public class CheckListActivity extends AppCompatActivity {

    private String nome, idade, altura, peso;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_check_list_activity);
        //Captura dados que vem da MainActivity
        Intent intent = this.getIntent();
        nome = intent.getStringExtra("nome");
        idade = intent.getStringExtra("idade");
        altura = intent.getStringExtra("altura");
        peso = intent.getStringExtra("peso");
    }

    public void abrirTerceiraTela(View view){
        Intent sac = new Intent(this, SensorActivity.class);
        sac.putExtra("nome", nome);
        sac.putExtra("idade", idade);
        sac.putExtra("altura", altura);
        sac.putExtra("peso", peso);

        startActivity(sac);
    }
}