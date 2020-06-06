package com.ivn.server.Model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.ivn.server.managers.ResourceManager;

import static com.ivn.server.util.Constantes.BULLET_SPEED;
import static com.ivn.server.util.Constantes.BULLET_RANGE;

public class Bala extends Sprite {
    public Rectangle rect;
    public Vector2 direction;

    public long momentoDisparo;
    public long tiempoVida;
    public long tiempoVidaMaximo = BULLET_RANGE;

    public Bala( Vector2 position,  Vector2 target, Float rotation){
        super((Texture) ResourceManager.assets.get("balas/bala.png"));
        super.setPosition(position.x,position.y);
        super.setRotation(rotation);
        this.rect = new Rectangle(position.x, position.y, super.getTexture().getWidth(), super.getTexture().getHeight());
        getDirectionBala(target);

        momentoDisparo = TimeUtils.millis();
    }

    public void getDirectionBala(Vector2 target){

        direction = target.cpy().sub(new Vector2(super.getX(),super.getY()));
        direction.nor();

        direction.scl(BULLET_SPEED);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        tiempoVida =  TimeUtils.millis() - momentoDisparo;
    }

    public boolean isTimeOver(){
        return tiempoVida >= tiempoVidaMaximo ? true : false;
    }

    public void mover(){
        Vector2 pos = new Vector2(super.getX(),super.getY()).add(direction);
        super.setPosition(pos.x,pos.y);
        rect.setPosition(pos);
    }
}
