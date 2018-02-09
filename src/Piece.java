import java.awt.Point;

public class Piece {

	PieceType type;
	Point initialPos;
	Point finalPos;

	Point inMotion;

	int key;

	boolean isWhite;

	boolean inSelection = true;
	boolean inAnimation = false;

	Board b;

	public Piece(int key,  boolean isWhite, Point initialPos, Board b){
		this.type = PieceType.getType(Math.abs(key));
		this.key = key;
		this.initialPos = initialPos;
		this.inMotion = new Point(initialPos.y * b.SPACER, initialPos.x * b.SPACER); // x , y translation not r , c 
		this.finalPos = new Point(-1,-1);
		this.isWhite = isWhite;
		this.b = b;
	}

	void animate(){ //Thread based draw and move

		final int ANIMATION_SLEEP = 1;
		final int ANIMATION_CYCLE = b.SPACER;

		Thread movePiece = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				inAnimation = true;
				double dx =  (finalPos.getX()) - (initialPos.getX());
				double dy =  (finalPos.getY()) - (initialPos.getY());
				for(int index = 0; index < ANIMATION_CYCLE; index++){ // 100 cycles
					inMotion.translate((int) (dy), (int) (dx));  //not actually perfect motion, illusion 
					b.window.repaint();
					try{ Thread.sleep(ANIMATION_SLEEP); } catch(Exception e) { }
				}
				//animation over
				inAnimation = false;
				//apply real value to solid board
				b.grid[finalPos.x][finalPos.y][0] = key;
				//no longer first move of piece, turning 0 flag to 1
				b.grid[finalPos.x][finalPos.y][1] = 1;
				//repaint

				b.window.repaint();
				//drawing grid
				b.drawBoard();
			}

		});
		movePiece.start();
	}
	boolean validMoveTo(Point p){  //switch of piece type

		//first checking to see if trying to take already set point
		if(initialPos.x == p.x && initialPos.y == p.y){
			return true;
		}

		//trying to take own team
		if((b.grid[p.x][p.y][0] * b.grid[initialPos.x][initialPos.y][0] > 0) && (b.grid[p.x][p.y][0] * b.grid[initialPos.x][initialPos.y][0] != 0)){
			return false;
		}

		
		switch(type){
		case BISHOP:
			if(Math.abs(p.x-initialPos.x) != Math.abs(p.y-initialPos.y)){ //improper diagonal
				return false;
			}
			if(!straightPathClear(p)){ 
				return false;
			}
			break;
		case KING:
			if(Math.abs(p.x-initialPos.x) > 1 || Math.abs(p.y-initialPos.y) > 1 ){ // one space move
				return false;
			}
			break;
		case KNIGHT:
			if(Math.abs(p.x-initialPos.x) * Math.abs(p.y-initialPos.y) != 2){ //not L shape: against rules
				return false;
			}
			break;
		case PAWN:
			//checking to see if move in right direction
			if(isWhite){
				if(p.x-initialPos.x > 0){
					return false;
				}
			}else{
				if(p.x-initialPos.x < 0){
					return false;
				}
			}
			
			//checking for proper forward
			if(b.grid[initialPos.x][initialPos.y][1] == 0){ // has not moved
				if(Math.abs(p.x-initialPos.x) > 2){
					return false;
				}
				straightPathClear(p);
			}else{
				if(Math.abs(p.x-initialPos.x) > 1){
					return false;
				}
			}
			
			if(Math.abs(p.y-initialPos.y) > 1){
				return false;
			}
			//checking for proper diagonal and forward
			if(Math.abs(p.y-initialPos.y) == 1){
				if(b.grid[p.x][p.y][0] * key == 0){
					return false;
				}
			}
			if(Math.abs(p.x-initialPos.x) == 1 && Math.abs(p.y-initialPos.y) == 0){
				if(b.grid[p.x][p.y][0] * key != 0){
					return false;
				}
			}
			break;
		case QUEEN:
			if(Math.abs(p.x-initialPos.x) != 0 && Math.abs(p.y - initialPos.y) != 0){ // a diagonal is formed : against rules
				if(Math.abs(p.x-initialPos.x) != Math.abs(p.y-initialPos.y)){ //improper diagonal
					return false;
				}
			}

			if(!straightPathClear(p)){ 
				return false;
			}
			break;
		case ROOK:

			if(Math.abs(p.x-initialPos.x) != 0 && Math.abs(p.y - initialPos.y) != 0){ // a diagonal is formed : against rules
				return false;
			}

			if(!straightPathClear(p)){ 
				return false;
			}
			break;
		}
		return true;
	}

	boolean straightPathClear(Point pos){//return true if no piece in straight line / diagonal line, dx and dy must equal
		int dx = pos.x - initialPos.x;
		int dy = pos.y - initialPos.y;

		int moveCount = (dx == 0) ? Math.abs(dy) : Math.abs(dx);

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

		int x = initialPos.x;
		int y = initialPos.y;

		for(int index = 0; index < moveCount; index++){
			x += incX;
			y += incY;
			if(b.grid[x][y][0] != 0){ //a piece was in the way
				//first checking to see if trying to take already set point
				if(index != moveCount-1){
					return false;
				}
			}
		}	
		return true;
	}

}
