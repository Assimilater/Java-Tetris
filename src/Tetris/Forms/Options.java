package Tetris.Forms;

import Tetris.Game;
import Tetris.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Options extends JDialog implements ActionListener {
	private static final int WIDTH = 325, HEIGHT = 180;
	private static final String TITLE = "Tetris Options";
	private Container pane;
	
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
			//GameSize newSize;
			// Check if settings have changed and there is still a game active
			if (Game.isActive()) {
				//if (Options.Size != newSize) {
				int Confirmation = JOptionPane.showConfirmDialog(this,
					"Changes will not affect the current game.\n" +
						"Quit the current game and start a new one?\n" +
						"Clicking \"No\" will still save the changes for the next game.",
					"Options Confirmation",
					JOptionPane.YES_NO_CANCEL_OPTION
				);

				if (JOptionPane.NO_OPTION == Confirmation) {
					//Options.Size = newSize;
				}
				else if (JOptionPane.YES_OPTION == Confirmation) {
					//Options.Size = newSize;
					new Game();
				}
				//}
			}
			else {
				//Options.Size = newSize;
				int Confirmation = JOptionPane.showConfirmDialog(this, "Start a new game?", "Options Confirmation", JOptionPane.YES_NO_OPTION);

				if (JOptionPane.YES_OPTION == Confirmation) {
					new Game();
				}
			}
		}
		this.dispose();
	}
}
