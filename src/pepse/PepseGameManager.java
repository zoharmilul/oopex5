package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
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
import java.util.Random;

/**
 * The main class of the simulator.
 */
public class PepseGameManager extends GameManager {
    private static final int cycleLength = 30;
    private static final int ORIGIN_POSITION = 0;
    private final int groundLayer = Layer.STATIC_OBJECTS;
    private final int treeLayer = Layer.STATIC_OBJECTS + 1;
    private final float AVATAR_RATIO_TO_LAND = 0.25f;
    private float landMaxX;
    private float landMinX;

    private Avatar avatar;
    private Terrain terrain;
    private Tree tree;
    private WindowController windowController;

    /**
     *
     * Runs the entire simulation.
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * The method will be called once when a GameGUIComponent is created,
     * and again after every invocation of windowController.resetGame().
     *
     * @param imageReader - Contains a single method: readImage, which reads an image from disk.
     * @param soundReader - Contains a single method: readSound, which reads a wav file from disk.
     * @param inputListener - Contains a single method: isKeyPressed,
     *                      which returns whether a given key is currently pressed by the user or not.
     * @param windowController - Contains an array of helpful, self explanatory methods concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {

        this.windowController = windowController;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Random rand = new Random();
        createSkyObjects(windowController);
        createLandObjects(windowController, rand);
        createAvatar(imageReader, inputListener);
        defineCollisionsBetweenLayers();
        setCamera(new Camera(avatar, Vector2.ZERO, windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    /*
     * Creates avatar
     */
    private void createAvatar(ImageReader imageReader, UserInputListener inputListener) {
        Vector2 startingPos =
                new Vector2(ORIGIN_POSITION, terrain.groundHeightAt(ORIGIN_POSITION) - Avatar.AVATAR_DIMS.y());
        avatar = Avatar.create(gameObjects(), Layer.DEFAULT, startingPos, inputListener, imageReader);
    }

    /*
     * Defines which layers should collide with each other
     */
    private void defineCollisionsBetweenLayers() {
        gameObjects().layers().shouldLayersCollide(groundLayer, treeLayer, true);
        gameObjects().layers().shouldLayersCollide(Layer.DEFAULT, treeLayer, true);
        gameObjects().layers().shouldLayersCollide(groundLayer, Layer.DEFAULT, true);
    }

    /*
     * Creates the land objects - terrain, grass and trees
     */
    private void createLandObjects(WindowController windowController, Random rand) {
        //terrain
        terrain = new Terrain(gameObjects(),
                groundLayer,
                windowController.getWindowDimensions(),
                rand.nextInt(5));
        terrain.createInRange((int) (-windowController.getWindowDimensions().x() * AVATAR_RATIO_TO_LAND),
                (int) (windowController.getWindowDimensions().x() * (1 + AVATAR_RATIO_TO_LAND)));
        //trees
        tree = new Tree(terrain::groundHeightAt, windowController.getWindowDimensions(), gameObjects(), treeLayer);
        tree.createInRange((int) (-windowController.getWindowDimensions().x() * AVATAR_RATIO_TO_LAND),
                (int) (windowController.getWindowDimensions().x() * (1 + AVATAR_RATIO_TO_LAND)));

        landMaxX = windowController.getWindowDimensions().x() * (1 + AVATAR_RATIO_TO_LAND);
        landMinX = (-1) * windowController.getWindowDimensions().x() * AVATAR_RATIO_TO_LAND;
    }

    /*
     * Creates sky, sun and night
     */
    private void createSkyObjects(WindowController windowController) {
        Sky.create(gameObjects(),
                windowController.getWindowDimensions(),
                Layer.BACKGROUND);
        Night.create(gameObjects(),
                Layer.FOREGROUND,
                windowController.getWindowDimensions(),
                cycleLength);
        GameObject sun = Sun.create(gameObjects(),
                Layer.BACKGROUND + 1,
                windowController.getWindowDimensions(),
                cycleLength);
        SunHalo.create(gameObjects(),
                Layer.BACKGROUND + 2,
                sun,
                new Color(255, 255, 0, 20));
    }


    /*
     * delete terrain and trees in the given range
     */
    private void deleteInRange(float minX, float maxX) {
        for (GameObject obj : gameObjects()) {
            if (obj.getTopLeftCorner().x() >= minX && obj.getTopLeftCorner().x() <= maxX) {
                if (obj.getTag().equals(Terrain.LAND_TAG) || obj.getTag().equals(Terrain.GRASS_TAG))
                    gameObjects().removeGameObject(obj, groundLayer);
                else if (obj.getTag().equals(Tree.TREE_TAG) || obj.getTag().equals(Tree.LEAF_TAG))
                    gameObjects().removeGameObject(obj, treeLayer);
            }
        }
    }

    /*
     * according to the avatar location, creates and deletes terrain where it needed (so that the world is infinite)
     */
    private void updateTerrain() {
        // right side
        if (landMaxX - avatar.getTopLeftCorner().x() <=
                (1 - AVATAR_RATIO_TO_LAND) * windowController.getWindowDimensions().x()) {
            deleteInRange(landMinX, landMinX + AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x());
            terrain.createInRange((int) landMaxX,
                    (int) (landMaxX + AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x()));
            tree.createInRange((int) landMaxX,
                    (int) (landMaxX + AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x()));
            landMaxX += AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x();
            landMinX += AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x();
        }

        // left side
        if (avatar.getTopLeftCorner().x() - landMinX <=
                (1 - AVATAR_RATIO_TO_LAND * 2) * windowController.getWindowDimensions().x()) {
            deleteInRange(landMaxX - AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x(), landMaxX);
            terrain.createInRange((int) (landMinX - AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x()),
                    (int) landMinX);
            tree.createInRange((int) (landMinX - AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x()),
                    (int) landMinX);
            landMaxX -= AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x();
            landMinX -= AVATAR_RATIO_TO_LAND * windowController.getWindowDimensions().x();
        }

    }

    /**
     * Called once per frame and updates the screen according to the changes that have been
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateTerrain();
    }
}
