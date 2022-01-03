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
    private final static int AVATAR_MOVEMENT_SPEED = 300;
    private static GameObjectCollection gameObjects;
    private static int layer;
    private static Vector2 topLeftCorner;
    private static UserInputListener inputListener;
    private static ImageReader imageReader;

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
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Avatar.gameObjects = gameObjects;
        Avatar.layer = layer;
        Avatar.topLeftCorner = topLeftCorner;
        Avatar.inputListener = inputListener;
        Avatar.imageReader = imageReader;


        Renderable avatarRenderable = imageReader.readImage("src/pepse/avatar.png",true);
        Avatar avatar = new Avatar(topLeftCorner,Avatar.DIMS,avatarRenderable);
        gameObjects.addGameObject(avatar);
        return null;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 toMove = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            toMove = toMove.add(Vector2.RIGHT);
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            toMove = toMove.add(Vector2.LEFT);
        this.setVelocity(toMove.mult(AVATAR_MOVEMENT_SPEED));
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.transform().getAcceleration().y() == 0) {
            this.setVelocity(Vector2.UP.mult(AVATAR_MOVEMENT_SPEED*3));
            this.transform().setAccelerationY(500);
        }
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (other instanceof Block)
            return true;
        return false;
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        this.setVelocity(this.getVelocity().add(new Vector2(0,this.getVelocity().y()*-1)));
        this.transform().setAccelerationY(0);
    }
}
