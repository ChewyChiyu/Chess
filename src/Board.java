import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JOptionPane;

public class Board {
	//A 3D Array to keep track of all the pieces on the board and if they have been move: denoted by 1 and 0
	final int[][][] STANDARD_GRID = new int[][][]{{{-2 , 0} , {-4 , 0} , {-3 , 0} , {-5 , 0} , {-6 , 0} , {-3 , 0} , {-4 , 0} , {-2 , 0}},
		{{-1 , 0} , {-1 , 0} , {-1 , 0} , {-1 , 0} , {-1 , 0} , {-1 , 0} , {-1 , 0} , {-1 , 0}},
		{{0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0}},
		{{0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0}},
		{{0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0}},
		{{0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0} , {0 , 0}},
		{{1 , 0} , {1 , 0} , {1 , 0} , {1 , 0} , {1 , 0} , {1 , 0} , {1 , 0} , {1 , 0}},
		{{2 , 0} , {4 , 0} , {3 , 0} , {5 , 0} , {6 , 0} , {3 , 0} , {4 , 0} , {2 , 0}}}; 


		int[][][] grid; 

		GameWindow window;

		//current piece var
		Piece currentlySelected;

		final int SPACER; //minus some for some pixel buffering

		boolean whiteTurn;

		GameState state;


		public Board(int row, int col, GameWindow window){
			grid = new int[row][col][1];
			this.window = window;
			whiteTurn = true;
			SPACER = (int) (((window.gameDim.height/grid.length)) * 0.97);
			state = GameState.IDLE;
			resetBoard(row,col);
			//drawBoard(grid);
		}

		void resetBoard(int row, int col){ //reseting pieces to standard board

			grid = getClone(STANDARD_GRID); //getting clone of standard board

		}

		void pickUpAt(Point pos){
			if(pos == null){return; }// null guard
			int key = grid[pos.x][pos.y][0]; //getting key from pos
			if(key == 0){ return; } //tile guard
			if(whiteTurn && key < 0) { return; } //wrong turn guard
			if(!whiteTurn && key > 0) { return; } //wrong turn guard
			if(state == GameState.CHECKMATE_BLACK || state == GameState.CHECKMATE_WHITE){ return; } //gameover guard 
			if(currentlySelected != null){ //selected guard
				if(currentlySelected.inAnimation){ return; } //animate guard
			}
			window.clickAlpha = !window.clickAlpha; //boolean flip
			currentlySelected = new Piece(key, (key < 0) ? false : true, pos, this); //adjusting currentlySelected to new var
			window.repaint(); //repainting
		}
		
		int[][][] futureMove(Point from, Point to, int[][][] grid){
			int[][][] gridClone = getClone(grid); //artificial move
			int key = gridClone[from.x][from.y][0];
			gridClone[from.x][from.y][0] = 0; //future pos
			gridClone[from.x][from.y][1] = 0;
			gridClone[to.x][to.y][0] = key;
			gridClone[to.x][to.y][1] = 1;
			return gridClone;
		}
		
		
		void dropOffAt(Point pos){
			if(!validMoveTo(currentlySelected.initialPos,pos, currentlySelected.type, currentlySelected.isWhite, currentlySelected.key, grid)) { return; } // not a valid move guard
			//checking to see if spot will result in a check
			
			int[][][] gridClone = futureMove(currentlySelected.initialPos, pos, grid); //chceking to see if future position will be check
			
			if(getState(gridClone) == GameState.CHECK_BLACK && !currentlySelected.isWhite){
				if(currentlySelected.initialPos.x != pos.x || currentlySelected.initialPos.y != pos.y){ //allow if putting down in check
					return;
				}
			}
			if(getState(gridClone) == GameState.CHECK_WHITE && currentlySelected.isWhite){
				if(currentlySelected.initialPos.x != pos.x || currentlySelected.initialPos.y != pos.y){ //allow if putting down in check
					return;
				}
			}			

			window.clickAlpha = !window.clickAlpha;//boolean flip


			//queue in currentlySelected postions
			currentlySelected.inSelection = false;
			currentlySelected.finalPos = pos;
			//clear initial grid space
			grid[currentlySelected.initialPos.x][currentlySelected.initialPos.y][0] = 0; //reseting key
			grid[currentlySelected.initialPos.x][currentlySelected.initialPos.y][1] = 0; //reseting first pick mask
			//begin animation
			currentlySelected.animate();
		}

		void updateGameState(){ //checking to see the game State
			state = scanForState(grid); //white , black scan for check
			//if there is a check or checkMate, notify with GUI
			if(state != GameState.IDLE){
				JOptionPane.showMessageDialog(window,
					    "" + state,
					    "Game State Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
			
		}			
		
		GameState scanForState(int[][][] grid){
			Point kingPosW = new Point();
			Point kingPosB = new Point();
			Point possibleCheck = new Point();
			GameState state = GameState.IDLE;
			//scanning arr for king
			//instance of inCheck() method here to initialize variables
			kingPosW = getKingLocation(true,grid);
			kingPosB = getKingLocation(false,grid);

			//scanning arr for possibleCheck points
			for(int row = 0; row < grid.length; row++){
				for(int col = 0; col < grid[0].length; col++){
					if(grid[row][col][0] < 0){ //black pos
						if(validMoveTo(new Point(row,col), kingPosW, PieceType.getType(Math.abs(grid[row][col][0])), false, grid[row][col][0], grid )){
							state = GameState.CHECK_WHITE;
							possibleCheck = new Point(row,col);
						}
					}
					if(grid[row][col][0] > 0){ //white pos
						if(validMoveTo(new Point(row,col), kingPosB, PieceType.getType(Math.abs(grid[row][col][0])), true, grid[row][col][0], grid )){
							state = GameState.CHECK_BLACK;
							possibleCheck = new Point(row,col);
						}
					}
				}

			}
			
			if(state == GameState.IDLE){ return state; } //clear, not in check
			
			//if it made it this far check for checkmate
			boolean isWhite = (state == GameState.CHECK_WHITE) ? true : false;
			int kingKey = (state == GameState.CHECK_WHITE) ? 6 : -6;
			Point kingPos = (state == GameState.CHECK_WHITE) ? kingPosW : kingPosB;

			//check Path
			Point[] checkPath = getPath(possibleCheck,kingPos);
			boolean checkMate = true;

			//first try moving the checked king, forshadowing possible recovery
			Point[] kingStruggle = new Point[]{new Point(kingPos.x+1, kingPos.y),new Point(kingPos.x-1, kingPos.y),new Point(kingPos.x, kingPos.y-1),new Point(kingPos.x, kingPos.y+1), new Point(kingPos.x-1, kingPos.y-1),new Point(kingPos.x+1, kingPos.y-1),new Point(kingPos.x-1, kingPos.y+1),new Point(kingPos.x+1, kingPos.y+1)};
			for(Point p : kingStruggle){
				try{
					if(validMoveTo(kingPos ,p, PieceType.getType(Math.abs(grid[kingPos.x][kingPos.y][0])), isWhite, grid[kingPos.x][kingPos.y][0], grid)){
						//seeing if that move would also result in a check
						int[][][] gridClone = futureMove(kingPos, p, grid); //getting possible future of king moving
						if(getState(gridClone) == GameState.IDLE){ //the future move takes game state out of check
							//checkMate avoided
							checkMate = false;
						}

					}
				}catch(Exception e) { } //edge guard
			}

			//second try blocking check path, forshadowing possible recovery moves and if they will also result in check
			for(int row = 0; row < grid.length; row++){
				for(int col = 0; col < grid[0].length; col++){
					if((grid[row][col][0] * kingKey > 0) && grid[row][col][0] != kingKey){ //scanning for allies, not including king
						Point possibleBlock = new Point(row,col);
						for(Point p : checkPath){
							if(validMoveTo(possibleBlock , p, PieceType.getType(Math.abs(grid[row][col][0])), isWhite, grid[row][col][0], grid)){
								//seeing if that move would also result in a check
								int[][][] gridClone = futureMove(possibleBlock, p, grid); //getting possible future of a recovery move
								if(getState(gridClone) == GameState.IDLE){  //the future move takes game state out of check
									//checkMate avoided
									checkMate = false;
								}
							}
						}
					}

				}


			}
			if(checkMate){ // if boolean flag is triggered then there is a checkMate
				state = (isWhite) ? GameState.CHECK_WHITE : GameState.CHECKMATE_BLACK;
			}
			return state; 
		}

		
		Point getKingLocation(boolean isWhite, int[][][] grid){
			int lookingFor = (isWhite) ? 6 : -6;
			for(int row = 0; row < grid.length; row++){
				for(int col = 0; col < grid[0].length; col++){
					if(grid[row][col][0] == lookingFor){
						return new Point(row,col);
					}
				}
			}
			return null;
		}
		

		int[][][] getClone(int[][][] grid){
			int[][][] cloned = new int[grid.length][grid[0].length][grid[0][0].length];
			for(int x = 0; x < cloned.length; x++){
				for(int y = 0; y < cloned[0].length; y++){
					for(int z = 0; z < cloned[0][0].length; z++){
						cloned[x][y][z] = grid[x][y][z];
					}
				}
			}
			return cloned;
		}

		GameState getState(int[][][] grid){
			Point kingPosW = new Point();
			Point kingPosB = new Point();
			
			//scanning arr for king
			//instance of inCheck() method here to initialize variables
			kingPosW = getKingLocation(true,grid);
			kingPosB = getKingLocation(false,grid);

			//scanning arr for possibleCheck points
			for(int row = 0; row < grid.length; row++){
				for(int col = 0; col < grid[0].length; col++){
					if(grid[row][col][0] < 0){ //black pos
						if(validMoveTo(new Point(row,col), kingPosW, PieceType.getType(Math.abs(grid[row][col][0])), false, grid[row][col][0], grid )){
							return GameState.CHECK_WHITE;
						}
					}
					if(grid[row][col][0] > 0){ //white pos
						if(validMoveTo(new Point(row,col), kingPosB, PieceType.getType(Math.abs(grid[row][col][0])), true, grid[row][col][0], grid )){
							return GameState.CHECK_BLACK;
						}
					}
				}

			}
			return GameState.IDLE;
		}


		boolean validMoveTo(Point o , Point p, PieceType type, boolean isWhite, int key, int[][][] grid){  //switch of piece type

			//first checking to see if trying to take already set point
			if(o.x == p.x && o.y == p.y){
				return true;
			}

			//trying to take own team
			if((grid[p.x][p.y][0] * grid[o.x][o.y][0] > 0) && (grid[p.x][p.y][0] * grid[o.x][o.y][0] != 0)){
				return false;
			}


			switch(type){
			case BISHOP:
				if(Math.abs(p.x-o.x) != Math.abs(p.y-o.y)){ //improper diagonal
					return false;
				}
				if(!straightPathClear(o,p, grid)){ 
					return false;
				}
				break;
			case KING:
				if(Math.abs(p.x-o.x) > 1 || Math.abs(p.y-o.y) > 1 ){ // one space move
					return false;
				}
				break;
			case KNIGHT:
				if(Math.abs(p.x-o.x) * Math.abs(p.y-o.y) != 2){ //not L shape: against rules
					return false;
				}
				break;
			case PAWN:
				//checking to see if move in right direction
				if(isWhite){
					if(p.x-o.x > 0){
						return false;
					}
				}else{
					if(p.x-o.x < 0){
						return false;
					}
				}

				//checking for proper forward
				if(grid[o.x][o.y][1] == 0){ // has not moved
					if(Math.abs(p.x-o.x) > 2){
						return false;
					}
					straightPathClear(o,p, grid);
				}else{
					if(Math.abs(p.x-o.x) > 1){
						return false;
					}
				}

				if(Math.abs(p.y-o.y) > 1){
					return false;
				}
				//checking for proper diagonal and forward
				if(Math.abs(p.y-o.y) == 1){
					if(grid[p.x][p.y][0] * key == 0){
						return false;
					}
				}
				if(Math.abs(p.x-o.x) == 1 && Math.abs(p.y-o.y) == 0){
					if(grid[p.x][p.y][0] * key != 0){
						return false;
					}
				}
				break;
			case QUEEN:
				if(Math.abs(p.x-o.x) != 0 && Math.abs(p.y - o.y) != 0){ // a diagonal is formed : against rules
					if(Math.abs(p.x-o.x) != Math.abs(p.y-o.y)){ //improper diagonal
						return false;
					}
				}
				if(!straightPathClear(o,p, grid)){ 
					return false;
				}
				break;
			case ROOK:

				if(Math.abs(p.x-o.x) != 0 && Math.abs(p.y - o.y) != 0){ // a diagonal is formed : against rules
					return false;
				}

				if(!straightPathClear(o,p, grid)){ 
					return false;
				}
				break;
			}
			return true;
		}

		boolean straightPathClear(Point o, Point pos, int[][][] grid){//return true if no piece in straight line / diagonal line, dx and dy must equal
			Point[] path = getPath(o,pos);
			for(int index = 0; index < path.length; index++){
				if(grid[path[index].x][path[index].y][0] != 0){
					if(index != path.length-1){
						return false;
					}
				}
			}
			return true;
		}

		Point[] getPath(Point o, Point pos){
			int dx = pos.x - o.x;
			int dy = pos.y - o.y;
			int moveCount = (dx == 0) ? Math.abs(dy) : Math.abs(dx);
			Point[] pointArr = new Point[moveCount]; //dx == dy anyways

			int incX = 0;
			int incY = 0;

			if(dx < 0){
				incX = -1;
			}else if(dx > 0){
				incX = 1;
			}

			if(dy < 0){
				incY = -1;
			}else if(dy > 0){
				incY = 1;
			}

			int x = o.x;
			int y = o.y;

			for(int index = 0; index < moveCount; index++){
				x += incX;
				y += incY;
				pointArr[index] = new Point(x,y);
			}	
			return pointArr;
		}


		void draw(Graphics g){
			drawGrid(g);
		}

		void drawBoard(int[][][] grid){
			for(int[][] row : grid){
				for(int[] col : row){
					System.out.print(col[0] + "," + col[1] + ((col[0] < 0) ? " |" : "  |")); // spacing out pieces
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

					int key = grid[row][col][0]; //if negative then it is black, 0 is not a piece
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
