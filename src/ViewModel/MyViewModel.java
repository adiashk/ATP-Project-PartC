package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;


    public  StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public  StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding
//    public IntegerProperty countMoves = new SimpleIntegerProperty(0);
    private int countMoves;
    public StringProperty moves = new SimpleStringProperty("0");


    public MyViewModel(IModel model){
        this.model = model;
    }


    @Override
    public  void update(Observable o, Object arg) {
        if (o==model){
//            System.out.println("posView: "+model.getCharacterPositionRow()+", "+model.getCharacterPositionColumn());

            characterPositionRow.set(model.getCharacterPositionRow()+ "");

            characterPositionColumn.set(model.getCharacterPositionColumn() + "");
            //notify my observer (MyViewController)that i change
            setChanged();
            notifyObservers();
        }

    }

    public int getRotation(){
        return model.getDirection();
    }

    public void generateMaze(int row, int col){
        model.generateMaze(row, col);
        model.setCharacterPositionRow(model.getMaze().getStartPosition().getRowIndex());
        model.setCharacterPositionColumn(model.getMaze().getStartPosition().getColumnIndex());
        countMoves=0;
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
        if(model.isCorrectMove()) {
            countMoves++;
            //model.setCorrectMove(false);
        }
        moves.setValue(countMoves+"");
//        int countM=countMoves.getValue();
//        countMoves.setValue(countM+1);
    }

    public boolean isCorrectMove() {
        return model.isCorrectMove();
    }
    public Maze getMaze() {
        return model.getMaze();
    }

    public void setMaze(Maze maze){//new
        this.model.setMaze(maze);
    }

    public void setPosition(Position pos){
        model.setCharacterPositionRow(pos.getRowIndex());
        model.setCharacterPositionColumn(pos.getColumnIndex());
    }

    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        return model.getCharacterPositionColumn();
    }

    public void saveMaze(){
        model.saveGame();
    }

    public ArrayList<AState> solveMaze(){return model.solveMaze();}
}
