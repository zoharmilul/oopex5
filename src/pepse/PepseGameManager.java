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

public class PepseGameManager extends GameManager {
    private static final int cycleLength = 30;
    private final int groundLayer = Layer.STATIC_OBJECTS;
    private final int treeLayer = Layer.STATIC_OBJECTS + 1;
    private final int avatarLayer = Layer.DEFAULT;
    private final float AVATAR_RATIO_TO_LAND = 0.25f;
    private float landMaxX;
    private float landMinX;

    private Avatar avatar;
    private Terrain terrain;
    private Tree tree;
    private WindowController windowController;


    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        this.windowController = windowController;
        super.initializeGame(imageReader,
                soundReader,
                inputListener,
                windowController);
        Random rand =new Random();
        Sky.create(gameObjects(),
                windowController.getWindowDimensions(),
                Layer.BACKGROUND);
        terrain = new Terrain(gameObjects(),
                groundLayer,
                windowController.getWindowDimensions(),
                rand.nextInt(5));
        terrain.createInRange((int)(-windowController.getWindowDimensions().x()*AVATAR_RATIO_TO_LAND),
                (int) (windowController.getWindowDimensions().x()*(1+AVATAR_RATIO_TO_LAND)));
        Night.create(gameObjects(),
                Layer.FOREGROUND,
                windowController.getWindowDimensions(),
                cycleLength);
        GameObject sun = Sun.create(gameObjects(),
                Layer.BACKGROUND+1,
                windowController.getWindowDimensions(),
                cycleLength);
        SunHalo.create(gameObjects(),
                Layer.BACKGROUND + 2,
                sun,
                new Color(255, 255, 0, 20));
        tree = new Tree(terrain::groundHeightAt,windowController.getWindowDimensions(),gameObjects(),treeLayer);
        tree.createInRange((int)(-windowController.getWindowDimensions().x()*AVATAR_RATIO_TO_LAND),
                (int) (windowController.getWindowDimensions().x()*(1+AVATAR_RATIO_TO_LAND)));
        landMaxX = windowController.getWindowDimensions().x()*(1+AVATAR_RATIO_TO_LAND);
        landMinX = (-1)*windowController.getWindowDimensions().x()*AVATAR_RATIO_TO_LAND;
        gameObjects().layers().shouldLayersCollide(groundLayer, treeLayer, true);
        Vector2 startingPos = new Vector2(0, terrain.groundHeightAt(0)-Avatar.DIMS.y());
        avatar = Avatar.create(gameObjects(),avatarLayer, startingPos,inputListener,imageReader);
        gameObjects().layers().shouldLayersCollide(avatarLayer, treeLayer, true);
        gameObjects().layers().shouldLayersCollide(groundLayer, avatarLayer, true);
//        windowController.getWindowDimensions().mult(0.5f).subtract(startingPos)
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }



    private void deleteInRange(float minX, float maxX){
        for(GameObject obj: gameObjects()){
            if(obj.getTopLeftCorner().x() >= minX && obj.getTopLeftCorner().x() <= maxX){
                if(obj.getTag().equals(Terrain.landLabel) || obj.getTag().equals(Terrain.grassLabel))
                    gameObjects().removeGameObject(obj,groundLayer);
                else if(obj.getTag().equals(Tree.treeTag) || obj.getTag().equals(Tree.leafTag))
                    gameObjects().removeGameObject(obj,treeLayer);
            }
        }
    }

    private void updateTerrain(){
        if (landMaxX - avatar.getTopLeftCorner().x() <=
                (1 - AVATAR_RATIO_TO_LAND)*windowController.getWindowDimensions().x())
        {
            deleteInRange(landMinX, landMinX + AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x());
            terrain.createInRange((int)landMaxX,
                    (int)(landMaxX + AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x()));
            tree.createInRange((int)landMaxX,
                    (int)(landMaxX + AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x()));
            landMaxX += AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x();
            landMinX += AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x();
        }

        if (avatar.getTopLeftCorner().x() - landMinX <=
                (1-AVATAR_RATIO_TO_LAND*2)*windowController.getWindowDimensions().x())
        {
            deleteInRange(landMaxX - AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x(),landMaxX);
            terrain.createInRange((int)(landMinX -AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x()),
                    (int)landMinX);
            tree.createInRange((int)(landMinX -AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x()),
                    (int)landMinX);
            landMaxX -= AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x();
            landMinX -= AVATAR_RATIO_TO_LAND*windowController.getWindowDimensions().x();
        }

    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateTerrain();
    }
}
