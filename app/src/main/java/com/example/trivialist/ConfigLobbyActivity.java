package com.example.trivialist;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.trivialist.databinding.ActivityConfigLobbyBinding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConfigLobbyActivity extends AppCompatActivity {

    private static final String TAG = "ConfigLobbyActivity";
    private static final String SERVER_IP = "192.168.181.155"; // Cambia esto por la IP de tu servidor
    private static final int SERVER_PORT = 5555;

    private ActivityConfigLobbyBinding binding;
    private String nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config_lobby);
        Intent intent = getIntent();
        nameUser = intent.getStringExtra("nameUser");

        setupButton();
        setupJoinButton();
    }

    private void setupButton() {
        binding.buttonInit.setOnClickListener(view -> {
            Log.d(TAG, "Botón Crear Sala presionado");
            new Thread(() -> {
                String response = crearSala(nameUser);
                runOnUiThread(() -> {
                    Log.d(TAG, response);
                    if (response.startsWith("Sala creada. Código: ")) {
                        String codigoSala = response.substring("Sala creada. Código: ".length());
                        showDialogWithRoomCode(codigoSala);

                        // Iniciar GameActivity después de crear la sala
                        Intent intent = new Intent(ConfigLobbyActivity.this, GameActivity.class);
                        intent.putExtra("nameUser", nameUser);
                        intent.putExtra("codigoSala", codigoSala);
                        startActivity(intent);
                    }
                });
            }).start();
        });
    }

    private void showDialogWithRoomCode(String codigoSala) {
        new AlertDialog.Builder(this)
                .setTitle("Sala Creada")
                .setMessage("Código de Sala: " + codigoSala + "\nComparte este código con tus amigos.")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // Copiar el código de la sala al portapapeles
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Código de Sala", codigoSala);
                    clipboard.setPrimaryClip(clip);
                    Log.d(TAG, "Código copiado al portapapeles");
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void setupJoinButton() {
        binding.buttonJoinRoom.setOnClickListener(view -> {
            EditText codeInput = binding.editTextRoomCode;
            String roomCode = codeInput.getText().toString();

            if (roomCode.isEmpty()) {
                Log.d(TAG, "Por favor, introduce un código de sala válido.");
                return;
            } else {
                new Thread(() -> {
                    String response = unirSala(nameUser, roomCode);
                    runOnUiThread(() -> {
                        Log.d(TAG, response);
                        if (response.startsWith("Unido a la sala con código: ")) {
                            Intent intent = new Intent(ConfigLobbyActivity.this, GameActivity.class);
                            intent.putExtra("nameUser", nameUser);
                            intent.putExtra("codigoSala", roomCode);
                            startActivity(intent);
                        }
                    });
                }).start();
            }
        });
    }

    private String crearSala(String nombreJugador) {
        Log.d(TAG, "Intentando crear sala...");
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            Log.d(TAG, "Conectado al servidor...");

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            Log.d(TAG, "Enviando acción 'crear_sala'...");
            out.writeUTF("crear_sala");
            out.writeUTF(nombreJugador);

            Log.d(TAG, "Esperando respuesta del servidor...");
            String response = in.readUTF();
            Log.d(TAG, "Respuesta del servidor: " + response);
            return response;
        } catch (IOException e) {
            Log.e(TAG, "Error al crear sala: " + e.getMessage(), e);
            runOnUiThread(() -> {
                // Muestra un mensaje de error al usuario
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Error al crear sala: " + e.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            });
            return "Error al crear sala: " + e.getMessage();
        }
    }

    private String unirSala(String nombreJugador, String codigoSala) {
        Log.d(TAG, "Intentando unirse a la sala...");
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            out.writeUTF("unir_sala");
            out.writeUTF(nombreJugador);
            out.writeUTF(codigoSala);

            Log.d(TAG, "Esperando respuesta del servidor...");
            String response = in.readUTF();
            Log.d(TAG, "Respuesta del servidor: " + response);
            return response;
        } catch (IOException e) {
            Log.e(TAG, "Error al unirse a la sala: " + e.getMessage(), e);
            runOnUiThread(() -> {
                // Muestra un mensaje de error al usuario
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Error al unirse a la sala: " + e.getMessage())
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            });
            return "Error al unirse a la sala: " + e.getMessage();
        }
    }
}
