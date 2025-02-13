package com.example.trivialist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Servidor {

    private final int PUERTO = 5555;
    private final int NUM_MAX_JUGADORES = 4;
    private final int NUM_MIN_JUGADORES = 4; // Agregado para indicar el número mínimo de jugadores
    private final int PUNTOS_GANADOR = 5;
    private static final String TAG = "Servidor";

    private Map<String, Sala> salas;
    private boolean partidaEnCurso;

    public Servidor() {
        salas = new HashMap<>();
        partidaEnCurso = false;
    }

    public void ejecutarServidor() {
        System.out.println(TAG + ": Servidor iniciado. Escuchando en el puerto " + PUERTO + "...");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(TAG + ": ¡Nuevo jugador conectado! " + socket.getInetAddress());

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String accion = in.readUTF();

                if (accion.equals("crear_sala")) {
                    String nombreJugador = in.readUTF();
                    String codigoSala = generarCodigoSala();
                    Sala sala = new Sala(codigoSala);
                    salas.put(codigoSala, sala);
                    sala.agregarJugador(new Jugador(nombreJugador, socket, 0));
                    out.writeUTF("Sala creada. Código: " + codigoSala);
                    System.out.println(TAG + ": Sala creada con código " + codigoSala);
                    System.out.println(TAG + ": Jugador " + nombreJugador + " conectado.");
                } else if (accion.equals("unir_sala")) {
                    String nombreJugador = in.readUTF();
                    String codigoSala = in.readUTF();
                    Sala sala = salas.get(codigoSala);
                    if (sala != null && sala.getJugadores().size() < NUM_MAX_JUGADORES) {
                        sala.agregarJugador(new Jugador(nombreJugador, socket, 0));
                        out.writeUTF("Unido a la sala con código " + codigoSala);
                        System.out.println(TAG + ": Jugador unido a la sala con código " + codigoSala);
                        System.out.println(TAG + ": Jugador " + nombreJugador + " conectado.");

                        if (sala.getJugadores().size() == NUM_MAX_JUGADORES && !sala.isPartidaEnCurso()) {
                            new Thread(() -> iniciarPartida(sala)).start();
                        }
                    } else {
                        out.writeUTF("No se pudo unir a la sala. Sala llena o inexistente.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(TAG + ": Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generarCodigoSala() {
        Random rand = new Random();
        int codigo = rand.nextInt(90000) + 10000; // Número aleatorio de 5 dígitos (10000-99999)
        return String.valueOf(codigo);
    }


    private void iniciarPartida(Sala sala) {
        sala.setPartidaEnCurso(true);
        System.out.println(TAG + ": Partida iniciada en la sala " + sala.getCodigoSala());

        while (sala.isPartidaEnCurso()) {
            for (Jugador jugador : sala.getJugadores()) {
                try {
                    DataInputStream in = new DataInputStream(jugador.getSocket().getInputStream());
                    DataOutputStream out = new DataOutputStream(jugador.getSocket().getOutputStream());

                    int puntos = in.readInt();
                    jugador.setPuntos(jugador.getPuntos() + puntos);
                    if (jugador.getPuntos() >= PUNTOS_GANADOR) {
                        out.writeUTF("¡Felicidades! Has ganado la partida.");
                        terminarPartida(sala);
                        break;
                    } else {
                        out.writeUTF("Tienes " + jugador.getPuntos() + " puntos.");
                    }
                } catch (IOException e) {
                    System.err.println(TAG + ": Error en la comunicación con el jugador: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void terminarPartida(Sala sala) {
        sala.setPartidaEnCurso(false);
        System.out.println(TAG + ": La partida en la sala " + sala.getCodigoSala() + " ha terminado.");
        // Aquí puedes manejar la limpieza y reiniciar el estado del juego si es necesario
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.ejecutarServidor();
    }
}
