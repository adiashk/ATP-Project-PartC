package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by Aviadjo on 3/9/2017.
 */
public class MazeDisplayer extends Canvas {

    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;
    private ArrayList<AState> solutionPath;
    private boolean isSolve;
    private int rotation;
    private double x;
    private double y;
    public Position rockPos1;
    public Position rockPos2;
    public Position rockPos3;
    private Position[]arrayRock = new Position[3];
    public boolean isDrawRocks=false;

    public MazeDisplayer() {
        widthProperty().addListener(e->redraw());
        heightProperty().addListener(e->redraw());
    }

    public void setMaze(Maze maze) {
        this.maze = maze;

        if (!isDrawRocks) {
            rockPos1 = getRandomRockPos();
            rockPos3 = getRandomRockPos();
            rockPos2 = getRandomRockPos();
            arrayRock[0] = rockPos1;
            arrayRock[1] = rockPos2;
            arrayRock[2] =rockPos3;
            isDrawRocks=true;
        }
    }

    public void setSolutionPath(ArrayList<AState> solutionPath) {
        this.solutionPath = solutionPath;
    }

    public void setCharacterPosition(int row, int column, int rotation) {
        characterPositionRow = row;
        characterPositionColumn = column;
        this.rotation = rotation;
        redraw();
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public boolean getIsSolve() {
        return isSolve;
    }

    public void setIsSolve(boolean issolve) {
        this.isSolve = issolve;
    }


    public void redraw() {
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
                //Image eggBeforeImage = new Image(new FileInputStream(ImageFileNameEgg.get()));
                //Image eggAfterImage = new Image(new FileInputStream(ImageFileNameEggAfter.get()));
                Image startImage = new Image(new FileInputStream(ImageFileNameStart.get()));
                Image endImage = new Image(new FileInputStream(ImageFileNameEnd.get()));
                Image rockImage = new Image(new FileInputStream(ImageFileNameRock.get()));

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
                        /*else if (maze.getValue(i, j) == 0){
                            gc.drawImage(eggBeforeImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                        }else if (maze.getValue(i, j) == -1){
                            gc.drawImage(eggAfterImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);

                        }*/
                    }
                }
                if(isSolve)
                    redrawSolve();
                //start:
                gc.drawImage(startImage, maze.getStartPosition().getColumnIndex() * cellWidth,
                        maze.getStartPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

                //end:
                gc.drawImage(endImage, maze.getGoalPosition().getColumnIndex() * cellWidth,
                        maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

                //Draw rocks
                //Position rockPos1=getRandomRockPos();
                gc.drawImage(rockImage, rockPos1.getColumnIndex() * cellWidth,
                        rockPos1.getRowIndex() * cellHeight, cellWidth, cellHeight);

                //Position rockPos2=getRandomRockPos();
                gc.drawImage(rockImage, rockPos2.getColumnIndex() * cellWidth,
                        rockPos2.getRowIndex() * cellHeight, cellWidth, cellHeight);

                //Position rockPos3=getRandomRockPos();
                gc.drawImage(rockImage, rockPos3.getColumnIndex() * cellWidth,
                        rockPos3.getRowIndex() * cellHeight, cellWidth, cellHeight);

                //Draw Character
//                 gc.drawImage(characterImage, characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                //gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
                //  gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
                //Draw Character
                ImageView iv = new ImageView(characterImage);
                iv.setRotate(rotation);
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                Image rotatedImageCharacter = iv.snapshot(params, null);
                gc.drawImage(rotatedImageCharacter, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
                x= characterPositionColumn * cellWidth;
                y = characterPositionRow * cellHeight;



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
    private StringProperty ImageFileNameStart = new SimpleStringProperty();
    private StringProperty ImageFileNameEnd = new SimpleStringProperty();

    private StringProperty ImageFileNameEgg = new SimpleStringProperty();
    private StringProperty ImageFileNameEggAfter = new SimpleStringProperty();
    private StringProperty ImageFileNameRock = new SimpleStringProperty();

    public String getImageFileNameRock() {
        return ImageFileNameRock.get();
    }

    public StringProperty imageFileNameRockProperty() {
        return ImageFileNameRock;
    }

    public void setImageFileNameRock(String imageFileNameRock) {
        this.ImageFileNameRock.set(imageFileNameRock);
    }

    public String getImageFileNameEggAfter() {
        return ImageFileNameEggAfter.get();
    }

    public StringProperty imageFileNameEggAfterProperty() {
        return ImageFileNameEggAfter;
    }

    public void setImageFileNameEggAfter(String imageFileNameEggAfter) {
        this.ImageFileNameEggAfter.set(imageFileNameEggAfter);
    }

    public String getImageFileNameEgg() {
        return ImageFileNameEgg.get();
    }

    public StringProperty imageFileNameEggProperty() {
        return ImageFileNameEgg;
    }

    public void setImageFileNameEgg(String imageFileNameEgg) {
        this.ImageFileNameEgg.set(imageFileNameEgg);
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.ImageFileNameStart.set(imageFileNameStart);
    }

    public void setImageFileNameEnd(String imageFileNameEnd) {
        this.ImageFileNameEnd.set(imageFileNameEnd);
    }

    public String getImageFileNameStart() {
        return ImageFileNameStart.get();
    }

    public StringProperty imageFileNameStartProperty() {
        return ImageFileNameStart;
    }

    public String getImageFileNameEnd() {
        return ImageFileNameEnd.get();
    }

    public StringProperty imageFileNameEndProperty() {
        return ImageFileNameEnd;
    }

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


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    //rock:
    public boolean validRock(Position pos) {
        //System.out.println(solutionPath);
        Object[] o = solutionPath.toArray();

        for (int i = 0; i < o.length; i++) {
            if (pos.toString().equals(((MazeState) o[i]).getPosition().toString())) {
                return false;
            }
        }
            if (pos.equals(maze.getGoalPosition()) || pos.equals(maze.getStartPosition()))
                return false;
            else if (maze.getValue(pos.getRowIndex(), pos.getColumnIndex()) == 1)
                return false;
            else {
                System.out.println(pos.toString());
                return true;
            }
        }


    public Position getRandomRockPos() {
            int x = getRandomInRange(maze.getrowSize());
            int y = getRandomInRange(maze.getcolSize());
            Position rockPos = new Position(x, y);


        while (validRock(rockPos)!=true){
           x=getRandomInRange(maze.getrowSize());
           y=getRandomInRange(maze.getcolSize());
            rockPos =new Position(x,y);
        }
        return rockPos;
    }

    /**retutn number between 0 to range*/
    private int getRandomInRange(int range) {
        Random rn = new Random();
        int i = rn.nextInt(range);
        return i;
    }

    public Position[] getArrayRock() {
        return arrayRock;
    }

    public void setArrayRock(Position[] arrayRock) {
        this.arrayRock = arrayRock;
        rockPos1 =arrayRock[0];
        rockPos2 = arrayRock[1];
        rockPos3 = arrayRock[2];
    }
}
