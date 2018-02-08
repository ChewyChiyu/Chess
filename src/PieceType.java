import java.awt.image.BufferedImage;

public enum PieceType {
	PAWN(Texture.pieces[11],Texture.pieces[0]),
	ROOK(Texture.pieces[10],Texture.pieces[1]),
	KNIGHT(Texture.pieces[9],Texture.pieces[2]),
	BISHOP(Texture.pieces[8],Texture.pieces[3]), 
	QUEEN(Texture.pieces[7],Texture.pieces[4]), 
	KING(Texture.pieces[6],Texture.pieces[5]);
	
	BufferedImage whiteDraw;
	BufferedImage blackDraw;
	
	private PieceType(BufferedImage whiteDraw, BufferedImage blackDraw){
		this.whiteDraw = whiteDraw;
		this.blackDraw = blackDraw;
	}
	
	public static PieceType getType(int num){
		switch(num){
		case 1:
			return PieceType.PAWN;
		case 2:
			return PieceType.ROOK;
		case 3:
			return PieceType.BISHOP;
		case 4:
			return PieceType.KNIGHT;
		case 5:
			return PieceType.QUEEN;
		case 6:
			return PieceType.KING;
		}
		return null;
	}
}
