package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

/**
 * Responsible for the creation and management of terrain.
 */
public class Terrain {

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final Vector2 windowDimensions;
    private final int seed;
    private final Random rand;

    private final float paramA;
    private final float paramB;
    private final float paramC;

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final Color BASE_GRASS_COLOR = new Color(15, 213, 31);
    private static final int TERRAIN_DEPTH = 30;

    public static final String GRASS_TAG = "GRASS";
    public static final String LAND_TAG = "LAND";

    /**
     * constructor
     *
     * @param gameObjects      The collection of all participating game objects.
     * @param groundLayer      The number of the layer to which the created ground objects should be added.
     * @param windowDimensions The dimensions of the windows.
     * @param seed             A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {

        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        rand = new Random(seed);
        this.paramA = rand.nextInt(20) - 10;
        this.paramB = rand.nextInt(20) - 10;
        this.paramC = rand.nextInt(20) - 10;
    }

    /**
     * calculates the approximate ground height at point x
     *
     * @param x x
     * @return the approximate ground height at point x
     */
    public float groundHeightAt(float x) {

        float initValue = (float) (this.paramA * Math.sin(Math.PI * x) +
                this.paramB * Math.sin(Math.sqrt(2) * x) +
                this.paramC * Math.sin(Math.sqrt(Math.E) * x)) * 10;
        return initValue - 20;
    }

    /**
     * create blocks in the given range
     *
     * @param minX min x
     * @param maxX max x
     */
    public void createInRange(int minX, int maxX) {

        for (int i = minX; i <= maxX; i += Block.SIZE) {
            float maxHeight = (float) Math.floor(groundHeightAt(i) / Block.SIZE) * Block.SIZE;
            for (float j = maxHeight; j < maxHeight + TERRAIN_DEPTH * Block.SIZE; j += Block.SIZE) {
                RectangleRenderable recRender;
                if (j == maxHeight)
                    recRender = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GRASS_COLOR));
                else
                    recRender = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Vector2 location = new Vector2(i, j);
                Block block = new Block(location, recRender);
                if (j <= maxHeight + Block.SIZE)
                    block.setTag(GRASS_TAG);
                else
                    block.setTag(LAND_TAG);
                this.gameObjects.addGameObject(block, groundLayer);
            }
        }

    }


}
