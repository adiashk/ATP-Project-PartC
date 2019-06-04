package ViewModel;

import Model.MyModel;
import Model.IModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    public  StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public  StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }


    @Override
    public  void update(Observable o, Object arg) {
        if (o==model){
            System.out.println("2");

            characterPositionRowIndex = model.getCharacterPositionRow();
            characterPositionColumnIndex = model.getCharacterPositionColumn();

            characterPositionRow.set(characterPositionRowIndex + "");


            characterPositionColumn.set(characterPositionColumnIndex + "");

            setChanged();
            notifyObservers();
        }

    }

    public void generateMaze(int row, int col){
        model.generateMaze(row, col);

    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public void setMaze(Maze maze){
        this.model.setMaze(maze);

        model.setCharacterPositionRow(maze.getStartPosition().getRowIndex());
        model.setCharacterPositionColumn(maze.getStartPosition().getColumnIndex());
        this.characterPositionColumnIndex =maze.getStartPosition().getColumnIndex();
        this.characterPositionRowIndex=     maze.getStartPosition().getRowIndex();


    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void saveMaze(){
        model.saveGame();
    }

}
