package Tetris;

import Tetris.Forms.MainFrame;
import Tetris.Structures.Block;
import Tetris.Structures.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JPanel implements ActionListener {
	public enum KEY_COMMAND { LEFT, RIGHT, ROTATE, HOLD, FALL, DROP }
	
	private static final int FALL_RATE = 1000, QUEUE_SIZE = 4;
	
	// boolean flag "holdUsed" in case the user tries to stall by switching between hold back and forth
	private boolean holdUsed;
	private Timer fallTimer;
	
	private Block holdBlock, gameBlock;
	private Block[] inQueueBlock;
	
	private Grid gameGrid, holdGrid;
	private Grid[] inQueueGrid;
	
	private Integer level, linesToLevel;
	private JLabel levelCountLabel, linesCountLabel;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static Game game;
	public static boolean isActive() { return game != null; }
	
	public Game() {
		game = this;
		this.setLayout(null);
		
		fallTimer = new Timer(FALL_RATE, this);
		holdUsed = false;
		
		JLabel
		levelLabel = new JLabel("Level: ");
		levelLabel.setFont(Program.displayFont);
		levelLabel.setBounds(5, 150, 150, 20);
		this.add(levelLabel);
		
		levelCountLabel = new JLabel("");
		levelCountLabel.setFont(new Font(Program.displayFont.getFontName(), Font.BOLD, 30));
		levelCountLabel.setForeground(Color.BLUE);
		levelCountLabel.setBounds(5, 175, 150, 50);
		this.add(levelCountLabel);
		
		JLabel
		linesLabel = new JLabel("Lines to Level: ");
		linesLabel.setFont(Program.displayFont);
		linesLabel.setBounds(5, 250, 150, 20);
		this.add(linesLabel);
		
		linesCountLabel = new JLabel("");
		linesCountLabel.setFont(new Font(Program.displayFont.getFontName(), Font.BOLD, 30));
		linesCountLabel.setForeground(Color.RED);
		linesCountLabel.setBounds(5, 275, 150, 50);
		this.add(linesCountLabel);
		
		level = 0;
		linesToLevel = 0;
		updateGameLevel();
		
		holdGrid = new Grid(true);
		holdGrid.setBounds(110, 25, 50, 50);
		this.add(holdGrid);
		
		holdBlock = null;
		
		gameGrid = new Grid(false);
		gameGrid.setBounds(200, 25, 330, 600);
		this.add(gameGrid);
		
		gameBlock = new Block(gameGrid);
		
		inQueueGrid = new Grid[QUEUE_SIZE];
		inQueueBlock = new Block[QUEUE_SIZE];
		
		for (int i = 0; i < QUEUE_SIZE; ++i) {
			inQueueGrid[i] = new Grid(true);
			inQueueGrid[i].setBounds(550, 25 + 50 * i, 50, 50);
			this.add(inQueueGrid[i]);
			
			inQueueBlock[i] = new Block((inQueueGrid[i]));
		}
		
		MainFrame.setGamePanel(this);
	}
	
	// updateGameLevel - Determine if the number of lines to level up has been reached, if so update the game
	private void updateGameLevel() {
		if (linesToLevel <= 0) {
			++level;
			linesToLevel += level * 5 + 15;
			
			// Update fall rate: Level 1 fall rate is 1 second, ever level thereafter is half the time
			fallTimer.setDelay((int) (FALL_RATE * Math.pow(.5, level - 1)));
		}
		updateLabels();
	}
	
	private void updateLabels() {
		levelCountLabel.setText(level.toString());
		linesCountLabel.setText(linesToLevel.toString());
	}
	
	private void getNext() {
		holdUsed = false;
		
		// Dequeue the head element
		gameBlock = inQueueBlock[0];
		gameBlock.insert(gameGrid);
		
		// Shuffle the queue forward
		// There's only four elements in this queue so it's not a noticeable performance hit
		// The benefit to this is it keeps the indices for each block lined up with the panels they're drawn in
		for (int i = 1; i < QUEUE_SIZE; ++i) {
			inQueueBlock[i - 1] = inQueueBlock[i];
			inQueueBlock[i - 1].insert(inQueueGrid[i - 1]);
		}
		
		// Insert a new block at the tail of the queue
		inQueueBlock[QUEUE_SIZE - 1] = new Block(inQueueGrid[QUEUE_SIZE - 1]);
	}
	
	// Called when a block "sinks into place"
	public static void sink(int row) {
		game.fallTimer.stop();
		
		//game.linesToLevel -= game.gameGrid.collapseAbove(row);
		game.updateGameLevel();
		
		// TODO: replace false with method call fo overflow check
		if (false) {
			JOptionPane.showMessageDialog(MainFrame.getThis(),
				"Congratulations!\n" +
				"You made it to:\n" +
				"Level " + game.level,
				"Game Over",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			// Stop the game simply by setting it to null
			game = null;
		}
		else {
			game.getNext();
			
			game.fallTimer.start();
		}
	}
	
	private void hold() {
		if (game.holdUsed) {
			// Alert the user they can't switch again
			return;
		}
		
		game.fallTimer.stop();
		
		if (game.holdBlock != null) {
			Block temp = game.gameBlock;
			
			game.gameBlock = game.holdBlock;
			game.holdBlock = temp;
			
			game.gameBlock.insert(game.gameGrid);
			game.holdBlock.insert(game.holdGrid);
		}
		else {
			game.holdBlock = game.gameBlock;
			game.holdBlock.insert(game.holdGrid);
			
			game.getNext();
		}
		
		game.holdUsed = true;
		game.fallTimer.start();
	}
	
	// Let block objects handle the key commands
	public static synchronized void executeKey(KEY_COMMAND k) {
		switch (k) {
			case LEFT:
				game.gameBlock.shiftLeft();
				break;
			
			case RIGHT:
				game.gameBlock.shiftRight();
				break;
			
			case ROTATE:
				game.gameBlock.rotate();
				break;
			
			case FALL:
				game.gameBlock.fall();
				break;
			
			case DROP:
				game.gameBlock.drop();
				break;
			
			case HOLD:
				game.hold();
				break;
		}
	}
	
	// ActionListener
	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getSource() == fallTimer) {
			//game.gameBlock.fall();
		}
	}
}
