package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

public class Sun {

    public static final float sunSize = 200;

    public static String sunLabel = "sun";

    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength) {
        OvalRenderable sunRenderable = new OvalRenderable(Color.YELLOW);
        GameObject sun = new GameObject(Vector2.ZERO,Vector2.ONES.mult(sunSize),sunRenderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(sun, layer);
        new Transition<Float>(sun,
                angle -> sun.setCenter(windowDimensions.mult(0.5f).add(new Vector2((float) Math.sin(angle) * windowDimensions.x()/2,
                        (float) Math.cos(angle)* windowDimensions.y()/2).multY(-1))) ,
                0f,
                (float) (Math.PI*2),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        sun.setTag(sunLabel);
        return sun;
    }

}
