package com.ivn.server.Register;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class UpdatePersonaje {
    public int id;
    public Vector2 pos;
    public float rotation;
    public ArrayList<Vector2> posBalas;
    public ArrayList<Float> rotationBalas;
    public int vida;
    public float tamañoLaser;
    public int diamantes;

    public UpdatePersonaje(){}

    public UpdatePersonaje(int id, Vector2 pos, int vida){
        this.id = id;
        this.pos = pos;
        posBalas = new ArrayList<>();
        rotationBalas = new ArrayList<>();
        this.vida = vida;
    }
}
