package com.example.trivialist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.trivialist.databinding.ActivityGameBinding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static ActivityGameBinding binding;
    private String codigoSala;
    private String nombreJugador;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private int numjugadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game);

        // Agrega un ImageView en coordenadas específicas x e y
        addImageAtPosition();

        Intent intent = getIntent();
        numjugadores = intent.getIntExtra("numJugadores", 2);
        nombreJugador = intent.getStringExtra("nameUser");
        Toast.makeText(this, nombreJugador, Toast.LENGTH_SHORT).show();

        codigoSala = intent.getStringExtra("codigoSala");
        Toast.makeText(this, codigoSala, Toast.LENGTH_SHORT).show();
        binding.codeRoom.setText(codigoSala);

        GridLayout gridLayout = binding.marcador;
        for (int i = 0; i < numjugadores; i++) {
            TextView textView = new TextView(this);
            textView.setText("Jugador " + (i + 1));

            int height = dpToPx(this, 40);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    GridLayout.spec(i + 1),
                    GridLayout.spec(0)
            );
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = height;

            textView.setLayoutParams(params);
            gridLayout.addView(textView);

            for (int j = 1; j < 6; j++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.drawable.ic_launcher_foreground);

                GridLayout.LayoutParams imageParams = new GridLayout.LayoutParams(
                        GridLayout.spec(i + 1),
                        GridLayout.spec(j)
                );
                imageParams.width = dpToPx(this, 40);
                imageParams.height = height;

                imageView.setLayoutParams(imageParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                gridLayout.addView(imageView);
            }
        }

        binding.imageDado.setImageResource(R.drawable.dice_1);
        binding.buttonDado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollDice(binding.imageDado);
            }
        });

        conectarAlServidor();
    }

    private void conectarAlServidor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("your_server_ip", 5555); // Cambia "your_server_ip" por la IP de tu servidor
                    out = new DataOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());

                    // Enviar acción para unirse a la sala
                    out.writeUTF("unir_sala");
                    out.writeUTF(nombreJugador);
                    out.writeUTF(codigoSala);

                    // Leer la respuesta del servidor
                    final String respuesta = in.readUTF();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GameActivity.this, respuesta, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void rollDice(final ImageView imageDado) {
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imageDado, "rotation", 0f, 360f);
        rotate.setDuration(700);
        rotate.start();
        rotate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setRandomDiceImage(imageDado);
            }
        });
    }

    private void setRandomDiceImage(final ImageView imageDado) {
        Random random = new Random();
        int randomNumber = random.nextInt(5) + 1;

        int[] posicion = getPosition(binding.ficha1);
        int posicionx = posicion[0];
        int posiciony = posicion[1];

        // Ajusta la posición de la ficha en base al número del dado
        switch (randomNumber) {
            case 1:
                imageDado.setImageResource(R.drawable.dice_1);
                movefichaAnimated(posicionx, posiciony, 169);
                break;
            case 2:
                imageDado.setImageResource(R.drawable.dice_2);
                movefichaAnimated(posicionx, posiciony, 339);
                break;
            case 3:
                imageDado.setImageResource(R.drawable.dice_3);
                movefichaAnimated(posicionx, posiciony, 509);
                break;
            case 4:
                imageDado.setImageResource(R.drawable.dice_4);
                movefichaAnimated(posicionx, posiciony, 679);
                break;
            case 5:
                imageDado.setImageResource(R.drawable.dice_5);
                movefichaAnimated(posicionx, posiciony, 849);
                break;
        }

        // Envía los puntos al servidor
        enviarPuntosAlServidor(randomNumber);

        // Retrasa la apertura de la nueva actividad 2 segundos
        final Intent intent = new Intent(GameActivity.this, QuestionActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (randomNumber) {
                    case 1:
                        intent.putExtra("categoria", "Matematicas");
                        break;
                    case 2:
                        intent.putExtra("categoria", "Geografia");
                        break;
                    case 3:
                        intent.putExtra("categoria", "Arte");
                        break;
                    case 4:
                        intent.putExtra("categoria", "Deportes");
                        break;
                    case 5:
                        intent.putExtra("categoria", "Historia");
                        break;
                }
                startActivity(intent);
            }
        }, 1500); // 1500 milisegundos = 1.5 segundos
    }

    private void enviarPuntosAlServidor(int puntos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.writeInt(puntos);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void movefichaAnimated(int startX, int startY, int endX) {
        ImageView ficha1 = binding.ficha1;

        // Animación para la coordenada X
        ObjectAnimator animX = ObjectAnimator.ofFloat(ficha1, "translationX", startX, endX);
        animX.setDuration(700); // Duración de la animación en milisegundos

        // Inicia la animación en X, manteniendo Y constante
        animX.start();
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    private void addImageAtPosition() {
        FrameLayout fichas = findViewById(R.id.fichas);

        // Encuentra la ImageView existente en el XML
        ImageView ficha1 = binding.ficha1;
        ImageView ficha2 = binding.ficha2;
        ImageView ficha3 = binding.ficha3;
        ImageView ficha4 = binding.ficha4;

        // Ajusta la posición de la ImageView
        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) ficha1.getLayoutParams();
        params1.leftMargin = -1;
        params1.topMargin = -1;
        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) ficha2.getLayoutParams();
        params2.leftMargin = 0;
        params2.topMargin = 98;
        FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) ficha3.getLayoutParams();
        params3.leftMargin = 96;
        params3.topMargin = 0;
        FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) ficha4.getLayoutParams();
        params4.leftMargin = 96;
        params4.topMargin = 96;

        ficha1.setLayoutParams(params1);
        ficha2.setLayoutParams(params2);
        ficha3.setLayoutParams(params3);
        ficha4.setLayoutParams(params4);
    }

    private int[] getPosition(ImageView imageView) {
        int[] location = new int[2];
        imageView.getLocationOnScreen(location);
        // También puedes usar getLocationInWindow(location) si prefieres obtener las coordenadas dentro de la ventana
        int x = location[0];
        int y = location[1];
        return location;
    }
}
