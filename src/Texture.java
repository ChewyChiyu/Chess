import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Texture {

	final public static BufferedImage[] pieces = new BufferedImage[12];
	
	public Texture(){
		load();
	}
	
	void load(){ //this is where all the images will be loaded
		try{
			BufferedImage chessSpriteSheet = ImageIO.read(getClass().getResource("ChessSpriteSheet.png"));
			pieces[0] = chessSpriteSheet.getSubimage(1737,393,184,239);
			pieces[1] = chessSpriteSheet.getSubimage(1393,393,213,236);
			pieces[2] = chessSpriteSheet.getSubimage(1037,378,251,251);
			pieces[3] = chessSpriteSheet.getSubimage(704,367,258,260);
			pieces[4] = chessSpriteSheet.getSubimage(356,371,287,268);
			pieces[5] = chessSpriteSheet.getSubimage(36,371,260,262);
			pieces[6] = chessSpriteSheet.getSubimage(36,37,260,263);
			pieces[7] = chessSpriteSheet.getSubimage(356,34,287,263);
			pieces[8] = chessSpriteSheet.getSubimage(704,34,258,260);
			pieces[9] = chessSpriteSheet.getSubimage(1037,45,251,250);
			pieces[10] = chessSpriteSheet.getSubimage(1393,60,213,235);
			pieces[11] = chessSpriteSheet.getSubimage(1737,60,184,239);


		}catch(Exception e) { e.printStackTrace(); }
	}
	
}
