package Model;

import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyCode;

public interface IModel {
    void generateMaze(int row, int col);
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    void setMaze(Maze maze);
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void saveGame();
    void setCharacterPositionColumn(int i);
    void setCharacterPositionRow(int i);
}

