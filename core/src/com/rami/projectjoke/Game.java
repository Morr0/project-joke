package com.rami.projectjoke;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.rami.projectjoke.core.Orientation;
import com.rami.projectjoke.core.PlayerTriangle;
import com.rami.projectjoke.core.Position;
import com.rami.projectjoke.core.TrianglePair;

import java.util.ArrayList;

public class Game extends ApplicationAdapter implements ContactListener {
	private PlayerTriangle currentPlayer;
	private ArrayList<TrianglePair> trianglePairs;

	private OrthographicCamera camera;
	private Box2DDebugRenderer physRenderer;

	private World world;

	private Texture triangle;
	private SpriteBatch batch;

	@Override
	public void create (){
		Box2D.init();
		this.world = new World(new Vector2(0, -10), false);
		this.world.setContactListener(this);

		this.camera = new OrthographicCamera(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.camera.translate(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.camera.update();

		this.physRenderer = new Box2DDebugRenderer();

		this.trianglePairs = new ArrayList<>();
		this.triangle = new Texture("traingle.png");

		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		TrianglePair pair1 = new TrianglePair(world, false, new Position(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2));
		trianglePairs.add(pair1);

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

			// Drawing part
			batch.draw(triangle, pair.pos.x, pair.pos.y, 50, 50);
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		triangle.dispose();
		batch.dispose();
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
			trianglePairs.remove(pair);
		else
			endGame();
	}

	private void endGame(){
		System.out.println("END game");
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
}
