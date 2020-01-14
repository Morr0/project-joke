package com.rami.projectjoke;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.rami.projectjoke.core.*;

import java.util.ArrayList;

public class Game extends ApplicationAdapter implements ContactListener, InputProcessor {
	private PlayerTriangle currentPlayer;
	private ArrayList<TrianglePair> trianglePairs;

	private OrthographicCamera camera;
	private Box2DDebugRenderer physRenderer;

	private World world;

	private Spawner spawner;

	private Texture triangle;
	private SpriteBatch batch;

	// To be deleted
	TrianglePair toBeDeletedPair;

	@Override
	public void create (){
		// Box 2d stuff
		Box2D.init();
		this.world = new World(new Vector2(0, -10), false);
		this.world.setContactListener(this);

		// Registering this class as an input handler
		Gdx.input.setInputProcessor(this);

		// Camera
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.camera.translate(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.camera.update();

		// Debug camera
		this.physRenderer = new Box2DDebugRenderer();

		// TEMP
		this.trianglePairs = new ArrayList<>();
		this.triangle = new Texture("traingle.png");

		// Spritebatch for drawing the stuff onto the screen
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		TrianglePair pair1 = new TrianglePair(world, false, new Position(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2));
		trianglePairs.add(pair1);

		this.spawner = new Spawner(world, trianglePairs);

		this.currentPlayer = new PlayerTriangle(world, Orientation.UP);

		// Setting the ground
		BodyDef groundDef = new BodyDef();
		groundDef.type = BodyDef.BodyType.StaticBody;
		groundDef.position.set(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2) - 120);

		Body ground = world.createBody(groundDef);
		ground.setUserData(this);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(camera.viewportWidth, 1);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0;
		ground.createFixture(fixtureDef);
		shape.dispose();
	}

	private void update(float dt){
		// Box2d thing
		world.step(1/60f, 6, 2);

		if (!world.isLocked()){
			if (toBeDeletedPair != null){
				toBeDeletedPair.dispose();
				trianglePairs.remove(toBeDeletedPair);
				toBeDeletedPair = null;

				System.out.println("Called");
			}
		}

		spawner.update();

		// Camera
		camera.update();
	}

	@Override
	public void render () {
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		physRenderer.render(world, camera.combined);
		batch.begin();

		for (TrianglePair pair: trianglePairs){
			// Updating the triangles
			pair.pos.x = (int) pair.body.getPosition().x;
			pair.pos.y = (int) pair.body.getPosition().y;

			// Drawing part
			batch.draw(triangle, pair.pos.x, pair.pos.y, 50, 50);
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		triangle.dispose();
		batch.dispose();
		physRenderer.dispose();
		world.dispose();
	}

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getBody().getUserData() instanceof TrianglePair){
			if (contact.getFixtureB().getBody().getUserData() instanceof PlayerTriangle){
				notifyContactedTriangle((TrianglePair) contact.getFixtureA().getBody().getUserData());
			}
		}

		else if (contact.getFixtureB().getBody().getUserData() instanceof TrianglePair){
			if (contact.getFixtureA().getBody().getUserData() instanceof PlayerTriangle){
				notifyContactedTriangle((TrianglePair) contact.getFixtureB().getBody().getUserData());
			}
		}

	}

	private void notifyContactedTriangle(TrianglePair pair){
		boolean resultOfEngagement = pair.engage(currentPlayer);
		if (resultOfEngagement == TrianglePair.COLLISION_SUCCESS)
			recycleTriangle(pair);
		else {
			endGame(pair);
		}

	}

	private void endGame(TrianglePair pair) {
		recycleTriangle(pair);
	}

	private void recycleTriangle(TrianglePair pair){
		toBeDeletedPair = pair;
		spawner.scheduleSpawn(currentPlayer.pos);
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	// INPUT HANDLING

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode){
			case Input.Keys.RIGHT:
				currentPlayer.body.applyForceToCenter(2500, 0, true);
				break;
			case Input.Keys.LEFT:
				currentPlayer.body.applyForceToCenter(-2500, 0, true);
				break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
