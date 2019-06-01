package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

public class MyViewController implements Observer, IView {
    @FXML
    private MyViewModel myViewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.Node GridPane_newMaze;


    public void setViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
        bindProperties(myViewModel);
    }

    private void bindProperties(MyViewModel myViewModel) {
        lbl_rowsNum.textProperty().bind(myViewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(myViewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            displayMaze(myViewModel.getMaze());
            btn_generateMaze.setDisable(false);
        }
    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = myViewModel.getCharacterPositionRow();
        int characterPositionColumn = myViewModel.getCharacterPositionColumn();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void generateMaze() {
        boolean isOk=true;
        try {
            int row = Integer.valueOf(txtfld_rowsNum.getText());
            int col = Integer.valueOf(txtfld_columnsNum.getText());
            isOk = true;
        } catch (NumberFormatException e) {
            popWindow("Wrong row or column", "Please enter valid numbers.");
            //btn_generateMaze.setDisable(true);
            isOk=false;
        }
        if(isOk)
            myViewModel.generateMaze(Integer.valueOf(txtfld_rowsNum.getText()), Integer.valueOf(txtfld_columnsNum.getText()));

    }
        public void solveMaze (ActionEvent actionEvent){
            showAlert("Solving maze..");
        }

        private void showAlert (String alertMessage){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(alertMessage);
            alert.show();
        }

        public void KeyPressed (KeyEvent keyEvent){
            myViewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }

        //region String Property for Binding
        public StringProperty characterPositionRow = new SimpleStringProperty();

        public StringProperty characterPositionColumn = new SimpleStringProperty();

        public String getCharacterPositionRow () {
            return characterPositionRow.get();
        }

        public StringProperty characterPositionRowProperty () {
            return characterPositionRow;
        }

        public String getCharacterPositionColumn () {
            return characterPositionColumn.get();
        }

        public StringProperty characterPositionColumnProperty () {
            return characterPositionColumn;
        }

        public void setResizeEvent (Scene scene){
            long width = 0;
            long height = 0;
            scene.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                    System.out.println("Width: " + newSceneWidth);
                }
            });
            scene.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                    System.out.println("Height: " + newSceneHeight);
                }
            });
        }


        //endregion


        @Override
        public void newGame () {
            GridPane_newMaze.setVisible(true);
        }

        public void mazePrintWindow ( int row, int col){
            IMazeGenerator mg = new MyMazeGenerator();
            Maze maze = mg.generate(row, col);
        }
        public void popWindow (String title, String message ){

            Stage window = new Stage();

            //Block events to other windows
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle(title);
            window.setMinWidth(550);
            window.setMinHeight(300);

            Label label = new Label();
            label.setText(message);
            Button closeButton = new Button("Close this window");
            closeButton.setOnAction(e -> window.close());

            VBox layout = new VBox(20);
            layout.getChildren().addAll(label, closeButton);
            layout.setAlignment(Pos.CENTER);

            //Display window and wait for it to be closed before returning
            Scene scene = new Scene(layout);
            window.setScene(scene);
            window.showAndWait();
        }
        @Override
        public void saveGame () {

        }

        @Override
        public void loadGame () {

        }

        @Override
        public void propertiesGame () {
            Properties prop = new Properties();

            InputStream input = Configurations.class.getClassLoader().getResourceAsStream("config.properties");
            // load a properties file
            try {
                prop.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // get value by key
            String str = "Tread = ";
            str += prop.getProperty("Tread");
            str += "\n";
            str += "Solution = ";
            str += prop.getProperty("sol");
            str += "\n";
            str += "Maze = ";
            str += prop.getProperty("maze");

            popWindow("maze properties", str);

        }

        @Override
        public void exitGame () {
            String strExit = "are you sure you want to exit?";
            exitPopWindow("exit window", strExit);

        }
        public void exitPopWindow (String title, String message ){

            Stage window = new Stage();

            //Block events to other windows
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle(title);
            window.setMinWidth(300);
            window.setMinHeight(300);

            Label label = new Label();
            label.setText(message);
            Button yesButton = new Button("Yes, of course!\n" +
                    "Close the game");
            Button noButton = new Button("No,I regretted it\n" +
                    "Keep playing");
            noButton.setOnAction(e -> window.close());
            yesButton.setOnAction(e -> System.exit(0));//Platform.exit();
            VBox layout = new VBox(20);//Platform.exit();
            layout.getChildren().addAll(label, yesButton, noButton);
            layout.setAlignment(Pos.CENTER);

            //Display window and wait for it to be closed before returning
            Scene scene = new Scene(layout);
            window.setScene(scene);
            window.showAndWait();
        }

        @Override
        public void helpGame () {
            String strHelp = "The roles of the game:\n" +
                    "move the rocket on the eggs and break them until\n" +
                    "you get to the end of the board.\n" +
                    "be careful not to collide the chickens. ";
            popWindow("Help window", strHelp);
        }

        @Override
        public void aboutGame () {
            String strAbout = "this game brought you by Yuval Mor Yosef and Adi Ashkenazi\n" +
                    "in the course of advanced topic in programing\n" +
                    "We build the maze with the algorithm of Prim\n" +
                    "We build solutions for the maze with the algorithms of :\n" +
                    "Breadth-first search and his expansion, Best-first search and depth-first search\n" +
                    "We compress the maze in a decimal method\n" +
                    "We use thread pool to to manage multiple client.";
            popWindow("About the game", strAbout);
        }

    }

