package Model;

import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyCode;

public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
}
