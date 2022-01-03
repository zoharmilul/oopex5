package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

public class Leaf extends GameObject {


    private Transition<Float> horizontalTransition;
    private final Random rand;
    private final Vector2 topLeftCorner;
    private final Renderable renderable;
    private final int layer;
    private final GameObjectCollection gameObjects;

    /**
     * constructor
     * @param topLeftCorner topleftcorner
     * @param renderable renderable
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable, int layer, GameObjectCollection gameObject) {
        super(topLeftCorner, Vector2.ONES.mult(Block.SIZE), renderable);
        this.topLeftCorner = topLeftCorner;
        this.renderable = renderable;
        this.layer = layer;
        this.gameObjects = gameObject;
        horizontalTransition = null;
        this.rand = new Random();
        this.physics().setMass(0);
        createCycle();
    }

    private void createCycle(){
        new ScheduledTask(this,
                rand.nextFloat(1),
                true,  () -> {

            //angle transition
            new Transition<Float>(this,
                    this.renderer()::setRenderableAngle,
                    0f,
                    180f,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    5,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);

            //dimensions transition
            new Transition<Vector2>(this,
                    this::setDimensions,
                    Vector2.ONES.mult(Block.SIZE),
                    new Vector2(Block.SIZE*0.1f,Block.SIZE*0.1f),
                    Transition.LINEAR_INTERPOLATOR_VECTOR,
                    10,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
        });


        float lifeTime = rand.nextFloat(200);
        //life cycle task
        new ScheduledTask(this, lifeTime, true, ()->{
           horizontalTransition = new Transition<Float>(this,
                    this.transform()::setVelocityX,
                    -20f,
                    20f,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    1,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
            this.transform().setVelocity(Vector2.DOWN.mult(50));
            this.renderer().fadeOut(rand.nextFloat(30),()-> {
                gameObjects.removeGameObject(this,layer);
                gameObjects.addGameObject(new Leaf(topLeftCorner, renderable, layer,gameObjects),layer);
            });
        });
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.setVelocity(Vector2.ZERO);
        this.removeComponent(horizontalTransition);
    }
}
