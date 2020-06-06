package com.ivn.server.Register;

import com.badlogic.gdx.math.Vector2;

public class MovePersonaje {
    public int id;
    public Vector2 dir;
    public float rotation;
    public Vector2 posicionCursor;

    public MovePersonaje(){}

    public MovePersonaje(int id){
        this.id = id;
    }
}