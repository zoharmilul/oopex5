package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.function.Function;

public class Tree {


    private static final Color treeLogColor = new Color(100,50,20);
    private static final Color treeLeavesColor = new Color(50  , 200, 30);
    private final Function<Float, Float> groundHeight;
    private final Vector2 windowDimensions;
    private final GameObjectCollection gameObjects;
    private final int layer;
    private final int seed = 8;

    public static String treeTag = "Tree";
    public static String leafTag = "Leaf";

    public Tree(Function<Float,Float> groundHeight,
                Vector2 windowDimensions,
                GameObjectCollection gameObjects,
                int layer){
        this.groundHeight = groundHeight;
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
        this.layer = layer;
    }

    public void createInRange(int minX, int maxX) {
        Random rand = new Random(seed);
        for (int i = minX; i<maxX; i+=5*Block.SIZE){
            if (groundHeight.apply((float)i) >= 0.5f){
                createTree((float)i,5 + rand.nextInt(20));
            }
        }

    }


    /*
    creates on tree
     */
    private void createTree(float location, int height){

        RectangleRenderable treeLogRenderable = new RectangleRenderable(ColorSupplier.approximateColor(treeLogColor));
        Vector2 treeTopLeft = new Vector2(location,
                 groundHeight.apply(location) - height*Block.SIZE);
        Vector2 dims = new Vector2(Block.SIZE, height*Block.SIZE);
        Block treeLog = new Block(treeTopLeft,treeLogRenderable);
        treeLog.setTag(treeTag);
        treeLog.setDimensions(dims);
        treeLog.setTopLeftCorner(treeTopLeft);
        createTreeLeaves((int)(treeLog.getTopLeftCorner().y()),
                (int)location,
                8*Block.SIZE);
        this.gameObjects.addGameObject(treeLog,layer);
    }

    /*
    create one leaf
     */
    private Leaf createLeaf(Vector2 position){
        RectangleRenderable leafRenderable = new RectangleRenderable(ColorSupplier.approximateColor(treeLeavesColor));
        return new Leaf(position,leafRenderable,layer,gameObjects);
    }
    /*
    create the leaves
     */
    private void createTreeLeaves(int treeTopY,int treeX,int leafSize){
        Random rand = new Random(treeX);
        for (int j = treeTopY - (leafSize)/2; j < treeTopY + leafSize/2 ; j+= Block.SIZE){
            for (int i = treeX - (leafSize )/2; i < treeX + (leafSize )/2; i += Block.SIZE){
                float temp = rand.nextFloat();
                if (temp >= 0.14f)
                {
                    final Vector2 leafLocation = new Vector2(i,j);
                    Leaf leaf = createLeaf(leafLocation);
                    leaf.setTag(leafTag);
                    gameObjects.addGameObject(leaf,layer);
                }
            }
        }
    }
}
