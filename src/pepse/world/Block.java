package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Block Represents a single block (larger objects can be created from blocks).
 */
public class Block extends GameObject {

        public static final int SIZE = 30;

    /**
     * constructor
     * @param topLeftCorner top left corner
     * @param renderable renderable
     */
        public Block(Vector2 topLeftCorner, Renderable renderable) {
            super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
            physics().preventIntersectionsFromDirection(Vector2.ZERO);
            physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        }
}
