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

You are awarded more points for clearing more rows at a time.
You are also awarded more points for clearing more consecutive rows!
A very significant bonus awaits you if you can clear the entire grid!

+---------------------------------------------------------------------------------+
|                                 OUTSTANDING BUG                                 |
+---------------------------------------------------------------------------------+

When playing with an imaged theme, the first couple minutes the program is open the
images will sometimes fail to be drawn appropriately. I have not yet found a solution
to this and this seems to happen sporadically. It will go away after a minute of playing
and you can also force the images to repaint by dragging the window off the screen
just out of your desktops viewing area and then drag it back into visibility.

+---------------------------------------------------------------------------------+
|                       OTHER INFORMATION ABOUT THIS PROJECT                      |
|                              WHY YOU SHOULD PICK ME                             |
+---------------------------------------------------------------------------------+

This project was designed with the ability to grow. It is well modularized.
Adding themes can be done easily, though admittedly you do need to add one line of
code for each new theme due to the fact that it was also designed to be ready to
export to a jar, and I have not yet found a way to loop through folders packaged
as resources in a jar.

Also my code's on GitHub. How cool is that? Instead of unzipping this you could have
cloned it from GitHub with IntelliJ! https://github.com/Assimilater/Java-Tetris/

Oh and, did I mention I compiled it into a jar? You can give it to your friends!
