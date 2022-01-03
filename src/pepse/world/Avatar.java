package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {


    private static GameObjectCollection gameObjects;
    private static int layer;
    private static Vector2 topLeftCorner;
    private static UserInputListener inputListener;
    private static ImageReader imageReader;
    private final int AVATAR_MOVEMENT_SPEED = 300;

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

        Vector2 dims = new Vector2(3,ea1).mult(Block.SIZE);
        Renderable avatarRenderable = imageReader.rdImage()
        GameObject avatar = new GameObject(topLeftCorner,dims,)
        return null;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 toMove = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            toMove.add(Vector2.RIGHT);
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            toMove.add(Vector2.LEFT);
        this.setVelocity(toMove.mult(AVATAR_MOVEMENT_SPEED));
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.getVelocity().y() == 0) {
            this.setVelocity(Vector2.UP.mult(AVATAR_MOVEMENT_SPEED));

        }

    }
}
