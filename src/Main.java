import java.util.ArrayList;

/*
 * Main function for a pathfinding algorithm in a NxN grid
 * with displacements only adjacent to our position and
 * not diagonally movement but only: "NORTH", "SOUTH", "WEST" and "EAST"
 * @author Dylan Lo Blundo
 */
public class Main {

	public static void main(String[] args) {
		
		// Elaborate main parameters and convert them
		int size = Integer.parseInt(args[0].trim());
		double density = Double.parseDouble(args[1].trim());
		long seed = Long.parseLong(args[2].trim());
		
		// Check if the parameters are ok
		if((size <= 0) || ((density < 0) || (density > 1)) || (args.length < 3)) {
			System.exit(0);
		}

		GridWorld gridWorld = new GridWorld(size, density, seed);
		AStar explorer = new AStar(gridWorld, size);
		ArrayList<GridWorld.Coordinate> path = new ArrayList<GridWorld.Coordinate>();
		
		/* Explore the grid to find best path and print it
		 * if and only if the path is acyclic
		 */
		path = explorer.pathFinder(gridWorld);
		if((gridWorld.checkPath(path)) && (gridWorld.checkPathAcyclic(path))) {
			System.out.print("Percorso: ");
			for(GridWorld.Coordinate cell : path) {
				System.out.print(cell + " ");
	        }
		} else {
			System.out.println("Nessun percorso!");
		}
	}
}