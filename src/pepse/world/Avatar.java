package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * An avatar can move around the world.
 */
public class Avatar extends GameObject {

    private static GameObjectCollection gameObjects;
    private static int layer;
    private static Vector2 topLeftCorner;
    private static UserInputListener inputListener;
    private static ImageReader imageReader;
    private float energy;
    private static Renderable avatarInitRenderable;

    public final static Vector2 AVATAR_DIMS = new Vector2(3, 5).mult(Block.SIZE);
    private static final int AVATAR_GRAVITY = 500;
    private static final int AVATAR_MOVEMENT_SPEED = 300;
    private static final float AVATAR_MAX_ENERGY = 100f;
    private static final float AVATAR_ENERGY_CHANGE = 0.5f;
    private static final int X_VELOCITY_CHANGE = 1;
    private static final int RESTING_VELOCITY = 0;
    private static final String AVATAR_INIT_IMAGE_PATH = "src/pepse/assets/avatar.png";
    private static final String AVATAR_FLIGHT_IMAGE_PATH = "src/pepse/assets/avatar_flies.png";

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        avatarInitRenderable = imageReader.readImage(AVATAR_INIT_IMAGE_PATH, true);
        energy = AVATAR_MAX_ENERGY;
    }

    /**
     * This function creates an avatar that can travel the world and is followed by the camera.
     *
     * @param gameObjects   game object collection instance
     * @param topLeftCorner top left corner of the avatar
     * @param inputListener input listener
     * @param imageReader   image reader
     * @return the avatar which is just created
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {
        Avatar.gameObjects = gameObjects;
        Avatar.topLeftCorner = topLeftCorner;
        Avatar.inputListener = inputListener;
        Avatar.imageReader = imageReader;
        Avatar.layer = layer;

        Avatar avatar = new Avatar(topLeftCorner, Avatar.AVATAR_DIMS, avatarInitRenderable);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(AVATAR_GRAVITY);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * defines with whom the avatar can collide with
     *
     * @param other other game object that participated in the collision
     * @return whether the other object is a Block
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (other instanceof Block);
    }

    /**
     * updates the avatar status while the game
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation of this method
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //X Movement
        xMovement();
        //Jump
        jumping();
        //Flight
        flight();
    }

    /*
     * according to the keys that are pressed, makes the avatar fly if it has enough energy, and updates its energy
     */
    private void flight() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
            if (energy >= AVATAR_ENERGY_CHANGE) {
                this.transform().setVelocityY(-AVATAR_MOVEMENT_SPEED);
                Renderable avatarFlightRenderable =
                        imageReader.readImage(AVATAR_FLIGHT_IMAGE_PATH, true);
                this.renderer().setRenderable(avatarFlightRenderable);
                energy -= AVATAR_ENERGY_CHANGE;
            }
        }
        if (this.getVelocity().y() == RESTING_VELOCITY) {
            this.renderer().setRenderable(avatarInitRenderable);
            if (energy < AVATAR_MAX_ENERGY && this.transform().getVelocity().y() == RESTING_VELOCITY) {
                energy += AVATAR_ENERGY_CHANGE;
            }
        }
    }

    /*
     * if the space key is pressed, makes the avatar jump
     */
    private void jumping() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.transform().getVelocity().y() == 0) {
            this.setVelocity(Vector2.UP.mult(AVATAR_MOVEMENT_SPEED));
        }
    }

    /*
     * if the left or right key is pressed, cause the avatar moving left and right
     */
    private void xMovement() {
        float xSpeed = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xSpeed += X_VELOCITY_CHANGE;
            renderer().setIsFlippedHorizontally(true);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            renderer().setIsFlippedHorizontally(false);
            xSpeed -= X_VELOCITY_CHANGE;
        }
        this.transform().setVelocityX(xSpeed * AVATAR_MOVEMENT_SPEED);
    }

    /**
     * when the avatar collides with other game objects, sets his velocity to zero
     *
     * @param other     other game object that participated in the collision
     * @param collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.transform().setVelocityY(RESTING_VELOCITY);
    }


}
