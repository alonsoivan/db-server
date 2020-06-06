package com.ivn.server.util;

import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;
import com.ivn.server.Model.Arma;
import com.ivn.server.Model.Diamante;
import com.ivn.server.Model.Personaje;
import com.ivn.server.ServerAplication;

import java.util.ArrayList;

import static com.ivn.server.ServerAplication.personajes;

public class Util {
    public static Vector2 getPosInGameWorld(Vector2 pos){
        Vector3 aux = ServerAplication.camera.unproject(new Vector3(pos.x,pos.y, 0));
        return new Vector2(aux.x,aux.y);
    }

    public static Vector2 getDirection(Vector2 origen, Vector2 target){

        Vector2 direction = target.sub(origen);
        direction.nor();

        return direction;
    }

    public static double getDistance(Vector2 object1, Vector2 object2){
        //return Math.sqrt(Math.pow((object2.x - object1.x), 2) + Math.pow((object2.y - object1.y), 2));
        return Math.sqrt(Vector2.dst2(object1.x,object1.y,object2.x,object2.y));

    }


    public static ArrayList<String> getRanking(){

        // TODO MEJORAR ESTA SHIT, HACERLA MÁS EFICIENTE.
        // que se ejecute 1 vez cada s o asi, no todo el rato

        ArrayList<Personaje> aux = new ArrayList<>();

        ArrayMap.Keys<Integer> idsPersonajes = personajes.keys();

        while(idsPersonajes.hasNext()) {

            int id = idsPersonajes.next();
            aux.add(personajes.get(id));
        }

        aux.sort(new DiamondSorter());

        ArrayList<String> ranking = new ArrayList<>();


        for(Personaje personaje : aux)
            ranking.add(String.format("%"+ (personaje.nombre.length()-40) +"s %d", personaje.nombre ,personaje.getDiamantes()));

        return ranking;
    }


    public static class DiamondSorter implements Comparator<Personaje>{

        @Override
        public int compare(Personaje personaje1, Personaje personaje2) {
            return personaje1.getDiamantes() > personaje2.getDiamantes() ? -1 : 1;
        }
    }

    public static Vector2 getCoordenadasLibresArmas(ArrayList<Vector2> puntos, ArrayMap<Integer, Arma> objetos){

        int cont = 0;
        boolean libre = false;

        while(cont < puntos.size() && !libre){

            ArrayMap.Keys<Integer> ids = objetos.keys();

            libre = true;
            while(ids.hasNext() && libre) {

                int id = ids.next();

                if (objetos.get(id).rect.contains(puntos.get(cont)))
                    libre = false;
            }

            cont++;
        }

        if(libre)
            return puntos.get(cont-1);
        else
            return null;
    }

}
