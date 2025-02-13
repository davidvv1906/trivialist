package com.example.trivialist;

import java.net.Socket;

public class Jugador {
    private String nombre;
    private Socket socket;
    private int puntos;

    public Jugador(String nombre, Socket socket, int puntos) {
        this.nombre = nombre;
        this.socket = socket;
        this.puntos = puntos;
    }

    public String getNombre() {
        return nombre;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }
}
