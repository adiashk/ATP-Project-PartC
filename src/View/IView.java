package View;

import algorithms.mazeGenerators.Maze;
import javafx.scene.input.ScrollEvent;

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
