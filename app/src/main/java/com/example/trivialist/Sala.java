package com.example.trivialist;

import java.util.ArrayList;

public class Sala {
    private String codigoSala;
    private ArrayList<Jugador> jugadores;
    private boolean partidaEnCurso;

    public Sala(String codigoSala) {
        this.codigoSala = codigoSala;
        this.jugadores = new ArrayList<>();
        this.partidaEnCurso = false;
    }

    public String getCodigoSala() {
        return codigoSala;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public void agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
    }

    public boolean isPartidaEnCurso() {
        return partidaEnCurso;
    }

    public void setPartidaEnCurso(boolean partidaEnCurso) {
        this.partidaEnCurso = partidaEnCurso;
    }
}
