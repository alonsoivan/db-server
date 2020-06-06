package com.ivn.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.ivn.server.Model.Personaje;
import com.ivn.server.Register.*;
import com.ivn.server.managers.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;

import static com.ivn.server.ServerAplication.addPersonajes;
import static com.ivn.server.ServerAplication.personajes;
import static com.ivn.server.util.Constantes.TEXTURE_ATLAS_GAME_GUN;

public class Servidor {
    Server server;

    public Servidor() throws IOException {

        server = new Server();
        server.start();
        server.bind(54555, 54777);

        registrar(server.getKryo());

        server.addListener(new MiListener(server));
    }

    public static void registrar(Kryo kryo) {

        kryo.register(AddPersonajes.class);
        kryo.register(AddPersonaje.class);
        kryo.register(RemovePersonaje.class);
        kryo.register(UpdatePersonaje.class);
        kryo.register(Vector2.class);
        kryo.register(ArrayList.class);
        kryo.register(MovePersonaje.class);
        kryo.register(Disparar.class);
        kryo.register(AddDiamante.class);
        kryo.register(RemoveDiamante.class);
        kryo.register(Timer.class);
        kryo.register(AddArma.class);
        kryo.register(RemoveArma.class);
        kryo.register(Personaje.Arma.class);

    }

}
class MiListener extends Listener {
    Server server;

    public MiListener(Server server) {
        this.server = server;
    }

    public void connected (Connection connection) {
        System.out.println("Cliente con id: " + connection.getID() + " e IP:"+connection.getRemoteAddressTCP().getAddress() + " conectado.");

        AddPersonaje addPersonaje = new AddPersonaje();
        addPersonaje.id = connection.getID();

        connection.sendTCP(addPersonaje);  // Le mando su id para que se cree a si mismo
    }

    public void disconnected (Connection connection) {
        System.out.println("Cliente con id: " + connection.getID() + " desconectado.");

        //addPersonajes.idsTexturas.remove(addPersonajes.ids.get(connection.getID())-1);
        //addPersonajes.ids.remove(connection.getID());

        personajes.removeKey(connection.getID());

        server.sendToAllExceptUDP(connection.getID(),new RemovePersonaje(connection.getID()));
    }

    public void received (Connection connection, Object object) {

        if(object instanceof AddPersonaje){

            AddPersonaje addPersonaje = (AddPersonaje)object;

            personajes.put(connection.getID(),new Personaje(ResourceManager.getRegion(addPersonaje.idTexture,TEXTURE_ATLAS_GAME_GUN),addPersonaje.nombre));  // creo un pj con su id

            addPersonajes.ids.add(addPersonaje.id); // guardo su id
            addPersonajes.idsTexturas.add(addPersonaje.idTexture);
            addPersonajes.nombres.add(addPersonaje.nombre);
        }


        if (object instanceof MovePersonaje) {

            MovePersonaje movePersonaje = (MovePersonaje)object;

            personajes.get(connection.getID()).posicionCursor = movePersonaje.posicionCursor;
            personajes.get(connection.getID()).mover(movePersonaje.dir);
            personajes.get(connection.getID()).setRotation(movePersonaje.rotation);

        }

        if(object instanceof Disparar){
            personajes.get(connection.getID()).disparar();

            Disparar disparar = ((Disparar) object);
            disparar.id = connection.getID();
        }
    }
}
