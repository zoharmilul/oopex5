package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.Random;

/**
 * leaf of the trees
 */
public class Leaf extends GameObject {

    private Transition<Float> horizontalTransition;
    private final Random rand;
    private final Vector2 topLeftCorner;
    private final Renderable renderable;
    private final int layer;
    private final GameObjectCollection gameObjects;

    private final int MIN_LEAF_CYCLE = 10;
    private final int MAX_LEAF_CYCLE = 200;
    private static final int LEAF_MASS = 0;

    private static final int ANGLE_TRANSITION_TIME = 5;
    private static final int DIMS_TRANSITION_TIME = 10;
    private static final int HORIZONTAL_TRANSITION_TIME = 1;
    private static final float MIN_LEAF_ANGLE = 0f;
    private static final float MAX_LEAF_ANGLE = 180f;
    private static final float NEW_DIMS_RATIO_TO_ORIGINAL = 0.1f;
    private static final float MIN_HORIZONTAL_MOVE = -20f;
    private static final float MAX_HORIZONTAL_MOVE = 20f;
    private static final int LEAF_FALL_VELOCITY = 50;


    /**
     * constructor
     *
     * @param topLeftCorner topleftcorner
     * @param renderable    renderable
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable, int layer, GameObjectCollection gameObject) {
        super(topLeftCorner, Vector2.ONES.mult(Block.SIZE), renderable);
        this.topLeftCorner = topLeftCorner;
        this.renderable = renderable;
        this.layer = layer;
        this.gameObjects = gameObject;
        horizontalTransition = null;
        this.rand = new Random();
        this.physics().setMass(LEAF_MASS);
        createCycle();
    }

    /*
     * Create tasks that the leaf does while its life
     */
    private void createCycle() {
        angleAndDimsTasks();
        lifeCycleTask();
    }

    /*
     * Chooses a random lifetime of the leaf. when this time is over - makes a leaf fall from the tree and fade out.
     * After it disappears, it returns to its initial position on the tree
     */
    private void lifeCycleTask() {
        int lifeTime = rand.nextInt(MAX_LEAF_CYCLE - MIN_LEAF_CYCLE) + MIN_LEAF_CYCLE;

        new ScheduledTask(this, (float) lifeTime, true, () -> {
            horizontalTransition = new Transition<Float>(this,
                    this.transform()::setVelocityX,
                    MIN_HORIZONTAL_MOVE,
                    MAX_HORIZONTAL_MOVE,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    HORIZONTAL_TRANSITION_TIME,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
            this.transform().setVelocity(Vector2.DOWN.mult(LEAF_FALL_VELOCITY));
            this.renderer().fadeOut(rand.nextInt(MIN_LEAF_CYCLE / 2), () -> {
                gameObjects.removeGameObject(this, layer);
                gameObjects.addGameObject(new Leaf(topLeftCorner, renderable, layer, gameObjects), layer);
            });
        });
    }

    /*
     * causes the leaf to turn at a random angle and to change its dimensions when it turns
     */
    private void angleAndDimsTasks() {
        new ScheduledTask(this,
                rand.nextFloat(),
                true, () -> {
            //angle transition
            new Transition<Float>(this,
                    this.renderer()::setRenderableAngle,
                    MIN_LEAF_ANGLE,
                    MAX_LEAF_ANGLE,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    ANGLE_TRANSITION_TIME,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);

            //dimensions transition
            new Transition<Vector2>(this,
                    this::setDimensions,
                    Vector2.ONES.mult(Block.SIZE),
                    new Vector2(Block.SIZE * NEW_DIMS_RATIO_TO_ORIGINAL, Block.SIZE * NEW_DIMS_RATIO_TO_ORIGINAL),
                    Transition.LINEAR_INTERPOLATOR_VECTOR,
                    DIMS_TRANSITION_TIME,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
        });
    }

    /**
     * defines with whom a leaf can collide with
     *
     * @param other other game object that participated in the collision
     * @return whether the other object is a terrain
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (other.getTag().equals(Terrain.GRASS_TAG));
    }

    /**
     * when the leaf collides with other game objects, sets his velocity to zero and stop its Xmoving.
     *
     * @param other     other game object that participated in the collision
     * @param collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.setVelocity(Vector2.ZERO);
        this.removeComponent(horizontalTransition);
    }
}
