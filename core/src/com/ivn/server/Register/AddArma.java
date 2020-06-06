package com.ivn.server.Register;

import com.badlogic.gdx.math.Vector2;
import com.ivn.server.Model.Personaje;

public class AddArma {
    public int id;
    public Vector2 pos;
    public Personaje.Arma tipo;

    public AddArma(){}

    public AddArma(int id, Vector2 pos, Personaje.Arma tipo){
        this.id = id;
        this.pos = pos;
        this.tipo = tipo;
    }
}