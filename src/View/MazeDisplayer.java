package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Aviadjo on 3/9/2017.
 */
public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;

    public void setMaze(Maze maze) {
        this.maze = maze;
        redraw(false);
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redraw(false);
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void redraw(boolean isSolve) {
        if (maze != null) {
            int row = maze.getrowSize();
            int col = maze.getcolSize();
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / row;
            double cellWidth = canvasWidth / col;

            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (maze.getValue(i, j) == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            // gc.drawImage(wallImage, i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                        }
                    }
                }

                if(isSolve)
                    redrawSolve();

                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                //gc.drawImage(characterImage, characterPositionRow * cellHeight, characterPositionColumn * cellWidth, cellHeight, cellWidth);
                // gc.drawImage(characterImage, characterPositionRow * cellWidth, characterPositionColumn * cellHeight, cellWidth, cellHeight);
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }


    public void redrawSolve() {
        if (maze != null) {
            int row = maze.getrowSize();
            int col = maze.getcolSize();
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / row;
            double cellWidth = canvasWidth / col;

            SearchableMaze searchableMaze = new SearchableMaze(maze);

            ISearchingAlgorithm searcher = new DepthFirstSearch();
            Solution solution = searcher.solve(searchableMaze);
            ArrayList<AState> solutionPath = solution.getSolutionPath();


            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameSolve.get()));

                GraphicsContext gc = getGraphicsContext2D();
                //Draw Maze
                Object[] o = solutionPath.toArray();
                for (int i = 0; i < o.length; i++) {
                    gc.drawImage(wallImage, ((MazeState) o[i]).getPosition().getColumnIndex() * cellWidth, ((MazeState) o[i]).getPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

                }

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }


    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameSolve = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameSolve() {
        return ImageFileNameSolve.get();
    }

    public StringProperty imageFileNameSolveProperty() {
        return ImageFileNameSolve;
    }

    public void setImageFileNameSolve(String imageFileNameSolve) {
        this.ImageFileNameSolve.set(imageFileNameSolve);
    }
    //endregion

}
