import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameWindow extends JPanel{

	Dimension gameDim;

	Board gameBoard;

	boolean clickAlpha;

	public GameWindow(Dimension gameDim){
		this.gameDim = gameDim;
		panel();
		gameStart();
		clicks();
	}

	void clicks(){
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(clickAlpha){
					gameBoard.pickUpAt(clickToRC(e.getX(),e.getY()));
				}else{
					gameBoard.dropOffAt(clickToRC(e.getX(),e.getY()));
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});
	}

	Point clickToRC(int x, int y){
		int row = ( y / gameBoard.SPACER);
		int col = ( x / gameBoard.SPACER);
		if(row > gameBoard.grid.length || col > gameBoard.grid[0].length){ //edge guard
			return null;
		}
		return new Point(row,col);
	}

	void gameStart(){
		clickAlpha = true; //reseting clickAlpha bool
		gameBoard = new Board(8,8,this);
		repaint();
	}

	void panel(){
		JFrame frame = new JFrame("Chess");
		frame.add(this);
		frame.setPreferredSize(gameDim);
		this.setLayout(null);
		
		JButton reset = new JButton("Reset");
		reset.setBounds((int)(gameDim.getWidth() * .85), (int)(gameDim.getHeight() * .05), 100, 30);
		reset.addActionListener(e -> {
			gameStart();
		});
		this.add(reset);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		drawSprites(g);
		drawStats(g);
	}
	void drawStats(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Aerial",Font.BOLD,15));
		g.drawString((gameBoard.whiteTurn) ? "TURN: WHITE" : "TURN: BLACK", (int)(gameDim.getWidth() * 0.82), (int)(gameDim.getHeight() * 0.03));
		g.drawString( "" + gameBoard.state , (int)(gameDim.getWidth() * 0.82), (int)(gameDim.getHeight() * 0.05));
	}
	void drawSprites(Graphics g){
		gameBoard.draw(g);
	}
}
