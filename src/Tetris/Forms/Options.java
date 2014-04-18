package Tetris.Forms;

import Tetris.Assets;
import Tetris.Game;
import Tetris.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

public class Options extends JDialog implements ActionListener {
	private static final int WIDTH = 325, HEIGHT = 180;
	private static final String
		TITLE = "Tetris Options",
		C_TITLE = "Options Confirmation";
	public static String theme = Assets.defaultTheme;
	
	private Container pane;
	private JComboBox<String> themeSelect;
	private JButton Ok, Cancel;
	
	public Options() {
		pane = this.getContentPane();
		pane.setLayout(null);
		
		this.setTitle(TITLE);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(MainFrame.getThis());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);
		
		JLabel
		themeLabel = new JLabel("Theme:");
		themeLabel.setFont(Program.displayFont);
		themeLabel.setBounds(5, 10, 100, 15);
		this.add(themeLabel);
		
		themeSelect = new JComboBox<String>(new Vector<String>(Assets.themes.keySet()));
		themeSelect.setFont(Program.displayFont);
		themeSelect.setBounds(110, 5, 200, 25);
		themeSelect.setSelectedItem(theme);
		this.add(themeSelect);
		
		Ok = new JButton("Ok");
		Ok.setBounds(5, HEIGHT - 68, (WIDTH - 20) / 2, 30);
		Ok.setMnemonic(KeyEvent.VK_O);
		Ok.setFont(Program.displayFont);
		Ok.addActionListener(this);
		pane.add(Ok);
		
		Cancel = new JButton("Cancel");
		Cancel.setBounds(10 + ((WIDTH - 20) / 2), HEIGHT - 68, (WIDTH - 20) / 2, 30);
		Cancel.setMnemonic(KeyEvent.VK_C);
		Cancel.setFont(Program.displayFont);
		Cancel.addActionListener(this);
		pane.add(Cancel);
		
		this.setVisible(true);
	}
	
	// ActionListener
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Ok) {
			// First update the theme
			theme = (String) themeSelect.getSelectedItem();
			Game.repaintGrid();
			
			// Finally, check all major options that might require a game reset
			boolean requireRestart = false;
			
			Update check = Update.performCheck(this, requireRestart);
			if (check.validate) {
				// Update options
			}
			if (check.restart) {
				new Game();
			}
			if (check.close) {
				this.dispose();
			}
		}
		else if (e.getSource() == Cancel) {
			this.dispose();
		}
	}
	private static class Update {
		public static Update performCheck(Options rel, boolean requireRestart) {
			if (requireRestart && Game.isActive()) {
				int Confirmation = JOptionPane.showConfirmDialog(rel,
					"Some changes will not affect the current game.\n" +
						"Quit the current game and start a new one?\n" +
						"Clicking \"No\" will still save the changes for the next game.",
					C_TITLE, JOptionPane.YES_NO_CANCEL_OPTION
				);
				
				switch (Confirmation) {
					case JOptionPane.YES_OPTION:
						return new Update(true, true, true);
					
					case JOptionPane.NO_OPTION:
						return new Update(true, false, true);
					
					default: // CANCEL_OPTION
						return new Update(false, false, true);
				}
			}
			else if (!Game.isActive()) {
				if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(rel, "Start a new game?", C_TITLE, JOptionPane.YES_NO_OPTION)) {
					return new Update(true, true, true);
				}
			}
			return new Update(true, false, true);
		}
		
		private boolean validate, restart, close;
		
		private Update(boolean validate, boolean restart, boolean close) {
			this.validate = validate;
			this.restart = restart;
			this.close = close;
		}
	}
}
