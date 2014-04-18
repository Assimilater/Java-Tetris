/***                                  Tetris                                  ***\
 *                                                                              *
 *                           Student Name:  John Call                           *
 *                           Student A#:    A01283897                           *
 *                                                                              *
\***                               CS2410 ~ USU                               ***/

This is a Java implementation of the classic puzzle game, Tetris.
Use the arrow keys to move blocks. Complete rows to clear them from the board.
Clear a number of rows to advance to the next level. Each level is faster, through.
There are multiple themes you may use, and varying difficulty levels.
May the tetromino be with you.

+---------------------------------------------------------------------------------+
|                                  CITED SOURCES                                  |
+---------------------------------------------------------------------------------+

Images for the "TetrisFriends" theme obtained from a screenshot of tetrisfriends.com

+---------------------------------------------------------------------------------+
|                         PROJECT REQUIREMENTS FULFILMENT                         |
+---------------------------------------------------------------------------------+

1	Require at least as much work as the Memory game project (project 1).
		Check
2	Use keyboard or mouse input (at least one).
		Check
3	Use at least five types of visible, non-container GUI components.
		JLabel
		JButton
		JComboBox
		JCheckBox
		
		JMenuBar with MenuItems
			Kind of containers...
			But consider them along side with how I use JPanels
		JPanels
			They're technically containers...
			But I draw images on them and arrange them into a game grid.
			That's got to count, right?
			I could replace "Difficulty" in the Options dialog with radio buttons...
			But it works so well as as JComboBox there's really no reason to...
			For tetris, there's not really another component that would be practical

+---------------------------------------------------------------------------------+
|                                   HOW TO PLAY!                                  |
+---------------------------------------------------------------------------------+

Keyboard Combinations are as follows

Left        Move Tetris Block Left
Right       Move Tetris Block Right
Up          Rotate Tetris Block (Clockwise)
Down        Slide Tetris Block Down
Space       Hard Drop Tetris Block
Shift       Swap Falling Tetris Block with Hold Item (Can't be used twice in a row)
Esc, P      Pause/Resume Game
Ctrl+N, F2  New Game

You are awarded more points for clearing more rows at a time, as well as more consecutive rows!


+---------------------------------------------------------------------------------+
|                       OTHER INFORMATION ABOUT THIS PROJECT                      |
+---------------------------------------------------------------------------------+

