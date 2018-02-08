import java.awt.Dimension;

public class ChessLauncher {
	
	final static Dimension gameDim = new Dimension(950,800);
	
	public static void main(String[] args){
		new ChessLauncher();
	}
	
	ChessLauncher(){
		new Texture(); //loading texture
		new GameWindow(gameDim);
	}
	
}
