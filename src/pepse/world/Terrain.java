package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

public class Terrain {

    private GameObjectCollection gameObjects;
    private int groundLayer;
    private Vector2 windowDimensions;

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;


    /**
     * constructor
     * @param gameObjects
     * @param groundLayer
     * @param windowDimensions
     * @param seed
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed){

        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
    }

    /**
     * calculates the approximate ground height at point x
     * @param x
     * @return
     */
    public float groundHeightAt(float x) { return windowDimensions.y()*(2/3f);} //todo create noise function


    public void createInRange(int minX, int maxX){

        for(int i = minX; i <= maxX; i+=Block.SIZE){
            float maxHeight = (float) Math.floor(groundHeightAt(i) / Block.SIZE) * Block.SIZE;
            for (float j = maxHeight; j < maxHeight+ TERRAIN_DEPTH*Block.SIZE; j+= Block.SIZE){
                RectangleRenderable recRender = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Vector2 location = new Vector2(i,j);
                Block block = new Block(location,recRender);
                this.gameObjects.addGameObject(block);
            }
        }

    }
}
