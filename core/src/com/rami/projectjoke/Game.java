package com.rami.projectjoke;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.rami.projectjoke.core.Orientation;
import com.rami.projectjoke.core.PlayerTriangle;
import com.rami.projectjoke.core.Position;
import com.rami.projectjoke.core.TrianglePair;

import java.util.ArrayList;

public class Game extends ApplicationAdapter {
	private PlayerTriangle currentPlayer;
	private ArrayList<TrianglePair> trianglePairs;

	private OrthographicCamera camera;
	private Box2DDebugRenderer physRenderer;

	private World world;

	private Texture triangle;
	private SpriteBatch batch;

	@Override
	public void create () {
		Box2D.init();
		this.world = new World(new Vector2(0, -10), false);

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

//		this.currentPlayer = new PlayerTriangle(world, Orientation.UP);

		// Setting the ground
		BodyDef groundDef = new BodyDef();
		groundDef.type = BodyDef.BodyType.StaticBody;
		groundDef.position.set(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2) - 120);

		Body ground = world.createBody(groundDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(camera.viewportWidth, 1);
		ground.createFixture(shape, 0);
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

}
