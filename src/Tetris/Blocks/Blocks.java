package Tetris.Blocks;

import java.util.Random;

public class Blocks {
	private static Random random;
	public static Block getRandom() {
		switch(random.nextInt(7)) {
			case 0:
				return new Square();
			case 1:
				
			case 2:
				
			case 3:
				
			case 4:
				
			case 5:
				
			case 6:
				
			default:
				return null;
		}
	}
}
