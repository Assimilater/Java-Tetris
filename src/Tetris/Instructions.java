package Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Instructions extends JDialog implements ActionListener {
	private static final int WIDTH = 325, HEIGHT = 180;
	private static final String TITLE = "Tetris - How to Play";
	private Container pane;
	
	JButton finished, next, previous;
	
	public Instructions() {
		pane = this.getContentPane();
		pane.setLayout(null);
		
		this.setTitle(TITLE);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(MainFrame.getThis());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);
		
		finished = new JButton("Done");
		finished.setBounds(5, HEIGHT - 68, (WIDTH - 20) / 2, 30);
		finished.setMnemonic(KeyEvent.VK_D);
		finished.setFont(Program.displayFont);
		finished.addActionListener(this);
		pane.add(finished);
		
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == finished)
		this.dispose();
	}
}
