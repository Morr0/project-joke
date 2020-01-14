package com.rami.projectjoke.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

// Represents a two isosceles triangles
public class TrianglePair {
    public final static boolean COLLISION_FATAL = false;
    public final static boolean COLLISION_SUCCESS = true;

    boolean emptyBottomSide;
    public Position pos;

    private Body body;

    public TrianglePair(World world, boolean emptyBottomSide, Position pos){
        this.emptyBottomSide = emptyBottomSide;
        this.pos = pos;

        initPhysBody(world);
    }

    private void initPhysBody(World world){
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(pos.x, pos.y);

        body = world.createBody(def);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        Vector2 vector2s[] = {new Vector2(-15, 0), new Vector2(0, 15), new Vector2(15, 0)};
        shape.set(vector2s);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;

        body.createFixture(fixDef);

        shape.dispose();


    }

    public boolean isEmptyBottomSide() {
        return emptyBottomSide;
    }

    public boolean engage(PlayerTriangle playerTriangle){
        // If the sides the collision completes a pair, wins, else loses
        if (emptyBottomSide)
            if (playerTriangle.orientation == Orientation.DOWN)
                return COLLISION_SUCCESS;

        return COLLISION_FATAL;
    }
}
