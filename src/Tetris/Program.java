package Tetris;

import Tetris.Forms.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Program {
	public static Font displayFont = new Font("Serif", Font.PLAIN, 20);
	public static Font displayFont(int style, int dSize) { return new Font("Serif", style, 20 + dSize); }
	public static Color
		background = Color.decode("#353B3D"),
		foreground = Color.decode("#B8BFC2");
	
	public static Image getImage(String name) {
		return new ImageIcon(
			Program.class.getClassLoader().getResource("assets/" + name + ".png")
		).getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
	}
	
	public static void main(String[] args) {
		new MainFrame();
		new Game();
	}
}
