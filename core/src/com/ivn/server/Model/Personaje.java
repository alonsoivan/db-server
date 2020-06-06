package com.ivn.server.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

import static com.ivn.server.ServerAplication.paredes;
import static com.ivn.server.util.Constantes.CHARACTER_SPEED;
import static com.ivn.server.managers.ResourceManager.nombrePantalla;

public class Personaje extends Sprite {

    public enum Arma{
        GUN, SHOTGUN, RAFAGA
    }

    public Arma arma;

    public Rectangle rect;

    public int vida;
    public Array<Bala> balas;
    public String nombre;

    public int diamantes;

    private int width;
    private int height;

    public double tamañoLaser;
    public Vector2 posicionCursor;

    public Personaje(TextureRegion texture, String nombre){
        super(texture);
        super.setPosition(0,0);

        width = texture.getRegionWidth();
        height = texture.getRegionHeight();

        super.setSize(width,height);
        this.rect = new Rectangle(getX(), getY(), width, height);

        balas = new Array<>();
        this.nombre = nombre;
        vida = 100;


        arma = Arma.GUN;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        nombrePantalla.draw(batch,nombre,super.getX(),super.getY());

        for (Bala bala : balas)
            bala.draw(batch);
    }

    public void disparar(){
        // TODO VARIOS DISPAROS.  DISPARO PISTOLA, DISPARO ESCOPETA, ETC
        // TODO ARMA AUTOMATICA ETC

        switch (arma){
            case GUN:

                balas.add(new Bala(new Vector2(getX(),getY()),posicionCursor,super.getRotation()));

                break;

            case SHOTGUN:


                int balasADisparar = MathUtils.random(6,9)+1;

                for(int i = 0; i < balasADisparar ; i++){
                    Vector2 cursor = new Vector2(MathUtils.random(posicionCursor.x-20,posicionCursor.x+20), MathUtils.random(posicionCursor.y-20,posicionCursor.y+20));
                    balas.add(new Bala(new Vector2(getX(), getY()),cursor,getRotation()));
                }

                break;

            case RAFAGA:

                Timer.schedule(new Timer.Task() {
                    public void run() {
                        balas.add(new Bala(new Vector2(getX(),getY()),posicionCursor,getRotation()));
                    }
                }, 0,0.09f,2);

                break;
            default:
                break;
        }
    }

    public void mover(Vector2 dir){

        // TODO ARREGLAR COLISIONES, SE QUEDA PILLADO EN ALGUNAS ESQUINAS.

        for(Rectangle pared : paredes){
            if(pared.overlaps(rect)){

                // colision izq
                if(pared.contains(new Vector2(getX(),getY())) || pared.contains(new Vector2(getX(),getY() + height))){
                    //if(dir.x < 0)
                        dir.x = 1;
                }else

                // colision der
                if(pared.contains(new Vector2(getX() + width,getY())) || pared.contains(new Vector2(getX() + width,getY() + height))){
                    //if(dir.x > 0)
                        dir.x = -1;
                }

                // colision arriba
                if(pared.contains(new Vector2(getX() ,getY() + height)) || pared.contains(new Vector2(getX() + width,getY() + height))){
                    //if(dir.y > 0)
                        dir.y = -1;
                }else

                // colision abajo
                if(pared.contains(new Vector2(getX() ,getY())) || pared.contains(new Vector2(getX() + width ,getY())) ){
                    //if(dir.y < 0)
                        dir.y = 1;
                }

            }

        }

        float movimiento = Gdx.graphics.getDeltaTime()*CHARACTER_SPEED;

        System.out.println("MOVIER : "+movimiento);

        Vector2 pos = new Vector2(super.getX(),super.getY()).add(dir.scl(movimiento));
        super.setPosition(pos.x,pos.y);
        rect.setPosition(pos);
    }


    public Arma getArma() {
        return arma;
    }

    public void setArma(Arma arma) {
        this.arma = arma;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public Array<Bala> getBalas() {
        return balas;
    }

    public void setBalas(Array<Bala> balas) {
        this.balas = balas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDiamantes() {
        return diamantes;
    }

    public void setDiamantes(int diamantes) {
        this.diamantes = diamantes;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getTamañoLaser() {
        return tamañoLaser;
    }

    public void setTamañoLaser(double tamañoLaser) {
        this.tamañoLaser = tamañoLaser;
    }

    public Vector2 getPosicionCursor() {
        return posicionCursor;
    }

    public void setPosicionCursor(Vector2 posicionCursor) {
        this.posicionCursor = posicionCursor;
    }
}
