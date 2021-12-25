package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sky {


    //static consts
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");


    /**
     * create new sky
     * @param gameObjects
     * @param windowDimensions
     * @param skyLayer
     * @return the sky (wow)
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer){
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sky,skyLayer);
        sky.setTag("sky"); //for debugging
        return sky;
    }


}
