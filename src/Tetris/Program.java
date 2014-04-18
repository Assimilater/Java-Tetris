package Tetris;

import Tetris.Forms.MainFrame;

import java.awt.*;

public class Program {
	public static Font displayFont(int style, int dSize) { return new Font("Serif", style, 20 + dSize); }
	public static Font displayFont = new Font("Serif", Font.PLAIN, 20);
	public static Color
		background = Color.decode("#353B3D"),
		foreground = Color.decode("#B8BFC2");
	
	public static void main(String[] args) {
		new MainFrame();
		new Game();
	}
}
