package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the halo of sun.
 */
public class SunHalo {
    /**
     * This function creates a halo around a given object that represents the sun.
     * @param gameObjects The collection of all participating game objects.
     * @param layer The number of the layer to which the created halo should be added.
     * @param sun  A game object representing the sun (it will be followed by the created game object).
     * @param color The color of the halo.
     * @return A new game object representing the sun's halo.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        OvalRenderable sunHaloRenderable = new OvalRenderable(color);
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), Vector2.ONES.mult(Sun.sunSize*3),sunHaloRenderable);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return sun;
    }
}
