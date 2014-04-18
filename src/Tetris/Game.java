package Tetris;

import Tetris.Forms.MainFrame;
import Tetris.Forms.Options;
import Tetris.Structures.Block;
import Tetris.Structures.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game extends JPanel implements ActionListener {
	public enum KEY_COMMAND { LEFT, RIGHT, ROTATE, HOLD, FALL, DROP, PAUSE }
	
	private static final int FALL_RATE = 1000, QUEUE_SIZE = 5;
	
	// boolean flag "holdUsed" in case the user tries to stall by switching between hold back and forth
	private boolean holdUsed, paused;
	private Timer fallTimer;
	
	private Options.Difficulty difficulty;
	
	private JButton resumeButton;
	
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
		if (game != null) { game.fallTimer.stop(); }
		
		game = this;
		this.setLayout(null);
		this.setOpaque(false);
		
		difficulty = Options.difficulty;
		
		fallTimer = new Timer(FALL_RATE, this);
		holdUsed = false;
		
		resumeButton = new JButton("Click to Resume");
		resumeButton.setFont(Program.displayFont);
		resumeButton.setBounds(215, 290, 175 ,30); //gameGrid.setBounds(150, 10, 305, 600);
		resumeButton.addActionListener(this);
		
		JLabel
		levelLabel = new JLabel("Level:");
		levelLabel.setForeground(Program.foreground);
		levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		levelLabel.setFont(Program.displayFont(Font.BOLD, 5));
		levelLabel.setBounds(25, 300, 100, 20);
		this.add(levelLabel);
		
		levelCountLabel = new JLabel("");
		levelCountLabel.setForeground(Color.decode("#6ED3FF"));
		levelCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		levelCountLabel.setFont(Program.displayFont(Font.BOLD, 10));
		levelCountLabel.setBounds(25, 325, 100, 30);
		this.add(levelCountLabel);
		
		JLabel
		linesLabel = new JLabel("Goal:");
		linesLabel.setForeground(Program.foreground);
		linesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		linesLabel.setFont(Program.displayFont(Font.BOLD, 5));
		linesLabel.setBounds(25, 375, 100, 20);
		this.add(linesLabel);
		
		linesCountLabel = new JLabel("");
		linesCountLabel.setForeground(Color.RED);
		linesCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		linesCountLabel.setFont(Program.displayFont(Font.BOLD, 10));
		linesCountLabel.setBounds(25, 400, 100, 30);
		this.add(linesCountLabel);
		
		level = 0;
		linesToLevel = 0;
		updateGameLevel();
		
		JLabel
		holdLabel = new JLabel("Hold");
		holdLabel.setForeground(Program.foreground);
		holdLabel.setHorizontalAlignment(SwingConstants.CENTER);
		holdLabel.setFont(Program.displayFont(Font.BOLD, 15));
		holdLabel.setBounds(25, 20, 100, 40);
		this.add(holdLabel);
		
		holdGrid = new Grid(true);
		holdGrid.setBounds(25, 70, 100, 100);
		this.add(holdGrid);
		
		holdBlock = null;
		
		gameGrid = new Grid(false);
		gameGrid.setBounds(150, 10, 305, 600);
		this.add(gameGrid);
		
		gameBlock = new Block(gameGrid);
		
		inQueueGrid = new Grid[QUEUE_SIZE];
		inQueueBlock = new Block[QUEUE_SIZE];
		
		JLabel
		nextLabel = new JLabel("Next");
		nextLabel.setForeground(Program.foreground);
		nextLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nextLabel.setFont(Program.displayFont(Font.BOLD, 15));
		nextLabel.setBounds(480, 20, 100, 40);
		this.add(nextLabel);
		
		for (int i = 0; i < QUEUE_SIZE; ++i) {
			inQueueGrid[i] = new Grid(true);
			inQueueGrid[i].setBounds(480, 70 + 110 * i, 100, 100);
			this.add(inQueueGrid[i]);
			
			inQueueBlock[i] = new Block((inQueueGrid[i]));
		}
		
		MainFrame.setGamePanel(this);
		fallTimer.start();
	}
	
	// updateGameLevel - Determine if the number of lines to level up has been reached, if so update the game
	private void updateGameLevel() {
		if (linesToLevel <= 0) {
			++level;
			linesToLevel += level * 2 + 5;
			
			// Update fall rate
			switch (difficulty) {
				case Normal:
					// Level 1 fall rate is 1 second, ever level thereafter decrease by 50 ms
					fallTimer.setDelay(FALL_RATE - (50 * level));
					break;
				case Hard:
					// Level 1 fall rate is 1 second, ever level thereafter is 50% faster
					fallTimer.setDelay((int) (FALL_RATE * Math.pow(1.5, 1 - level)));
					break;
			}
		}
		updateLabels();
	}
	
	private void updateLabels() {
		levelCountLabel.setText(level.toString());
		linesCountLabel.setText(linesToLevel.toString());
	}
	
	private boolean getNext() {
		holdUsed = false;
		
		// Dequeue the head element
		gameBlock = inQueueBlock[0];
		if (!gameBlock.insert(gameGrid)) { return false; }
		
		// Shuffle the queue forward
		// There's only four elements in this queue so it's not a noticeable performance hit
		// The benefit to this is it keeps the indices for each block lined up with the panels they're drawn in
		for (int i = 1; i < QUEUE_SIZE; ++i) {
			inQueueBlock[i - 1] = inQueueBlock[i];
			inQueueBlock[i - 1].insert(inQueueGrid[i - 1]);
		}
		
		// Insert a new block at the tail of the queue
		inQueueBlock[QUEUE_SIZE - 1] = new Block(inQueueGrid[QUEUE_SIZE - 1]);
		return true;
	}
	
	// Called when a block "sinks into place"
	public static void sink(int row) {
		game.fallTimer.stop();
		
		try {
			game.linesToLevel -= game.gameGrid.collapseAbove(row);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		game.updateGameLevel();
		
		if (game.getNext()) {
			game.fallTimer.start();
		}
		else {
			game.gameOver();
		}
	}
	
	private void hold() {
		if (holdUsed) {
			// Alert the user they can't switch again
			return;
		}
		
		game.fallTimer.stop();
		
		if (holdBlock != null) {
			Block temp = gameBlock;
			
			gameBlock = holdBlock;
			holdBlock = temp;
			
			gameBlock.freeGrid();
			holdBlock.insert(holdGrid);
			if (!gameBlock.insert(gameGrid)) {
				gameOver();
				return;
			}
		}
		else {
			holdBlock = gameBlock;
			holdBlock.insert(holdGrid);
			
			if (!getNext()) {
				gameOver();
				return;
			}
		}
		
		holdUsed = true;
		fallTimer.start();
	}
	
	private void gameOver() {
		JOptionPane.showMessageDialog(MainFrame.getThis(),
			"Congratulations!\n" +
				"You made it to:\n" +
				"Level " + level,
			"Game Over",
			JOptionPane.INFORMATION_MESSAGE
		);
		
		// Stop the game simply by setting it to null
		game = null;
	}
	
	public static void repaintGrid() {
		if (game != null) {
			game.gameGrid.repaintGrid();
			game.holdGrid.repaintGrid();
			for (int i = 0; i < QUEUE_SIZE; ++i) {
				game.inQueueGrid[i].repaintGrid();
			}
		}
		MainFrame.getThis().repaint();
		MainFrame.getThis().revalidate();
	}
	
	// Let block objects handle the key commands
	public static void executeKey(KEY_COMMAND k) {
		if (game.paused) {
			if (k == KEY_COMMAND.PAUSE) {
				game.resume();
			}
			return;
		}
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
			
			case PAUSE:
				game.pause();
				break;
		}
	}
	
	public static void pauseGame() { if (game != null) { game.pause(); } }
	private void pause() {
		fallTimer.stop();
		paused = true;
		
		this.add(resumeButton);
		
		this.remove(gameGrid);
		this.remove(holdGrid);
		for (int i = 0; i < QUEUE_SIZE; ++i) {
			this.remove(inQueueGrid[i]);
		}
		
		repaintGrid();
		this.add(resumeButton);
	}
	private void resume() {
		this.remove(resumeButton);
		
		this.add(gameGrid);
		this.add(holdGrid);
		for (int i = 0; i < QUEUE_SIZE; ++i) {
			this.add(inQueueGrid[i]);
		}
		
		MainFrame.getThis().requestFocus();
		
		repaintGrid();
		paused = false;
		fallTimer.start();
	}
	
	// ActionListener
	public synchronized void actionPerformed(ActionEvent e) {
		if (e.getSource() == fallTimer) {
			game.gameBlock.fall();
		}
		else if (e.getSource() == resumeButton) {
			game.resume();
		}
	}
}
