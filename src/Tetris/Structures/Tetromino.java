package Tetris.Structures;

import java.awt.*;

public class Tetromino {
	// TODO: Replace colors with nicer images :)
	public static final Color
		SHADOW = Color.GRAY,
		EMPTY = Color.BLACK;
	
	public static final int NUM_SHAPES = 7;
	
	public enum tetroNames { ZBlock, SBlock, LineBlock, TBlock, SquareBlock, LBlock, MirroredLBlock }
	
	// Primitive data about each shape by order of tetroNames
	private final Point[][] tetroCoords = new Point[][]{
		{new Point( 0, -1), new Point(0,  0), new Point(-1, 0), new Point(-1, 1)},
		{new Point( 0, -1), new Point(0,  0), new Point( 1, 0), new Point( 1, 1)},
		{new Point( 0, -1), new Point(0,  0), new Point( 0, 1), new Point( 0, 2)},
		{new Point(-1,  0), new Point(0,  0), new Point( 1, 0), new Point( 0, 1)},
		{new Point( 0,  0), new Point(1,  0), new Point( 0, 1), new Point( 1, 1)},
		{new Point(-1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)},
		{new Point( 1, -1), new Point(0, -1), new Point( 0, 0), new Point( 0, 1)}
	};
	private final Color[] tetroColors = new Color[] {
		Color.PINK,
		Color.GREEN,
		Color.CYAN,
		Color.MAGENTA,
		Color.YELLOW,
		Color.BLUE,
		Color.ORANGE
	}; // TODO: Replace colors with nicer images :)
	
	// Static list of all shape instances
	private static Tetromino[] shapes = new Tetromino[] {
		new Tetromino(tetroNames.ZBlock),
		new Tetromino(tetroNames.SBlock),
		new Tetromino(tetroNames.LineBlock),
		new Tetromino(tetroNames.TBlock),
		new Tetromino(tetroNames.SquareBlock),
		new Tetromino(tetroNames.LBlock),
		new Tetromino(tetroNames.MirroredLBlock),
	};
	
	
	
	// Even the constructor is private because this class maintains a static list of all shape instances
	private tetroNames name;
	private Color color;
	private Configuration configuration;
	private Tetromino(tetroNames index) {
		name = index;
		color = tetroColors[index.ordinal()];
		
		Point[] coords = tetroCoords[index.ordinal()];
		
		// Create new Configuration instances based on cached coordinates
		configuration = new Configuration(coords);
	}
	
	// We need accessors to be public, however
	public tetroNames getName() { return name; }
	public Color getColor() { return color; }
	public Configuration getConfiguration() {
		return new Configuration(configuration);
	}
	
	// This is where external classes will get a particular shape
	public static Tetromino getTetromino(int i) {
		if (i > NUM_SHAPES || i < 0) {
			return null;
		}
		return shapes[i];
	}
}
