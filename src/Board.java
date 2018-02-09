import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Board {

	final int[][] STANDARD_GRID = new int[][]{{-2 , -4 , -3 , -5 , -6 , -3 , -4 , -2},
		{-1 , -1 , -1 , -1 , -1 , -1 , -1 , -1},
		{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
		{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
		{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
		{0 , 0 , 0 , 0 , 0 , 0 , 0 , 0},
		{1 , 1 , 1 , 1 , 1 , 1 , 1 , 1},
		{2 , 4 , 3 , 6 , 5 , 3 , 4 , 2}};


		int[][] grid; 

		GameWindow window;

		//current piece var
		Piece currentlySelected;

		final int SPACER; //minus some for some pixel buffering

		boolean whiteTurn;

		public Board(int row, int col, GameWindow window){
			grid = new int[row][col];
			this.window = window;
			whiteTurn = true;
			SPACER = (int) (((window.gameDim.height/grid.length)) * 0.97);
			resetBoard(row,col);
			drawBoard();
		}

		void resetBoard(int row, int col){ //reseting pieces to standard board

			grid = STANDARD_GRID.clone(); //getting clone of standard board

		}

		void pickUpAt(Point pos){
			if(pos == null){return; }// null guard
			int key = grid[pos.x][pos.y];
			if(key == 0){ return; } //tile guard
			if(whiteTurn && key < 0) { return; } //wrong turn guard
			if(!whiteTurn && key > 0) { return; } //wrong turn guard
			window.clickAlpha = !window.clickAlpha; //boolean flip
			if(currentlySelected != null){ //selected guard
				if(currentlySelected.inAnimation){ return; } //animate guard
			}
			currentlySelected = new Piece(key, (key < 0) ? false : true, pos, this);
			window.repaint();
		}

		void dropOffAt(Point pos){
			if(!validMove(pos)) { return; } // not a valid move
			whiteTurn = !whiteTurn; //no longer whites turn 
			window.clickAlpha = !window.clickAlpha;//boolean flip
			//queue in currentlySelected postions
			currentlySelected.inSelection = false;
			currentlySelected.finalPos = pos;
			//clear initial grid space
			grid[currentlySelected.initialPos.x][currentlySelected.initialPos.y] = 0;
			//begin animation
			currentlySelected.animate();
		}

		boolean validMove(Point pos){ //this is where the valid move is checked
			return currentlySelected.validMoveTo(pos); //guard of valid position before the actual final position is placed
		}

		void draw(Graphics g){
			drawGrid(g);
		}

		void drawBoard(){
			for(int[] row : grid){
				for(int col : row){
					System.out.print(col + ((col < 0) ? " " : "  ")); // spacing out pieces
				}
				System.out.println();
			}
			System.out.println();
		}

		void drawGrid(Graphics g){

			//draw Tiles first
			int xBuffer = 0;
			int yBuffer = 0;

			for(int row = 0; row < grid.length; row++){
				for(int col = 0; col < grid[0].length; col++){
					//draw tile first
					if(row % 2 == 0){
						g.setColor((col % 2 == 1) ? new Color(118,150,86) : new Color(238,238,210));
					}else{
						g.setColor((col % 2 == 1) ? new Color(238,238,210) : new Color(118,150,86));
					}

					//highlighting possible piece
					if(currentlySelected != null && currentlySelected.inSelection){
						if(currentlySelected.initialPos.x == row && currentlySelected.initialPos.y == col){ //highlight if at index
							g.setColor(new Color(152,251,152));
						}
					}
					g.fillRect(xBuffer, yBuffer, SPACER, SPACER);

					//draw piece second

					int key = grid[row][col]; //if negative then it is black, 0 is not a piece
					if(key != 0){ //0 is a tile
						PieceType type = PieceType.getType(Math.abs(key));
						g.drawImage((key < 0) ? type.blackDraw : type.whiteDraw, xBuffer, yBuffer, SPACER, SPACER,  window);
					}


					//draw animation if possible

					if(currentlySelected != null && currentlySelected.inAnimation){ //has a set goal and exists
						g.drawImage((currentlySelected.isWhite) ? currentlySelected.type.whiteDraw : currentlySelected.type.blackDraw, currentlySelected.inMotion.x, currentlySelected.inMotion.y, SPACER, SPACER,  window);

					}

					xBuffer += SPACER;
				}
				xBuffer = 0;
				yBuffer += SPACER;
			}
		}

}
