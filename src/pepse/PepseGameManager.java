package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

public class PepseGameManager extends GameManager {
    private static final int cycleLength = 30;
    private final int groundLayer = Layer.STATIC_OBJECTS;
    private final int treeLayer = Layer.STATIC_OBJECTS + 1;


    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        Sky.create(gameObjects(),windowController.getWindowDimensions(), Layer.BACKGROUND);
        Terrain terrain = new Terrain(gameObjects(),groundLayer,windowController.getWindowDimensions(),0);
        terrain.createInRange(0, (int) windowController.getWindowDimensions().x());
        Night.create(gameObjects(),Layer.FOREGROUND,windowController.getWindowDimensions(),cycleLength);
        GameObject sun = Sun.create(gameObjects(),Layer.BACKGROUND+1,windowController.getWindowDimensions(),cycleLength);
        SunHalo.create(gameObjects(),Layer.BACKGROUND + 2,sun,new Color(255, 255, 0, 20));
        Tree tree = new Tree(terrain::groundHeightAt,windowController.getWindowDimensions(),gameObjects(),treeLayer);
        tree.createInRange(0,(int) windowController.getWindowDimensions().x());
        gameObjects().layers().shouldLayersCollide(groundLayer, treeLayer, true);
        Vector2 startingPos = new Vector2(0, terrain.groundHeightAt(0)-Avatar.DIMS.y());
        Avatar avatar = Avatar.create(gameObjects(), startingPos,inputListener,imageReader);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, treeLayer, true);
        gameObjects().layers().shouldLayersCollide(groundLayer, Layer.DEFAULT, true);
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));

    }

}
