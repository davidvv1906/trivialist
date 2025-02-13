package com.example.trivialist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.trivialist.databinding.ActivityQuestionBinding;

public class QuestionActivity extends AppCompatActivity {
    private static ActivityQuestionBinding binding;
    String respuestaCorrecta = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question);

        Intent intent = getIntent();
        String categoria = intent.getStringExtra("categoria");

        // Define las opciones de respuesta para cada categoría
        String[] opciones = new String[3];

        switch (categoria) {
            case "Matematicas":
                binding.Tittlequestion.setText(categoria);
                binding.questiondescript.setText("¿Cuál es el valor de π (pi) con dos decimales?");
                opciones = new String[]{"3.14", "2.71", "1.61"};
                respuestaCorrecta = "3.14";
                break;
            case "Geografia":
                binding.Tittlequestion.setText(categoria);
                binding.questiondescript.setText("¿Cuál es el río más largo del mundo?");
                opciones = new String[]{"El Amazonas", "El Nilo", "El Misisipi"};
                respuestaCorrecta = "El Amazonas";
                break;
            case "Arte":
                binding.Tittlequestion.setText(categoria);
                binding.questiondescript.setText("¿Quién pintó la famosa obra \"La Noche Estrellada\"?");
                opciones = new String[]{"Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci"};
                respuestaCorrecta = "Vincent van Gogh";
                break;
            case "Historia":
                binding.Tittlequestion.setText(categoria);
                binding.questiondescript.setText("¿Cuál fue el motivo principal que desencadenó la Primera Guerra Mundial en 1914?");
                opciones = new String[]{"El asesinato del archiduque Francisco Fernando de Austria", "La firma del Tratado de Versalles", "La Revolución Francesa"};
                respuestaCorrecta = "El asesinato del archiduque Francisco Fernando de Austria";
                break;
            case "Deportes":
                binding.Tittlequestion.setText(categoria);
                binding.questiondescript.setText("¿En qué deporte es conocida Serena Williams por haber ganado múltiples títulos de Grand Slam?");
                opciones = new String[]{"Tenis", "Baloncesto", "Natación"};
                respuestaCorrecta = "Tenis";
                break;
        }

        // Añade las opciones al RadioGroup
        for (String opcion : opciones) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opcion);
            binding.radioGroupQuestions.addView(radioButton);
        }

        binding.enterQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = binding.radioGroupQuestions.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // El usuario no ha marcado ninguna opción
                    Toast.makeText(QuestionActivity.this, "Por favor, marque una opción.", Toast.LENGTH_SHORT).show();
                } else {
                    // El usuario ha marcado una opción
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedText = selectedRadioButton.getText().toString();

                    if (selectedText.equals(respuestaCorrecta)) {
                        // Respuesta correcta
                        Toast.makeText(QuestionActivity.this, "¡Correcto!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Respuesta incorrecta
                        Toast.makeText(QuestionActivity.this, "¡Incorrecto!", Toast.LENGTH_SHORT).show();
                    }
                    // Volver a GameActivity
                    Intent intent = new Intent(QuestionActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
