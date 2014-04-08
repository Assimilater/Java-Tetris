package Tetris.Forms;

import Tetris.Game;
import Tetris.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	private static final int WIDTH = 1250, HEIGHT = 750, MIN_WIDTH = 500, MIN_HEIGHT = 450;
	private static final String TITLE = "Tetris!";
	private Container pane;
	private JPanel contentPanel;
	
	private JMenuBar menu;
	private JMenu gameMenu, helpMenu;
	private JMenuItem NewGameMenuItem, OptionsMenuItem, ExitMenuItem, HowtoMenuItem, AboutMenuItem;
	
	// This exists because there will only be a single instance that can be tracked statically
	private static MainFrame instance;
	public static MainFrame getThis() { return instance; }
	
	public MainFrame() {
		instance = this;
		
		this.setTitle(TITLE);
		this.setSize(WIDTH, HEIGHT);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(this);
		this.addKeyListener(this);
		
		pane = this.getContentPane();
		contentPanel = new JPanel();
		
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
		
		gameMenu = new JMenu("Game");
		gameMenu.setFont(Program.displayFont);
		gameMenu.add(NewGameMenuItem);
		//gameMenu.add(OptionsMenuItem);
		gameMenu.add(ExitMenuItem);
		
		helpMenu = new JMenu("Help");
		helpMenu.setFont(Program.displayFont);
		//helpMenu.add(HowtoMenuItem);
		helpMenu.add(AboutMenuItem);
		
		menu = new JMenuBar();
		menu.add(gameMenu);
		menu.add(helpMenu);
		
		pane.add(menu, BorderLayout.NORTH);
		pane.add(contentPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	public static void setGamePanel(Game panel) {
		instance.contentPanel.removeAll();
		if (panel != null) {
			instance.contentPanel.add(panel);
		}
	}
	
	// boolean confirmExit() show a confirmation if a game is active before closing the program
	private static boolean confirmQuit(String title) {
		return
			!Game.isActive() ||
			JOptionPane.NO_OPTION != JOptionPane.showConfirmDialog(instance, "Quit the Current Game?", "Confirm " + title, JOptionPane.YES_NO_OPTION);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == NewGameMenuItem) {
			if (confirmQuit("New Game")) {
				new Game();
			}
		}
		else if (e.getSource() == OptionsMenuItem) {
			new Options();
		}

		else if (e.getSource() == ExitMenuItem) {
			if (confirmQuit("Exit")) {
				System.exit(0);
			}
		}
		else if (e.getSource() == HowtoMenuItem) {
			new Instructions();
		}
		else if (e.getSource() == AboutMenuItem) {
			JOptionPane.showMessageDialog(this,
				"About Tetris:\n" +
				"Student Name:  John Call\n" +
				"Student A#:        A01283897\n" +
				"CS2410 ~ USU",
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
		if (Game.isActive()) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Game.drop();
			}
			else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				Game.hold();
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				Game.shiftLeft();
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				Game.shiftRight();
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP) {
				Game.rotate();
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				Game.accelerate();
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		if (Game.isActive()) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				Game.decelerate();
			}
		}
	}
	public void keyTyped(KeyEvent e) { }
}
