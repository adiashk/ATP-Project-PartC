package View;

import algorithms.mazeGenerators.Maze;

public interface IView {
    void displayMaze(Maze maze);

    void newGame();
    void saveGame();
    void loadGame();
    void propertiesGame();
    void exitGame();
    void helpGame();
    void aboutGame();

}
