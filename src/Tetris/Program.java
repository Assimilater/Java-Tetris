package Tetris;

import Tetris.Forms.MainFrame;

import java.awt.*;
import java.io.InputStream;

public class Program {
	public static Font displayFont = new Font("Serif", Font.PLAIN, 20);

	public static InputStream getImage(String name) {
		return Program.class.getClassLoader().getResourceAsStream("assets/" + name);
	}
	
	public static void main(String[] args) {
		new MainFrame();
		new Game();
	}
}
