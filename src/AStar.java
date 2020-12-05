import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Comparator;

/*
 * A* algorithm to search minimum path in a NxN grid
 * with displacements only adjacent to our position and
 * not diagonally movement but only: "NORTH", "SOUTH", "WEST" and "EAST"
 * @author Dylan Lo Blundo
 */
public class AStar {
	
	private GridWorld gridWorld;
	private	GridWorld.Coordinate startCell;
	private	int size;
	private	int startRow;
	private	int startCol;
	private	int finishRow;
	private	int finishCol;
	
	// boolean array for check visited cells
	private boolean[][] visited;
	
	// two-dimensional array for cell's parents
	private GridWorld.Coordinate[][] cameFrom;
	
	// gScore is cost from start to current cell
	private int gScore[][];
	
	// fScore is cost from current cell to finish
	private int fScore[][];
	
	// Create a PriorityQueue for save heuristic calculation for cell
	PriorityQueue<GridWorld.Coordinate> pqueue = new PriorityQueue<GridWorld.Coordinate>(new Comparator<GridWorld.Coordinate>() {
		
		/* 
		 * Compare best cell for best path, where gScore is my heuristicCacl from cell start to current cell
		 * and the first addendum is the heuristic calculation from current cell to finish cell 
		 */
		@Override
		public int compare(GridWorld.Coordinate cell1, GridWorld.Coordinate cell2) {
			int cell1Cost = heuristicCalc(cell1.row, cell1.col, finishRow, finishCol) + gScore[cell1.row][cell1.col];
			int cell2Cost = heuristicCalc(cell2.row, cell2.col, finishRow, finishCol) + gScore[cell2.row][cell2.col];
			if(cell1Cost < cell2Cost) {
				return -1;
			} else if(cell1Cost == cell2Cost) {
				return 0;
			} else {
				return 1;
			}
		}
	});
	
	/*
	 *  Constructor
	 */
	public AStar (GridWorld gridWorld, int size) {
		this.gridWorld = gridWorld;
		this.size = size;
		this.startCell = gridWorld.getCurrentCell();
		this.startRow = gridWorld.getCurrentCell().row;
		this.startCol = gridWorld.getCurrentCell().col;
		
		// Destination is always at the bottom right
		this.finishCol = size;
		this.finishRow = size;
		
		this.gScore = new int[size][size];
		this.fScore = new int[size][size];
		this.visited = new boolean[size][size];
		this.cameFrom = new GridWorld.Coordinate[size][size];
		
		// Initializes visited to false and cameFrom to null
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				this.visited[i][j] = false;
				this.cameFrom[i][j] = null;
			}
		}
	}
	
	/*
	 *  Method for reconstruct my path from start to finish cell
	 */
	private ArrayList<GridWorld.Coordinate> reconstructPath (GridWorld.Coordinate finishCell) {
		ArrayList<GridWorld.Coordinate> totalPath = new ArrayList<GridWorld.Coordinate>();
		int i = finishCell.row;
		int j = finishCell.col;
		while(cameFrom[i][j].row + cameFrom[i][j].col != 0) {
			GridWorld.Coordinate node = cameFrom[i][j];
			i = node.row;
			j = node.col;
			totalPath.add(0, node);
		}
		totalPath.add(0, startCell);
		totalPath.add(finishCell);
		return totalPath;
	}
	
	/*
	 * Heuristic calculation
	 */
	private int heuristicCalc (int startRow, int startCol, int finishRow, int finishCol) {
		int heuristic = ((finishRow + finishCol) - (startRow + startCol)) - 2;
		return heuristic;
	}
	
	/*
	 * Move method for check if the next cell to go is adjacent
	 */
	private boolean moveTo (GridWorld.Coordinate nextPosition) {
		int nextX = nextPosition.row;
		int nextY = nextPosition.col;
		int x = gridWorld.getCurrentCell().row;
		int y = gridWorld.getCurrentCell().col;
		if((x == nextX - 1) && (y == nextY)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.SOUTH);
			visited[nextX][nextY] = true;
			return true;
		} else if((x == nextX + 1) && (y == nextY)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.NORTH);
			visited[nextX][nextY] = true;
			return true;
		} else if((y == nextY - 1) && (x == nextX)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.EAST);
			visited[nextX][nextY] = true;
			return true;
		} else if((y == nextY + 1) && (x == nextX)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.WEST);
			visited[nextX][nextY] = true;
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Method that uses A* algorithm to search the shortest path 
	 */
	public ArrayList<GridWorld.Coordinate> pathFinder (GridWorld gridWorld) {
		pqueue.add(gridWorld.getCurrentCell());
		
		// Initializes gScore and fScore
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if((i == 0) && (j == 0)) {
					gScore[i][j] = 0;
				}
				gScore[i][j] = Integer.MAX_VALUE;
			}
		}
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if((i == 0) && (j == 0)) {
					fScore[i][j] = heuristicCalc(startRow, startCol, finishRow, finishCol);
				}
				fScore[i][j] = Integer.MAX_VALUE;
			}
		}
		
		// Initializes the starting cell to true 
		visited[startRow][startCol] = true;
		
		// Start A* algorithm
		while(!pqueue.isEmpty()) {
			
			/*
			 *  Until I have reached the cell in the first position of the priority queue,
			 *  I call DFS for to go back 
			 */
			if((pqueue.isEmpty() == false) && (moveTo(pqueue.peek()) == false)) {
				DFS dfs = new DFS (gridWorld, size, gridWorld.getCurrentCell(), pqueue.peek());
				dfs.dfsStart(visited);
				pqueue.remove();
			}
			
			// If I achieved the goal, return the path
			if(gridWorld.targetReached() == true) {
				return reconstructPath(gridWorld.getCurrentCell());
			}
			
			// Add adjacent cell into the priority queue
			for(GridWorld.Coordinate cell : gridWorld.getAdjacentFreeCells()) {	
				
				int tentative_gScore = gScore[gridWorld.getCurrentCell().row][gridWorld.getCurrentCell().col] + 1;
				if(tentative_gScore < gScore[cell.row][cell.col]) {
					cameFrom[cell.row][cell.col] = gridWorld.getCurrentCell();
					gScore[cell.row][cell.col] = tentative_gScore;
					fScore[cell.row][cell.col] = gScore[cell.row][cell.col] + heuristicCalc(cell.row,cell.col, finishRow, finishCol);
					
					// Don't add the same cell and the cell where I was before
					if((pqueue.contains(cell) == false)) {
						pqueue.add(cell);
					}
				}	
			}
		}
		
		// if path never found
		return null;
	}
}