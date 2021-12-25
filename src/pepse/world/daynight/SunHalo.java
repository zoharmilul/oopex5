package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        OvalRenderable sunHaloRenderable = new OvalRenderable(color);
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), Vector2.ONES.mult(Sun.sunSize*3),sunHaloRenderable);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.addComponent((deltaTime -> sunHalo.setCenter(sun.getCenter())));
        return sun;
    }
}
