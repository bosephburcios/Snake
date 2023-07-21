import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 6;
	int applesEaten = 0;
	int highScore = 0;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random rand;
	
	GamePanel() {
		
		rand = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		
		startGame();
	}
	
	public void startGame() {
		
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			
			g.setColor(Color.red);
			g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i = 0; i < bodyParts; i++) {
				
				if(i == 0) { 
					
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
		} else { gameOver(g); }
	}
	
	public void newApple() {
		
		appleX = rand.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = rand.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}
	
	public void move() { 
	
		for(int i = bodyParts; i > 0; i--) {
			
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
			
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
			
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
			
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() {
		
		if((x[0] == appleX) && y[0] == appleY) {
			
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		
		for(int i = bodyParts; i > 0; i--) {
			
			// if snake head touches body
			if((x[0] == x[i]) && (y[0] == y[i])) { running = false; }
		}
		
		// if snake head touches left wall
		if(x[0] < 0) { running = false; }
		
		// if snake head touches right wall
		if(x[0] > SCREEN_WIDTH - UNIT_SIZE) { running = false; }
		
		// if snake head touches top wall
		if(y[0] < 0) { running = false; }
		
		// if snake head touches bottom wall
		if(y[0] > SCREEN_HEIGHT - UNIT_SIZE) { running = false; }
		
		if(running == false) { timer.stop(); }
	}
	
	public void gameOver(Graphics g) {
		
		if(running == false) {
			
			// Game over text
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 75));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("GAME OVER", (SCREEN_WIDTH - metrics.stringWidth("GAME OVER")) / 2, 300);
			
			// this will display the final score
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics score = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - score.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
			
			// high score
			if(highScore < applesEaten) { highScore = applesEaten; }
			
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics most = getFontMetrics(g.getFont());
			g.drawString("High Score: " + highScore, (SCREEN_WIDTH - most.stringWidth("High Score: " + highScore)) / 2, g.getFont().getSize() + 50);
			
			// try again?
			g.setColor(Color.white);
			g.setFont(new Font("Ink Free", Font.BOLD, 30));
			FontMetrics again = getFontMetrics(g.getFont());
			g.drawString("try again? (y)", (SCREEN_WIDTH - again.stringWidth("try again? (y)")) / 2, 350);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(running) {
			
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			switch(e.getKeyCode()) {
			
			case KeyEvent.VK_LEFT:
				if(direction != 'R') { direction = 'L'; }
				break;
				
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') { direction = 'R'; }
				break;
				
			case KeyEvent.VK_UP:
				if(direction != 'D') { direction = 'U'; }
				break;
				
			case KeyEvent.VK_DOWN:
				if(direction != 'U') { direction = 'D'; }
				break; 
			}
			
			// restart
			if(!running) {
				
				if(e.getKeyCode() == KeyEvent.VK_Y) {
				
					startGame();
					bodyParts = 6;
					applesEaten = 0;
					repaint();			
				}
			}
			
		}
	}
}
