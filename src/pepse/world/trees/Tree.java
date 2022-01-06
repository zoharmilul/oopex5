package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;

/**
 * Responsible for the creation and management of trees.
 */
public class Tree {


    private static final Color treeLogColor = new Color(100, 50, 20);
    private static final Color treeLeavesColor = new Color(50, 200, 30);
    private final Function<Float, Float> groundHeight;
    private final Vector2 windowDimensions;
    private final GameObjectCollection gameObjects;
    private final int layer;
    private final int seed = 8;
    private final int leafSize = 8;
    private static final int DIFF_BETWEEN_TREES = 5;
    private static final int MIN_TREE_HEIGHT = 5;
    private static final int MAX_TREE_HEIGHT = 20;
    private static final float CREATE_TREE = 0.5f;
    private static final float CREATE_LEAF = 0.14f;

    public static String TREE_TAG = "Tree";
    public static String LEAF_TAG = "Leaf";

    /**
     * constructor
     * @param groundHeight ground height
     * @param windowDimensions The dimensions of the windows.
     * @param gameObjects The collection of all participating game objects.
     * @param layer The number of the layer to which the created halo should be added.
     */
    public Tree(Function<Float, Float> groundHeight,
                Vector2 windowDimensions,
                GameObjectCollection gameObjects,
                int layer) {
        this.groundHeight = groundHeight;
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    /**
     * This method creates trees in a given range of x-values.
     * @param minX The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        Random rand = new Random(seed);
        for (int i = minX; i < maxX; i += DIFF_BETWEEN_TREES * Block.SIZE) {

            //decide if create tree here or not
            if (groundHeight.apply((float) i) >= CREATE_TREE) {
                createTree((float) i, MIN_TREE_HEIGHT + rand.nextInt(MAX_TREE_HEIGHT));
            }
        }

    }

    /*
     * creates one tree
     */
    private void createTree(float location, int height) {

        RectangleRenderable treeLogRenderable =
                new RectangleRenderable(ColorSupplier.approximateColor(treeLogColor));
        Vector2 treeTopLeft = new Vector2(location,
                groundHeight.apply(location) - height * Block.SIZE);
        Vector2 dims = new Vector2(Block.SIZE, height * Block.SIZE);
        Block treeLog = new Block(treeTopLeft, treeLogRenderable);
        treeLog.setTag(TREE_TAG);
        treeLog.setDimensions(dims);
        treeLog.setTopLeftCorner(treeTopLeft);
        createTreeLeaves((int) (treeLog.getTopLeftCorner().y()),
                (int) location,
                leafSize * Block.SIZE);
        this.gameObjects.addGameObject(treeLog, layer);
    }

    /*
     * create one leaf
     */
    private Leaf createLeaf(Vector2 position) {
        RectangleRenderable leafRenderable =
                new RectangleRenderable(ColorSupplier.approximateColor(treeLeavesColor));
        return new Leaf(position, leafRenderable, layer, gameObjects);
    }

    /*
     * create the leaves
     */
    private void createTreeLeaves(int treeTopY, int treeX, int leafSize) {
        Random rand = new Random(treeX);
        for (int j = treeTopY - (leafSize) / 2; j < treeTopY + leafSize / 2; j += Block.SIZE) {
            for (int i = treeX - (leafSize) / 2; i < treeX + (leafSize) / 2; i += Block.SIZE) {
                float temp = rand.nextFloat();
                if (temp >= CREATE_LEAF) {
                    final Vector2 leafLocation = new Vector2(i, j);
                    Leaf leaf = createLeaf(leafLocation);
                    leaf.setTag(LEAF_TAG);
                    gameObjects.addGameObject(leaf, layer);
                }
            }
        }
    }
}
