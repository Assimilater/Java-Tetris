package Tetris;

import Tetris.Forms.MainFrame;
import Tetris.Forms.Options;
import Tetris.Structures.Block;
import Tetris.Structures.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class Game extends JPanel implements ActionListener {
	public enum KEY_COMMAND { LEFT, RIGHT, ROTATE, HOLD, FALL, DROP, PAUSE }
	
	private static final int FALL_RATE = 1000, QUEUE_SIZE = 5;
	
	// boolean flag "holdUsed" in case the user tries to stall by switching between hold back and forth
	private boolean holdUsed, paused;
	private Timer fallTimer;
	
	private Options.Difficulty difficulty;
	private Boolean multipleLives;
	private Integer lives;
	
	private JButton resumeButton;
	
	private Block holdBlock, gameBlock;
	private Block[] inQueueBlock;
	
	private Grid gameGrid, holdGrid;
	private Grid[] inQueueGrid;

	private static HashMap<Boolean, HashMap<Options.Difficulty, Integer>> highScore;
	static {
		highScore = new HashMap<Boolean, HashMap<Options.Difficulty, Integer>>();
		highScore.put(true, new HashMap<Options.Difficulty, Integer>());
		for (Options.Difficulty d : Options.Difficulty.values()) {
			highScore.get(true).put(d, 0);
		}
		highScore.put(false, new HashMap<Options.Difficulty, Integer>());
		for (Options.Difficulty d : Options.Difficulty.values()) {
			highScore.get(false).put(d, 0);
		}
	}
	
	private Integer level, linesToLevel, score, consecutive;
	private JLabel livesCountLabel, levelCountLabel, linesCountLabel, scoreCountLabel, highScoreCountLabel;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static Game game;
	public static boolean isActive() { return game != null; }
	
	public Game() {
		if (game != null) { game.fallTimer.stop(); }
		
		game = this;
		this.setLayout(null);
		this.setOpaque(false);
		
		difficulty = Options.difficulty;
		multipleLives = Options.multipleLives;
		lives = multipleLives ? 3 : 1;
		
		fallTimer = new Timer(FALL_RATE, this);
		holdUsed = false;
		
		resumeButton = new JButton("Click to Resume");
		resumeButton.setFont(Program.displayFont);
		resumeButton.setBounds(215, 290, 175 ,30); //gameGrid.setBounds(150, 10, 305, 600);
		resumeButton.addActionListener(this);
		
		JLabel
		livesLabel = new JLabel("Lives:");
		livesLabel.setForeground(Program.foreground);
		livesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		livesLabel.setFont(Program.displayFont(Font.BOLD, 5));
		livesLabel.setBounds(25, 225, 100, 20);
		
		livesCountLabel = new JLabel("");
		livesCountLabel.setForeground(Color.YELLOW);
		livesCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		livesCountLabel.setFont(Program.displayFont(Font.BOLD, 10));
		livesCountLabel.setBounds(25, 250, 100, 30);
		
		if (multipleLives) {
			this.add(livesLabel);
			this.add(livesCountLabel);
		}
		
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
		
		JLabel
		scoreLabel = new JLabel("Score:");
		scoreLabel.setForeground(Program.foreground);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setFont(Program.displayFont(Font.BOLD, 5));
		scoreLabel.setBounds(25, 450, 100, 20);
		this.add(scoreLabel);
		
		scoreCountLabel = new JLabel("");
		scoreCountLabel.setForeground(Color.decode("#6ED3FF"));
		scoreCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreCountLabel.setFont(Program.displayFont(Font.BOLD, 10));
		scoreCountLabel.setBounds(25, 475, 100, 30);
		this.add(scoreCountLabel);
		
		JLabel
		highScoreLabel = new JLabel("Score:");
		highScoreLabel.setForeground(Program.foreground);
		highScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		highScoreLabel.setFont(Program.displayFont(Font.BOLD, 5));
		highScoreLabel.setBounds(25, 525, 100, 20);
		this.add(highScoreLabel);
		
		highScoreCountLabel = new JLabel("");
		highScoreCountLabel.setForeground(Color.RED);
		highScoreCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		highScoreCountLabel.setFont(Program.displayFont(Font.BOLD, 10));
		highScoreCountLabel.setBounds(25, 550, 100, 30);
		this.add(highScoreCountLabel);
		
		consecutive = 0;
		score = 0;
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
	private boolean updateGameLevel() {
		if (linesToLevel <= 0) {
			int delay;
			switch (difficulty) {
				case Normal:
					++level;
					linesToLevel += (int)(level * 1.25) + 5;
					
					// Level 1 fall rate is 1 second, ever level thereafter decrease by 50 ms
					delay = FALL_RATE - (75 * level);
					break;
				
				case Hard:
					++level;
					linesToLevel += level * 2 + 5;
					
					// Level 1 fall rate is 1 second, ever level thereafter is 50% faster
					delay = (int)(FALL_RATE * Math.pow(1.5, 1 - level));
					break;
				
				default:
					delay = FALL_RATE;
			}
			
			// Let's not allow the timer to get too low...
			if (delay <= 10) {
				gameOver(true);
				return false;
			}
			fallTimer.setDelay(delay);
		}
		updateLabels();
		
		return true;
	}
	
	private void updateLabels() {
		livesCountLabel.setText(lives.toString());
		levelCountLabel.setText(level.toString());
		linesCountLabel.setText(linesToLevel.toString());
		highScoreCountLabel.setText(highScore.get(multipleLives).get(difficulty).toString());
		scoreCountLabel.setText(score.toString());
	}
	
	private boolean getNext() {
		holdUsed = false;
		
		// Dequeue the head element
		gameBlock = inQueueBlock[0];
		if (!insertBlock(gameBlock)) { return false; }
		
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

	private boolean insertBlock(Block gameBlock) {
		if (!gameBlock.insert(gameGrid)) {
			--lives;
			if (lives != 0) {
				gameGrid.wipe();
				return gameBlock.insert(gameGrid);
			}
			return false;
		}
		return true;
	}

	// Called when a block "sinks into place"
	public static void sink(int row) {
		game.fallTimer.stop();
		
		int lines = 0;
		try {
			lines = game.gameGrid.collapseAbove(row);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Give bonus points for consecutive line removals
		if (lines == 0) {
			game.consecutive = 1;
		}
		else {
			++game.consecutive;
			
			// Score weighs number of lines removed twice as heavily but still gives a noticeable bonus for consecutive completions
			game.score += (lines * game.level * 10) + (game.consecutive * game.level * 5);
		}
		
		game.linesToLevel -= lines;
		
		if (game.updateGameLevel()) {
			if (game.getNext()) {
				game.fallTimer.start();
			} else {
				game.gameOver(false);
			}
		}
	}
	
	private void hold() {
		if (holdUsed) {
			// TODO: Alert the user they can't switch again with perhaps a sound
			return;
		}
		
		game.fallTimer.stop();
		
		if (holdBlock != null) {
			Block temp = gameBlock;
			
			gameBlock = holdBlock;
			holdBlock = temp;
			
			gameBlock.freeGrid();
			holdBlock.insert(holdGrid);
			if (!insertBlock(gameBlock)) {
				gameOver(false);
				return;
			}
		}
		else {
			holdBlock = gameBlock;
			holdBlock.insert(holdGrid);
			
			if (!getNext()) {
				gameOver(false);
				return;
			}
		}
		
		holdUsed = true;
		fallTimer.start();
	}
	
	private void gameOver(boolean completed) {
		boolean beatRecord = score > highScore.get(multipleLives).get(difficulty);
		if (beatRecord) {
			highScore.get(multipleLives).put(difficulty, score);
		}
		JOptionPane.showMessageDialog(MainFrame.getThis(),
			"Congratulations!\n" +
				(!completed
					?
					"You made it to:\n" +
					"Level " + level
					:
					"You completed all available levels!\n" +
					"You must be superman or something!"
				) + 
				(beatRecord
					?
					"\n\nYou even beat the high score!\n" +
					"Most Impressive!"
					:
					""
				),
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
