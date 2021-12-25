package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;

public class PepseGameManager extends GameManager {
    private static final int cycleLength = 30;

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Sky.create(gameObjects(),windowController.getWindowDimensions(), Layer.BACKGROUND);
        Terrain terrain = new Terrain(gameObjects(),Layer.STATIC_OBJECTS,windowController.getWindowDimensions(),0);
        terrain.createInRange(0, (int) windowController.getWindowDimensions().x());
        Night.create(gameObjects(),Layer.FOREGROUND,windowController.getWindowDimensions(),cycleLength);
        Sun.create(gameObjects(),Layer.BACKGROUND+1,windowController.getWindowDimensions(),cycleLength);
    }
}
