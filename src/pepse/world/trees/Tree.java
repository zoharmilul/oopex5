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
    private Function<Float, Float> groundHeight;
    private Vector2 windowDimensions;
    private GameObjectCollection gameObjects;
    private int layer;


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
        Random rand = new Random();
        for (int i = minX; i<maxX; i+=5*Block.SIZE){
            if (rand.nextBoolean()){
                createTree((float)i,25 + rand.nextInt(10));
            }
        }

    }


    /*
    creates on tree
     */
    private void createTree(float location, int height){

        RectangleRenderable treeLogRenderable = new RectangleRenderable(ColorSupplier.approximateColor(treeLogColor));
        Vector2 treeTopLeft = new Vector2(location,
                windowDimensions.x() - groundHeight.apply(location) - height* Block.SIZE);
        Vector2 dims = new Vector2(Block.SIZE,height*Block.SIZE);
        GameObject treeLog = new GameObject(treeTopLeft,dims,treeLogRenderable);
        createTreeLeaves((int) (windowDimensions.x() - groundHeight.apply(location) - height* Block.SIZE),(int)location,8*Block.SIZE);
        this.gameObjects.addGameObject(treeLog,layer);
    }

    /*
    create the leaves
     */
    private void createTreeLeaves(int treeHeight,int treeX,int leafSize){
        Random rand = new Random();
        for (int j = treeHeight - (leafSize)/2; j < treeHeight + leafSize/2 ; j+= Block.SIZE){
            for (int i = treeX - (leafSize )/2; i < treeX + (leafSize )/2; i += Block.SIZE){
                float temp = rand.nextFloat(1);
                if (temp >= 0.14f)
                {
                    RectangleRenderable leafRenderable = new RectangleRenderable(ColorSupplier.approximateColor(treeLeavesColor));
                    final Vector2 leafLocation = new Vector2(i,j);
                    GameObject leaf = new Leaf(leafLocation,leafRenderable);
                    float lifeTime = rand.nextFloat(200);
                    //wind change task
                    new ScheduledTask(leaf,
                            rand.nextFloat(1),
                            true,  () -> {
                         new Transition<Float>(leaf,
                                leaf.renderer()::setRenderableAngle,
                                0f,
                                180f,
                                Transition.CUBIC_INTERPOLATOR_FLOAT,
                                5,
                                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                                null);
                        new Transition<Vector2>(leaf,
                                leaf::setDimensions,
                                Vector2.ONES.mult(Block.SIZE),
                                new Vector2(Block.SIZE*0.5f,Block.SIZE*0.9f),
                                Transition.LINEAR_INTERPOLATOR_VECTOR,
                                10,
                                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                                null);
                    });

                    //life cycle task
                    new ScheduledTask(leaf,
                            lifeTime,
                            true,
                            ()->{
                            leaf.transform().setVelocity(Vector2.DOWN.mult(50));
                                new Transition<Float>(leaf,
                                        leaf.transform()::setVelocityX,
                                        -20f,
                                        20f,
                                        Transition.CUBIC_INTERPOLATOR_FLOAT,
                                        1,
                                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                                        null);
                            leaf.renderer().fadeOut(rand.nextFloat(30),()-> {
                                leaf.setTopLeftCorner(leafLocation);
                                leaf.renderer().setOpaqueness(1);
                            });
                            }
                            );
                    gameObjects.addGameObject(leaf);
                }
            }

        }

    }

}
