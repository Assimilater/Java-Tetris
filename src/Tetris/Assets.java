package Tetris;


import Tetris.Forms.Options;
import Tetris.Structures.Grid;
import Tetris.Structures.Shape;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Assets {
	public static final String
		classicTheme = "Classic",
		defaultTheme = "TetrisFriends";
	
	// Use a static initializer so we can have a static final HashMap
	public static final HashMap<String, Theme> themes;
	static {
		themes = new HashMap<String, Theme>();
		themes.put(classicTheme, new Theme(false, classicTheme));
		themes.put("TetrisFriends", new Theme(true, "TetrisFriends"));
	}
	
	public static void paint(Shape.Tetromino block, boolean isBig, Grid.GridCell gridCell, final Graphics g) {
		if (themes.containsKey(Options.theme)) {
			themes.get(Options.theme).paint(block, isBig, gridCell, g);
		}
		else {
			themes.get(defaultTheme).paint(block, isBig, gridCell, g);
		}
	}
	
	private static class Theme {
		private HashMap<Shape.Tetromino, ThemePainter> painters;
		
		public Theme(boolean usesImage, String name) {
			painters = new HashMap<Shape.Tetromino, ThemePainter>();
			if (usesImage) {
				for (Shape.Tetromino shape : Shape.Tetromino.values()) {
					painters.put(shape, new ImagePainter(
						new ImageIcon(
							Program.class.getClassLoader().getResource("assets/" + name + "/" + shape.name() + ".png")
						).getImage()
					));
				}
			}
			else if(!name.equals(classicTheme)) {
				// TODO: Insert code to parse JSON formatted file for color scheme
			}
			else {
				painters.put(Shape.Tetromino.Empty, new ColorPainter(Color.BLACK));
				painters.put(Shape.Tetromino.Shadow, new ColorPainter(Color.GRAY));
				painters.put(Shape.Tetromino.Square, new ColorPainter(Color.YELLOW));
				painters.put(Shape.Tetromino.Line, new ColorPainter(Color.CYAN));
				painters.put(Shape.Tetromino.S, new ColorPainter(Color.GREEN));
				painters.put(Shape.Tetromino.Z, new ColorPainter(Color.PINK));
				painters.put(Shape.Tetromino.T, new ColorPainter(Color.MAGENTA));
				painters.put(Shape.Tetromino.L, new ColorPainter(Color.BLUE));
				painters.put(Shape.Tetromino.MirroredL, new ColorPainter(Color.ORANGE));
			}
		}
		public void paint(Shape.Tetromino block, boolean isBig, Grid.GridCell gridCell, final Graphics g) {
			painters.get(block).drawImage(isBig, gridCell, g);
		}
	}
	
	private interface ThemePainter {
		public abstract void drawImage(boolean isBig, JPanel control, final Graphics g);
	}
	
	private static class ColorPainter implements ThemePainter {
		private Color color;
		@Override
		public void drawImage(boolean isBig, JPanel control, final Graphics g) {
			control.setBackground(color);
		}
		
		public ColorPainter(Color c) { color = c; }
	}
	
	private static class ImagePainter implements ThemePainter {
		private Image original, big, small;
		
		@Override
		public void drawImage(boolean isBig, JPanel control, final Graphics g) {
			Image toDraw =  isBig ? big : small;
			if (toDraw != null) {
				g.drawImage(toDraw, 0, 0, null);
			}
		}
		
		public ImagePainter(Image i) {
			original = i;
			big = original.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
			small = original.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
		}
	}
}
