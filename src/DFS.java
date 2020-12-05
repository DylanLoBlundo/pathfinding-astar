/*
 * An implementation of DFS algorithm (Depth-First-Search)
 * to move to a non-adjacent cell when the A* algorithm finds a non-adjacent cell 
 * at the head of the priority queue
 * @author Dylan Lo Blundo
 */
public class DFS {
	private	GridWorld gridWorld;
	private	Color[][] color;
	private int startRow;
	private int startCol;
	private int targetRow;
	private int targetCol;

	/*
	 *  Constructor
	 */
	public DFS (GridWorld gridWorld, int size, GridWorld.Coordinate startCell, GridWorld.Coordinate targetCell) {
		this.gridWorld = gridWorld;
		this.startRow = startCell.row;
		this.startCol = startCell.col;
		this.targetRow = targetCell.row;
		this.targetCol = targetCell.col;
		this.color = new Color[size][size];
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				this.color[i][j] = Color.WHITE;
			}
		}
	}
	
	/*
	 *  Move method for check if the next cell to go is adjacent
	 */
	private boolean moveTo (GridWorld.Coordinate nextPosition) {
		int nextX = nextPosition.row;
		int nextY = nextPosition.col;
		int x = gridWorld.getCurrentCell().row;
		int y = gridWorld.getCurrentCell().col;
		if((x == nextX - 1) && (y == nextY)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.SOUTH);
			return true;
		} else if((x == nextX + 1) && (y == nextY)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.NORTH);
			return true;
		} else if((y == nextY - 1) && (x == nextX)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.EAST);
			return true;
		} else if((y == nextY + 1) && (x == nextX)) {
			gridWorld.moveToAdjacentCell(GridWorld.Direction.WEST);
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * DFS algorithm starts
	 */
	public void dfsStart (boolean[][] visited) {
		dfsVisit(startRow, startCol, visited);
	}
	
	/*
	 * This is the main of the DFS algorithm
	 */
	private boolean dfsVisit (int startRow, int startCol, boolean[][] visited) {

		// Color grey for cell we are exploring
		color[startRow][startCol] = Color.GREY;
		
		for(GridWorld.Coordinate cell : gridWorld.getAdjacentFreeCells()) {
			int adjRow = cell.row;
			int adjCol = cell.col;
			GridWorld.Coordinate lastCell = gridWorld.getCurrentCell();
			
			// If adjacent cell is visited or it's our target cell
			if((visited[adjRow][adjCol] == true) || ((adjRow == targetRow) && (adjCol == targetCol))) {
				
				// If color is WHITE then I we didn't visit the adjacent cell
				if(color[adjRow][adjCol] == Color.WHITE) {
					visited[adjRow][adjCol] = true;
					moveTo(cell);
					dfsVisit(adjRow, adjCol, visited);
					if(gridWorld.targetReached() == true) {
						return true;
					}
				}
				if((gridWorld.getCurrentCell().row == targetRow) && (gridWorld.getCurrentCell().col == targetCol)) {
					return true;
				} else {

					// If the adjacent cell has already been visited then come back
					moveTo(lastCell);
				}
			}
		}
		color[startRow][startCol] = Color.BLACK;
		return false;
	}
}
