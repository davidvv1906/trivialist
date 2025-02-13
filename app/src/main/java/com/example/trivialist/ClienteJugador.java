package com.example.trivialist;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteJugador {

    private final String HOST = "192.168.181.155";  // Cambia esto si el servidor está en otra máquina
    private final int PUERTO = 5555;

    public String ejecutarJugador(String nombre, String partida, String codigoSala) {
        StringBuilder respuestaBuilder = new StringBuilder();
        try (Socket socket = new Socket(HOST, PUERTO);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Conectado al servidor.");
            out.writeUTF(partida);

            if (partida.equals("crear_sala")) {
                out.writeUTF(nombre);
                String respuesta = in.readUTF();
                respuestaBuilder.append(respuesta);
                System.out.println(respuesta);
            } else if (partida.equals("unir_sala")) {
                out.writeUTF(nombre);
                out.writeUTF(codigoSala);
                String respuesta = in.readUTF();
                respuestaBuilder.append(respuesta);
                System.out.println(respuesta);
            }

            while (true) {
                String mensajeServidor = in.readUTF();
                respuestaBuilder.append("\n").append(mensajeServidor);
                System.out.println(mensajeServidor);

                if (mensajeServidor.contains("ganado")) {
                    break;
                }
            }

        } catch (IOException e) {
            respuestaBuilder.append("Error en el jugador: ").append(e.getMessage());
            e.printStackTrace();
        }
        return respuestaBuilder.toString();
    }
}
