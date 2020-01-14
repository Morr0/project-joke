package com.rami.projectjoke.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

public final class Spawner {
    private World world;
    private ArrayList<TrianglePair> currentTrianglePairs;
    private int currentDifficulty;

    private Random rand;

    // Scheduling
    private boolean schedule = false;
    private Position playerPos;
    private TrianglePair nextSpawnable = null;

    public Spawner(World world, ArrayList<TrianglePair> currentTrianglePairs) {
        this.world = world;
        this.currentTrianglePairs = currentTrianglePairs;

        this.currentDifficulty = 1;

        this.rand = new Random();
    }

    public void upDifficulty(){
        this.currentDifficulty++;
    }

    /**
     * To be called when a triangle gets removed or on occasion
     * */
    public void scheduleSpawn(Position playerPos){
        this.playerPos = playerPos;
        this.schedule = true;
    }

    public void update(){
        if (!world.isLocked()){
            spawn();
        }

        if (nextSpawnable != null){
            currentTrianglePairs.add(nextSpawnable);

            nextSpawnable = null;
        }
    }

    private void spawn(){
        // Random traingle orientation
        boolean emptyBottomSide = rand.nextBoolean();

        // Random position
        Position position = new Position(0,
                (Gdx.graphics.getHeight() / 2) + 150 - (4 * currentDifficulty)/30);
        position.x = rand.nextInt(Gdx.graphics.getWidth());
        nextSpawnable = new TrianglePair(world, emptyBottomSide, position);
    }
}
