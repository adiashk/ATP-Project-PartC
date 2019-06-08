package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.Node GridPane_newMaze;
//    public javafx.scene.Node pane;
    public javafx.scene.layout.Pane pane;
    public boolean isPushedSolve = false;
    public boolean isPushedNewMaze = false;
    public Stage stage;
 //   public ImageView imageView;

    public void initStage(Stage s) {
        stage = s;
    //        File file = new File("resources/images/open_manu.jpg");
//            Image image = new Image("resources/images/open_manu.jpg");
    //        imageView=new ImageView()
    }

    public void setViewModel(MyViewModel myViewModel) {

        //System.out.println("wwwwwwwwwww");
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

        mazeDisplayer.setIsSolve(isPushedSolve);
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        System.out.println("pos: "+characterPositionRow+", "+characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
           if(characterPositionRow == maze.getGoalPosition().getRowIndex()&&
            characterPositionColumn == maze.getGoalPosition().getColumnIndex()){
                popWindow("final", "awawawaw!!!!!");
            }

    }

    public void generateMaze() {

        isPushedSolve = false;
        boolean isOk = true;
        try {
            int row = Integer.valueOf(txtfld_rowsNum.getText());
            int col = Integer.valueOf(txtfld_columnsNum.getText());
            if (row<3||col<3)
                throw new NumberFormatException();
            isOk = true;
        } catch (NumberFormatException e) {
            popWindow("Wrong row or column", "Please enter valid numbers\n" +
                                                        "positive numbers, bigger then 3.");
            isOk = false;
        }
        if (isOk) {

            myViewModel.generateMaze(Integer.valueOf(txtfld_rowsNum.getText()), Integer.valueOf(txtfld_columnsNum.getText()));
            btn_solveMaze.setDisable(false);
            isPushedNewMaze = true;
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        mazeDisplayer.setSolutionPath(myViewModel.solveMaze());
        isPushedSolve = !isPushedSolve;
        mazeDisplayer.setIsSolve(isPushedSolve);
        //btn_solveMaze.setDisable(true);
        mazeDisplayer.redraw();
        //btn_solveMaze.setDisable(true);

    }

    public void KeyPressed(KeyEvent keyEvent) {
        myViewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }
    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }

//    public void mouseDrag(MouseEvent mouseEvent) {
//        myViewModel.moveCharacter(mouseEvent.);
//        mouseEvent.consume();
//    }


    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                mazeDisplayer.widthProperty().bind(pane.widthProperty());

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                mazeDisplayer.heightProperty().bind(pane.heightProperty());
            }
        });
    }

    @Override
    public void newGame() {
        GridPane_newMaze.setVisible(true);
    }


    @Override
    public void saveGame() {
        if (isPushedNewMaze == true) {
            FileChooser fileChooser = new FileChooser();
            //Set extension filter
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Maze Files", "*.maze");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            //Show save file dialog
            File file = fileChooser.showSaveDialog(stage);
            Position p = new Position(myViewModel.getCharacterPositionRow(),myViewModel.getCharacterPositionColumn());
            SaveMazeFile(myViewModel.getMaze(),p, file);

        } else
            popWindow("Attempt Saving failed", " attempt failed to save the maze\n" +
                    "you need to create maze first");
    }

    private void SaveMazeFile(Maze maze,Position pos, File file) {
        try {
            File newFile = new File(file.getPath());
            ArrayList<Object> arrObjects=new ArrayList<>();
            arrObjects.add(maze);
            arrObjects.add(pos);
            ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(newFile));
            oos.writeObject(arrObjects);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadGame() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files", "*.maze"));
        File loadFile = fileChooser.showOpenDialog(stage);
        if (loadFile != null) {
            try {
                loadMazeFile(loadFile);
                GridPane_newMaze.setVisible(true);
                btn_solveMaze.setDisable(false);

            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            //Desktop.getDesktop().open(loadFile); // open in new window
        } else {
            popWindow("Attempt Loading failed", " attempt failed to load the maze\n" +
                    "you need to create maze first");
        }
    }

    private void loadMazeFile(File file) throws IOException, ClassNotFoundException {
        FileInputStream inputFile = new FileInputStream(file.getPath());
        ObjectInputStream ois=new ObjectInputStream(inputFile);
        ArrayList<Object> arrObjects=new ArrayList<>();
        arrObjects=(ArrayList<Object>)ois.readObject();
        Maze maze =(Maze)(arrObjects.toArray()[0]);
        Position p=(Position) (arrObjects.toArray()[1]);
        this.myViewModel.setMaze(maze);
        this.myViewModel.setPosition(p);
        this.txtfld_rowsNum.setText(maze.getrowSize()+"");
        this.txtfld_columnsNum.setText(maze.getcolSize()+"");
        this.isPushedSolve = false;
        setViewModel(myViewModel);
        displayMaze(maze);


        //mazeDisplayer.setMaze(maze);
    }


    @Override
    public void propertiesGame() {
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
    public void exitGame() {
        String strExit = "are you sure you want to exit?";
        exitPopWindow("exit window", strExit);

    }
    public void popWindow(String title, String message) {

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
        closeButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                window.close();
            }
        });

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
//        scene.getStylesheets().add("PopUpWindow.css");
        scene.getStylesheets().add(getClass().getResource("PopUpWindow.css").toExternalForm());

        window.setScene(scene);
        window.showAndWait();
    }


    public void exitPopWindow(String title, String message) {

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
        noButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                window.close();
            }
        });
        yesButton.setOnAction(e -> System.exit(0));//Platform.exit();
        yesButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                System.exit(0);
            }
        });

        VBox layout = new VBox(20);//Platform.exit();
        layout.getChildren().addAll(label, yesButton, noButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("PopUpWindow.css").toExternalForm());
        window.showAndWait();
    }

    @Override
    public void helpGame() {
        String strHelp = "The roles of the game:\n" +
                "move the rocket on the eggs and break them until\n" +
                "you get to the end of the board.\n" +
                "be careful not to collide the chickens. ";
        popWindow("Help window", strHelp);
    }

    @Override
    public void aboutGame() {
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

