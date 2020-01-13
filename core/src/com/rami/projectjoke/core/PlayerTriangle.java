package com.rami.projectjoke.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class PlayerTriangle {

    public Position pos;
    Orientation orientation;

    private Body body;

    public PlayerTriangle(World world, Orientation orientation) {
        this.pos = new Position(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.orientation = orientation;

        initPhysBody(world);
    }

    private void initPhysBody(World world){
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(pos.x, pos.y);

        body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        Vector2 vector2s[] = {new Vector2(-15, 0), new Vector2(0, 15), new Vector2(15, 0)};
        shape.set(vector2s);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;

        body.createFixture(fixDef);
        body.setUserData(this);

        shape.dispose();

    }

    public Orientation getOrientation() {
        return orientation;
    }
}
