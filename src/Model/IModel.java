package Model;

import javafx.scene.input.KeyCode;

public interface IModel {
    //void startServers();
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
}
