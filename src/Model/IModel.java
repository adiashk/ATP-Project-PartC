package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;

public interface IModel {
    void generateMaze(int row, int col);
    ArrayList<AState> solveMaze();
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    void setMaze(Maze maze);
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void saveGame();
    void setCharacterPositionColumn(int i);
    void setCharacterPositionRow(int i);
    int getDirection();
    boolean isCorrectMove();
    void setCorrectMove(boolean b);
}

