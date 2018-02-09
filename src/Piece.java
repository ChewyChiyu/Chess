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
				postAnimation();
			}

		});
		movePiece.start();
	}

	void postAnimation(){ //run this code after the animation is completed, handling grid loc movement and position guards
		inAnimation = false; //no longer animating
		//apply real value to solid board
		if(Math.abs(key) == 1 && (finalPos.x == 0|| finalPos.x == b.grid.length)){
			key*= 5; //Changing a pawn to queen if on other side
		}
		
		//setting spot in grid
		b.grid[finalPos.x][finalPos.y][0] = key;
		
		//repaint
		b.window.repaint();
				
		//drawing grid
		if(initialPos.x != finalPos.x || initialPos.y != finalPos.y){ //same place guard
			b.whiteTurn = !b.whiteTurn; //no longer whites turn 
			//no longer first move of piece, turning 0 flag to 1
			b.grid[finalPos.x][finalPos.y][1] = 1;
			b.updateGameState(); //updating the state of the game
			//repaint
			b.window.repaint();
		}

		
		
		//b.drawBoard();
	}

}
