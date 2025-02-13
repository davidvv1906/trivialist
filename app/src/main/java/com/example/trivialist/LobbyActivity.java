package com.example.trivialist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.trivialist.databinding.ActivityLobbyBinding;

public class LobbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLobbyBinding binding = DataBindingUtil.setContentView(this , R.layout.activity_lobby);

        Intent intent = getIntent();
        String nameuser=intent.getStringExtra("nameUser");

        binding.buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LobbyActivity.this, ConfigLobbyActivity.class);
                intent.putExtra("nameUser",nameuser);
                startActivity(intent);
            }
        });
        binding.buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LobbyActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
    }
}