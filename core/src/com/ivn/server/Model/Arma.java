package com.ivn.server.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.ivn.server.managers.ResourceManager.*;


public class Arma extends Sprite {
    public Personaje.Arma tipo;
    public Rectangle rect;

    public Arma(Vector2 pos){
        super.setPosition(pos.x,pos.y);

        int aleatorio = MathUtils.random(0,Personaje.Arma.values().length-1); // Genera un arma aleatoria

        rect = new Rectangle(pos.x,pos.y, rafagas.getWidth(),rafagas.getHeight());

        tipo = Personaje.Arma.values()[aleatorio]; // Ponemos ese arma

        if(tipo == Personaje.Arma.RAFAGA) // y su apariencia
            super.setTexture(rafagas);
        else if(tipo == Personaje.Arma.GUN)
            super.setTexture(gun);
        else if(tipo == Personaje.Arma.SHOTGUN)
            super.setTexture(shotgun);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}
