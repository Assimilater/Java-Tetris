package Tetris.Forms;

import Tetris.Game;
import Tetris.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	private static final int WIDTH = 605, HEIGHT = 685;
	private static final String TITLE = "Tetris!";
	
	private JPanel contentPanel;
	private JMenuItem NewGameMenuItem, OptionsMenuItem, ExitMenuItem, HowtoMenuItem, AboutMenuItem;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static MainFrame instance;
	public static MainFrame getThis() { return instance; }
	
	public MainFrame() {
		instance = this;
		
		this.setTitle(TITLE);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(this);
		this.addKeyListener(this);
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBackground(Program.background);
		
		NewGameMenuItem = new JMenuItem("New Game");
		NewGameMenuItem.addActionListener(this);
		NewGameMenuItem.setFont(Program.displayFont);
		NewGameMenuItem.setMnemonic(KeyEvent.VK_N);
		
		OptionsMenuItem = new JMenuItem("Options");
		OptionsMenuItem.addActionListener(this);
		OptionsMenuItem.setFont(Program.displayFont);
		OptionsMenuItem.setMnemonic(KeyEvent.VK_O);
		
		ExitMenuItem = new JMenuItem("Exit");
		ExitMenuItem.addActionListener(this);
		ExitMenuItem.setFont(Program.displayFont);
		ExitMenuItem.setMnemonic(KeyEvent.VK_X);
		
		HowtoMenuItem = new JMenuItem("How to Play");
		HowtoMenuItem.addActionListener(this);
		HowtoMenuItem.setFont(Program.displayFont);
		HowtoMenuItem.setMnemonic(KeyEvent.VK_R);
		
		AboutMenuItem = new JMenuItem("About");
		AboutMenuItem.addActionListener(this);
		AboutMenuItem.setFont(Program.displayFont);
		AboutMenuItem.setMnemonic(KeyEvent.VK_A);
		
		JMenu
		gameMenu = new JMenu("Game");
		gameMenu.setFont(Program.displayFont);
		gameMenu.add(NewGameMenuItem);
		gameMenu.add(OptionsMenuItem);
		gameMenu.add(ExitMenuItem);
		
		JMenu
		helpMenu = new JMenu("Help");
		helpMenu.setFont(Program.displayFont);
		helpMenu.add(HowtoMenuItem);
		helpMenu.add(AboutMenuItem);
		
		JMenuBar
		menu = new JMenuBar();
		menu.add(gameMenu);
		menu.add(helpMenu);
		
		this.getContentPane().add(menu, BorderLayout.NORTH);
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	public static void setGamePanel(Game panel) {
		instance.contentPanel.removeAll();
		if (panel != null) {
			instance.contentPanel.add(panel, BorderLayout.CENTER);
			instance.repaint();
			instance.revalidate();
		}
	}
	
	// boolean confirmExit() show a confirmation if a game is active before closing the program
	private static boolean confirmQuit(String title) {
		Game.pauseGame();
		
		return
			!Game.isActive() ||
			JOptionPane.NO_OPTION != JOptionPane.showConfirmDialog(instance,
				"Quit the Current Game?",
				"Confirm " + title,
				JOptionPane.YES_NO_OPTION
			);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == NewGameMenuItem) {
			if (confirmQuit("New Game")) {
				new Game();
			}
		}
		else if (e.getSource() == OptionsMenuItem) {
			Game.pauseGame();
			new Options();
		}
		
		else if (e.getSource() == ExitMenuItem) {
			if (confirmQuit("Exit")) {
				System.exit(0);
			}
		}
		else if (e.getSource() == HowtoMenuItem) {
			Game.pauseGame();
			JOptionPane.showMessageDialog(this,
				"Keyboard Controls:\n\n" +
					"Left               Move Tetris Block Left\n" +
					"Right             Move Tetris Block Right\n" +
					"Up                  Rotate Tetris Block (Clockwise)\n" +
					"Down            Slide Tetris Block Down\n" +
					"Space           Hard Drop Tetris Block\n" +
					"Shift              Swap Falling Tetris Block with Hold Item (Can't be used twice in a row)\n" +
					"Esc, P           Pause/Resume Game\n" +
					"Ctrl+N, F2     New Game",
				"How to Play",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
		else if (e.getSource() == AboutMenuItem) {
			Game.pauseGame();
			JOptionPane.showMessageDialog(this,
				"About Tetris:\n" +
				"Student Name:  John Call\n" +
				"Student A#:        A01283897\n" +
				"CS 2410 ~ USU",
				"About Tetris",
				JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	
	// WindowListener methods
	public void windowClosing(WindowEvent e) {
		if (confirmQuit("Exit")) {
			System.exit(0);
		}
	}
	public void windowClosed(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	
	// KeyListener methods
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F2 || (e.getKeyCode() == KeyEvent.VK_N && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))) {
			Game.pauseGame();
			if (confirmQuit("New Game")) {
				new Game();
			}
		}
		else if (Game.isActive()) {
			Game.KEY_COMMAND k = null;
			switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					k = Game.KEY_COMMAND.LEFT;
					break;
				
				case KeyEvent.VK_RIGHT:
					k = Game.KEY_COMMAND.RIGHT;
					break;
				
				case KeyEvent.VK_UP:
					k = Game.KEY_COMMAND.ROTATE;
					break;
				
				case KeyEvent.VK_DOWN:
					k = Game.KEY_COMMAND.FALL;
					break;
				
				case KeyEvent.VK_SPACE:
					k = Game.KEY_COMMAND.DROP;
					break;
				
				case KeyEvent.VK_SHIFT:
					k = Game.KEY_COMMAND.HOLD;
					break;
				
				case KeyEvent.VK_ESCAPE: case KeyEvent.VK_P:
					k = Game.KEY_COMMAND.PAUSE;
					break;
			}
			if (k != null) { Game.executeKey(k); }
		}
	}
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
}
