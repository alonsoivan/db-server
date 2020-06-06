package com.ivn.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Timer;
import com.ivn.server.Model.Arma;
import com.ivn.server.Model.Bala;
import com.ivn.server.Model.Diamante;
import com.ivn.server.Model.Personaje;
import com.ivn.server.Register.*;
import com.ivn.server.managers.MyTimer;
import com.ivn.server.managers.ResourceManager;
import com.ivn.server.util.Util;

import java.io.IOException;
import java.util.ArrayList;

import static com.ivn.server.util.Constantes.*;
import static com.ivn.server.util.Util.getDirection;
import static com.ivn.server.util.Util.getDistance;

public class ServerAplication extends ApplicationAdapter {

	public boolean gameStarted = false;

	private Servidor servidor;

	public static ArrayMap<Integer, Personaje> personajes;

	// DIAMANTES
	public static ArrayMap<Integer, Diamante> diamantes;
	public static ArrayList<Vector2> coordenadasDiamantes;
	public static int idDiamantes;

	// ARMAS
	public static ArrayMap<Integer, Arma>  armas;
	public static ArrayList<Vector2> coordenadasArmas;
	public static int idArmas;

	public static AddPersonajes addPersonajes = new AddPersonajes();

	// TiledMap
	private Batch batch;
	public static OrthographicCamera camera;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	public static Array<Rectangle> paredes;


	// TIMER
	MyTimer myTimer;
	public Batch batchTimer;
	public BitmapFont font;

	@Override
	public void create () {
		ResourceManager.loadAllResources();


		// Timer
		myTimer = new MyTimer(5,0);
		batchTimer  = new SpriteBatch();
		font = new BitmapFont();


		personajes = new ArrayMap<>();

		// DIAMANTES
		diamantes = new ArrayMap<>();
		coordenadasDiamantes = new ArrayList<>();

		// ARMAS
		armas = new ArrayMap<>();
		coordenadasArmas = new ArrayList<>();


		// Tiledmap
		camera = new OrthographicCamera();
		camera.setToOrtho(false, TILES_IN_CAMERA_WIDTH * TILE_WIDTH, TILES_IN_CAMERA_HEIGHT * TILE_WIDTH);
		camera.update();

		map = new TmxMapLoader().load("levels/pruebita2.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1);
		batch = renderer.getBatch();

		try {
			servidor = new Servidor();
		} catch (IOException e) {
			e.printStackTrace();
		}

		paredes = new Array<>();
		getParedes();
		getCoordenadasAparicionDiamantes();
		getCoordenadasAparicionArmas();
	}

	private void getParedes(){
		MapLayer collisionsLayer = map.getLayers().get("colisiones");

		for (MapObject object : collisionsLayer.getObjects())
			paredes.add(((RectangleMapObject) object).getRectangle());

	}

	private void getCoordenadasAparicionArmas(){
		MapLayer collisionsLayer = map.getLayers().get("armas");

		for (MapObject object : collisionsLayer.getObjects())
			coordenadasArmas.add(new Vector2(((RectangleMapObject) object).getRectangle().x,((RectangleMapObject) object).getRectangle().y));

	}

	private void getCoordenadasAparicionDiamantes(){
		MapLayer collisionsLayer = map.getLayers().get("diamantes");

		for (MapObject object : collisionsLayer.getObjects())
			coordenadasDiamantes.add(new Vector2(((RectangleMapObject) object).getRectangle().x,((RectangleMapObject) object).getRectangle().y));

	}

	private void generarDiamantes(){

		Timer.schedule(new Timer.Task() {
			public void run() {
				int aleatorio = MathUtils.random(0,coordenadasDiamantes.size()-1);
				Vector2 coordenadas = coordenadasDiamantes.get(aleatorio);

				// TODO COMPROBAR QUE NO HAYA YA UN DIAMANTE EN ESE PUNTO


				// Lo creo en el server
				diamantes.put(idDiamantes,new Diamante(coordenadas));


				// Digo a los clientes que lo creen
				servidor.server.sendToAllTCP(new AddDiamante(idDiamantes,coordenadas));

				idDiamantes++;

			}
		}, 0.5f,15,20);
	}

	private void generarArmas(){

		Timer.schedule(new Timer.Task() {
			public void run() {
				//int aleatorio = MathUtils.random(0,coordenadasArmas.size()-1);
				//Vector2 coordenadas = coordenadasArmas.get(aleatorio);

				// TODO COMPROBAR QUE NO HAYA YA UN ARMA EN ESE PUNTO

				// APROX
				Vector2 coordenadas = Util.getCoordenadasLibresArmas(coordenadasArmas,armas);
				if(coordenadas != null) {

					Arma arma = new Arma(coordenadas);

					// Lo creo en el server
					armas.put(idArmas, arma);


					// Digo a los clientes que lo creen
					servidor.server.sendToAllTCP(new AddArma(idArmas, coordenadas, arma.tipo));

					idArmas++;
				}
			}
		}, 0.5f,5,20);
	}


	@Override
	public void render () {

		if(ResourceManager.update()) {
			actualizar();
			pintar();
			handleInput();
		}
	}

	private void handleInput(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {

			// HACE COMENZAR LA PARTIDA
			servidor.server.sendToAllTCP(addPersonajes);

			// PRUEBA TIMER
			myTimer.start();

			generarDiamantes();
			generarArmas();

			gameStarted = true;
		}
	}

	private void handleCamera() {
		// These values likely need to be scaled according to your world coordinates.

		// The top boundary of the map (y + height)
		float mapTop = 0 + TILES_IN_CAMERA_HEIGHT * TILE_WIDTH * 4f;

		// The bottom boundary of the map (y)
		int mapBottom = 0;

		// The left boundary of the map (x)
		int mapLeft = 0;

		// The right boundary of the map (x + width)
		float mapRight = TILES_IN_CAMERA_WIDTH * TILE_WIDTH * 4f ;

		// The camera dimensions, halved
		float cameraHalfWidth = camera.viewportWidth * .5f;
		float cameraHalfHeight = camera.viewportHeight * .5f;

		// Move camera after player as normal

		float cameraLeft = camera.position.x - cameraHalfWidth;
		float cameraRight = camera.position.x + cameraHalfWidth;
		float cameraBottom = camera.position.y - cameraHalfHeight;
		float cameraTop = camera.position.y + cameraHalfHeight;

		// Horizontal axis
		if(mapRight < camera.viewportWidth)
		{
			camera.position.x = mapRight / 2;
		}
		else if(cameraLeft <= mapLeft)
		{
			camera.position.x = mapLeft + cameraHalfWidth;
		}
		else if(cameraRight >= mapRight)
		{
			camera.position.x = mapRight - cameraHalfWidth;
		}


		// Vertical axis
		if(mapTop < camera.viewportHeight)
		{
			camera.position.y = mapTop / 2;
		}
		else if(cameraBottom <= mapBottom)
		{
			camera.position.y = mapBottom + cameraHalfHeight;
		}
		else if(cameraTop >= mapTop)
		{
			camera.position.y = mapTop - cameraHalfHeight;
		}


		camera.update();
		renderer.setView(camera);
	}

	public void pintar(){
		handleCamera();

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


		renderer.render();


		// PRUEBAS TIMER
		if(!myTimer.isFinished)
			myTimer.drawTimer(batchTimer,font);


		batch.begin();


		System.out.println("pj:"+personajes.size);
		System.out.println("armas:"+armas.size);


		// PINTAR PERSONAJES
		ArrayMap.Keys<Integer> idsPersonajes = personajes.keys();

		while(idsPersonajes.hasNext()) {

			int id = idsPersonajes.next();

			personajes.get(id).draw(batch);

			UpdatePersonaje updatePersonaje = new UpdatePersonaje(id,new Vector2(personajes.get(id).getX(),personajes.get(id).getY()), personajes.get(id).vida);
			updatePersonaje.rotation = personajes.get(id).getRotation();
			updatePersonaje.tamañoLaser = (float) personajes.get(id).tamañoLaser;
			updatePersonaje.diamantes = personajes.get(id).diamantes;

			for(Bala bala : personajes.get(id).balas) {
				updatePersonaje.posBalas.add(new Vector2(bala.getX(), bala.getY()));
				updatePersonaje.rotationBalas.add(bala.getRotation());
			}

			servidor.server.sendToAllTCP(updatePersonaje);
		}

		// PINTAR DIAMANTES

		ArrayMap.Keys<Integer> idsDiamantes = diamantes.keys();

		while(idsDiamantes.hasNext()) {

			int id = idsDiamantes.next();

			diamantes.get(id).draw(batch);
		}

		servidor.server.sendToAllTCP(new com.ivn.server.Register.Timer(myTimer.toString()));

		batch.end();
	}

	public void actualizar(){
		moverBalas();
		comprobarcolisiones();

		if(gameStarted)
			servidor.server.sendToAllTCP(Util.getRanking());
	}

	public void moverBalas(){
		ArrayMap.Keys<Integer> ids = personajes.keys();
		while(ids.hasNext()) {
			int id = ids.next();
				for(Bala bala : personajes.get(id).balas)
					bala.mover();
		}
	}

	public void comprobarcolisiones(){

		ArrayList<Integer> ids2 = new ArrayList<>();
		ArrayMap.Keys<Integer> ids = personajes.keys();
		while(ids.hasNext()) {
			ids2.add(ids.next());
		}

		// TODO NESTED ITERATOR , optimizar el calculo de colisiones
		// Exception in thread "LWJGL Application" com.badlogic.gdx.utils.GdxRuntimeException: #iterator() cannot be used nested. COLISIONES BALAS - PAREDES
		for(int id : ids2){
			int id3 = id;
			for(Bala bala1 : personajes.get(id).balas) {

				// Tiempo bala
				if(bala1.isTimeOver())
					personajes.get(id).balas.removeValue(bala1, true);


				try {
					// Colisiones balas - paredes
					for (Rectangle pared : paredes)  //////////////////////////
						if (pared.overlaps(bala1.rect))
							personajes.get(id).balas.removeValue(bala1, true);
				}catch (Exception e){System.out.println("!!!!!!!!!!!!!!!!!!ERROR!!!!!!!!!!!!!!!!!!!");}

				// Colisiones balas - lados sup e inferior
				if (bala1.rect.getX() < 0 || bala1.rect.getX() > TILES_IN_CAMERA_WIDTH*TILE_WIDTH) {
					if(personajes.get(id).balas.indexOf(bala1, true) >= 0) {
						personajes.get(id).balas.removeValue(bala1, true);
					}
				}


				// Colisiones balas - lados izq y derecho
				if (bala1.rect.getY() < 0 || bala1.rect.getY() > TILES_IN_CAMERA_HEIGHT*TILE_WIDTH){
					if(personajes.get(id).balas.indexOf(bala1, true) >= 0) {
						personajes.get(id).balas.removeValue(bala1, true);
					}
				}

				// Colisiones balas - otros pjs
				for(int id2 : ids2){
					if(id3 != id2 && bala1.rect.overlaps(personajes.get(id2).rect)) {
						if(personajes.get(id3).balas.indexOf(bala1, true) >= 0) {
							personajes.get(id).balas.removeValue(bala1, true);
							// TODO QUITAR VIDA BIEN
							personajes.get(id2).vida--;
						}
					}
				}
			}

			// COLISIONES PERSONAJE - DIAMANTES

			ArrayMap.Keys<Integer> idsDiamantes = diamantes.keys();
			while(idsDiamantes.hasNext()) {
				int idDiamante = idsDiamantes.next();
				if(diamantes.get(idDiamante).rect.overlaps(personajes.get(id).rect)){

					personajes.get(id).diamantes += diamantes.get(idDiamante).cantidad;

					diamantes.removeKey(idDiamante);

					servidor.server.sendToAllTCP(new RemoveDiamante(idDiamante));

				}

			}

			// COLISIONES PERSONAJE - ARMAS

			ArrayMap.Keys<Integer> idsArmas = armas.keys();
			while(idsArmas.hasNext()) {
				int idArma = idsArmas.next();
				if(armas.get(idArma).rect.overlaps(personajes.get(id).rect)){

					personajes.get(id).arma = armas.get(idArma).tipo;

					armas.removeKey(idArma);

					servidor.server.sendToAllTCP(new RemoveArma(idArma));
				}

			}



			// COLISIONES LASER

			if(personajes.get(id).posicionCursor != null) {

				Vector2 posOriginal = new Vector2(personajes.get(id).getX(), personajes.get(id).getY());

				boolean encontrado = false;
				int j = 0;
				double distancia = 800;
				while (j < paredes.size -1 ) {

					Vector2 pos = posOriginal.cpy();
					Vector2 dir = getDirection(pos.cpy(), personajes.get(id).posicionCursor.cpy());

					encontrado = false;
					while (getDistance(posOriginal.cpy(), pos.cpy()) < 800 && !encontrado) {
						if (paredes.get(j).contains(pos.cpy())) {

							if(getDistance(posOriginal.cpy(), pos.cpy()) < distancia)
								distancia = getDistance(posOriginal.cpy(), pos.cpy());


							encontrado = true;

						}
						else{
							pos.add(dir);
						}
					}

					personajes.get(id).tamañoLaser = distancia;

					j++;
				}

			}

		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();

		ArrayMap.Keys<Integer> idsPersonajes = personajes.keys();

		while(idsPersonajes.hasNext()) {

			int id = idsPersonajes.next();
			personajes.get(id).getTexture().dispose();
		}
	}
}
