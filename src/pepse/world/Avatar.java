package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {


    public final static Vector2 DIMS = new Vector2(3,5 ).mult(Block.SIZE);

    private final static int AVATAR_GRAVITY = 1000;
    private final static int AVATAR_MOVEMENT_SPEED = 300;
    private static GameObjectCollection gameObjects;
    private static int layer;
    private static Vector2 topLeftCorner;
    private static UserInputListener inputListener;
    private static ImageReader imageReader;
    private  float energy;
    private boolean isFlightMode = false;

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
        energy = 100;
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Avatar.gameObjects = gameObjects;
        Avatar.topLeftCorner = topLeftCorner;
        Avatar.inputListener = inputListener;
        Avatar.imageReader = imageReader;
        Renderable avatarRenderable = imageReader.readImage("src/pepse/avatar.png",true);
        Avatar avatar = new Avatar(topLeftCorner,Avatar.DIMS,avatarRenderable);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        avatar.transform().setAccelerationY(AVATAR_GRAVITY);
        gameObjects.addGameObject(avatar);
        return null;
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        return (other instanceof Block);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 toMove = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            toMove = toMove.add(Vector2.RIGHT);
            renderer().setIsFlippedHorizontally(true);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            toMove = toMove.add(Vector2.LEFT);
            renderer().setIsFlippedHorizontally(false);
        }
        this.setVelocity(toMove.mult(AVATAR_MOVEMENT_SPEED));
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.transform().getVelocity().y() == 0) {
            this.setVelocity(Vector2.UP.mult(AVATAR_MOVEMENT_SPEED*3));

        }

        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)){
            if (energy >= 0.5f) {
                this.setVelocity(Vector2.UP.mult(AVATAR_MOVEMENT_SPEED*3));
//                this.transform().setAccelerationY(AVATAR_GRAVITY);
                this.renderer().setRenderableAngle(90f);
                energy -= 0.5f;
            }
        }
        if (this.getVelocity().y() == 0){
            this.renderer().setRenderableAngle(0f);
            if (energy < 100f && this.transform().getAcceleration().y() == 0){
                energy += 0.5f;
                System.out.println(energy);
            }
        }

  System.out.println(String.format("VelocityX: %f, VelocityY: %f",this.getVelocity().x(), this.transform().getVelocity().y()));

    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
    }


}
