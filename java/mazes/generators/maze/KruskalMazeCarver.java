package mazes.generators.maze;

import datastructures.concrete.Graph;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Wall;
import mazes.entities.Room;
import java.util.Random;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.
        ISet<Wall> walls = maze.getWalls();
        Random rand = new Random();
        for (Wall wall : walls) {
            wall.setDistance(rand.nextDouble());
        }
        Graph<Room, Wall> graph = new Graph<>(maze.getRooms(), walls);
        ISet<Wall> bstWall = graph.findMinimumSpanningTree();
        for (Wall wall: bstWall) {
            wall.resetDistanceToOriginal();
        }
        return bstWall;
    }
}
