package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class MyViewController implements Observer, IView {
    @FXML
    private MyViewModel myViewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Label lbl_Moves;
    public javafx.scene.control.Label lbl_Lives;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.Node GridPane_newMaze;
    //    public javafx.scene.Node pane;
    public javafx.scene.layout.Pane pane;
    public javafx.scene.layout.Pane BorderPane;
    public boolean isPushedSolve = false;
    public boolean isPushedNewMaze = false;
    public Stage stage;
    public MediaPlayer mediaPlayer;
    public boolean isMute=false;
    private double scaleX;
    private double scaleY;
    private double transX;
    private double transY;
    private boolean isZoom = false;
    private int countLives=3;
    public StringProperty lives = new SimpleStringProperty("3");

    public void initStage(Stage s) {
        stage = s;
        String open = "Welcome to The Chicken Invaders Maze!";
        playSound("resources/sounds/startSong.m4a");
        openWindow("The Chicken Invaders Maze!", open);
        scaleX= pane.getScaleX();
        scaleY = pane.getScaleY();
        transX = pane.getTranslateX();
        transY = pane.getTranslateY();
    }


    public void setViewModel(MyViewModel myViewModel) {

        this.myViewModel = myViewModel;
        bindProperties(myViewModel);
    }

    private void bindProperties(MyViewModel myViewModel) {
        lbl_rowsNum.textProperty().bind(myViewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(myViewModel.characterPositionColumn);
        lbl_Moves.textProperty().bind(myViewModel.moves);
        lbl_Lives.textProperty().bind(lives);
    }

    /*        public long measureAlgorithmTimeMillis(){
                long timeBefore = System.currentTimeMillis();
        //        generate(row, col);
                long timeAfter = System.currentTimeMillis();
                return (timeAfter-timeBefore);
            }*/
    @Override
    public void update(Observable o, Object arg) {
        if (o == myViewModel) {
            displayMaze(myViewModel.getMaze());
            btn_generateMaze.setDisable(false);
        }

    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setSolutionPath(myViewModel.solveMaze());

        mazeDisplayer.setMaze(maze);
        int characterPositionRow = myViewModel.getCharacterPositionRow();
        int characterPositionColumn = myViewModel.getCharacterPositionColumn();

        mazeDisplayer.setIsSolve(isPushedSolve);
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn, myViewModel.getRotation());
        //System.out.println("pos: " + characterPositionRow + ", " + characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        if(isZoom){doZoom();}

        //wining!!!
        if (characterPositionRow == maze.getGoalPosition().getRowIndex() &&
                characterPositionColumn == maze.getGoalPosition().getColumnIndex()) {
            finishZoom();
            String message = "Congratulations!!!\n" + "You solved the maze";
            String buttonOut = "Do you want to finish the game and exit?";
            String buttonStay = "Do you want to keep play more?\n" +
                    "press here to start a new maze";
            playSound("resources/sounds/winSong.m4a");
            exitPopWindow("final", message, buttonOut, buttonStay);

        }

    }

    public void generateMaze() {
        countLives=3;
        lives.setValue(countLives+"");
        mazeDisplayer.isDrawRocks=false;
        isPushedSolve = false;
        boolean isOk = true;
        try {
            int row = Integer.valueOf(txtfld_rowsNum.getText());
            int col = Integer.valueOf(txtfld_columnsNum.getText());
            if (row < 3 || col < 3)
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
            myViewModel.moves.setValue("0");
            pane.setVisible(true);
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        //mazeDisplayer.setSolutionPath(myViewModel.solveMaze());
        isPushedSolve = !isPushedSolve;
        mazeDisplayer.setIsSolve(isPushedSolve);
        //btn_solveMaze.setDisable(true);
        mazeDisplayer.redraw();
        //btn_solveMaze.setDisable(true);

    }

    public void KeyPressed(KeyEvent keyEvent) {
        myViewModel.moveCharacter(keyEvent.getCode());
        if (myViewModel.isCorrectMove() == false)
            playSound("resources/sounds/packaSound.m4a");

        if (myViewModel.getCharacterPositionRow()==mazeDisplayer.rockPos1.getRowIndex()&
                myViewModel.getCharacterPositionColumn()==mazeDisplayer.rockPos1.getColumnIndex()){
            playSound("resources/sounds/rockHit.m4a");
            countLives--;
        }
        if (myViewModel.getCharacterPositionRow()==mazeDisplayer.rockPos2.getRowIndex()&
                myViewModel.getCharacterPositionColumn()==mazeDisplayer.rockPos2.getColumnIndex()){
            playSound("resources/sounds/rockHit.m4a");
            countLives--;
        }if (myViewModel.getCharacterPositionRow()==mazeDisplayer.rockPos3.getRowIndex()&
                myViewModel.getCharacterPositionColumn()==mazeDisplayer.rockPos3.getColumnIndex()){
            playSound("resources/sounds/rockHit.m4a");
            countLives--;
        }
        lives.setValue(countLives+"");
        if (countLives==0){
            String exit="you lose the game";
            String out="exit";
            String stay="new game";
            exitPopWindow("Game Over",exit,out,stay);
        }


        keyEvent.consume();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }
//todo--------
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
    //endregion

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                //System.out.println("Width: " + newSceneWidth);
                mazeDisplayer.widthProperty().bind(pane.widthProperty());

            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                // System.out.println("Height: " + newSceneHeight);
                mazeDisplayer.heightProperty().bind(pane.heightProperty());
            }
        });

    }



    //region save&load
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
            Position p = new Position(myViewModel.getCharacterPositionRow(), myViewModel.getCharacterPositionColumn());
            SaveMazeFile(myViewModel.getMaze(), p, file);

        } else
            popWindow("Attempt Saving failed", " attempt failed to save the maze\n" +
                    "you need to create maze first");
    }

    private void SaveMazeFile(Maze maze, Position pos, File file) {
        try {
            File newFile = new File(file.getPath());
            ArrayList<Object> arrObjects = new ArrayList<>();
            arrObjects.add(maze);
            arrObjects.add(pos);
            //arrObjects.add(countLives);//
            //System.out.println("lives before: "+ countLives);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(newFile));
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

            } catch (IOException | ClassNotFoundException e) {
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
        ObjectInputStream ois = new ObjectInputStream(inputFile);
        ArrayList<Object> arrObjects = new ArrayList<>();
        arrObjects = (ArrayList<Object>) ois.readObject();
        Maze maze = (Maze) (arrObjects.toArray()[0]);
        Position p = (Position) (arrObjects.toArray()[1]);
        //int loadLives = (int)(arrObjects.toArray()[2]);//
        //countLives =loadLives;
        //System.out.println("lives after: "+ loadLives);
       // this.lives.set(lives);

        this.myViewModel.setMaze(maze);
        this.myViewModel.setPosition(p);
        //this.countLives = loadLives;//
        this.txtfld_rowsNum.setText(maze.getrowSize() + "");
        this.txtfld_columnsNum.setText(maze.getcolSize() + "");
        this.isPushedSolve = false;
        setViewModel(myViewModel);
        displayMaze(maze);


        //mazeDisplayer.setMaze(maze);
    }
    //endregion



    //region pop windows
    public void popWindow(String title, String message) {
        Stage window = new Stage();
        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(700);
        window.setMinHeight(500);

        Label label = new Label();
//        label.setId("about");
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


    public void exitPopWindow(String title, String message, String buttonOut, String buttonStay) {

        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(700);
        window.setMinHeight(500);

        Label label = new Label();
        label.setText(message);
        Button yesButton = new Button(buttonOut);
        Button noButton = new Button(buttonStay);
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

    public void openWindow(String title, String message) {

        Stage window = new Stage();
        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMaximized(true);
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Start the game!!");
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
        scene.getStylesheets().add(getClass().getResource("openGame.css").toExternalForm());

        window.setScene(scene);
        window.showAndWait();

    }
    //endregion


    //region menu bar
    @Override
    public void newGame() {
//        GridPane_newMaze.setVisible(true);
//        pane.setVisible(false);
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
        String str = "The Properties of our game:\n" +
                "Treads amount = ";
        str += prop.getProperty("Tread");
        str += "\n";
        str += "The Solve Algorithm = ";
        str += prop.getProperty("sol");
        str += "\n";
        str += "The Maze method of generating = ";
        str += prop.getProperty("maze");
        playSound("resources/sounds/popSong.m4a");
        popWindow("maze properties", str);

    }

    @Override
    public void exitGame() {
        playSound("resources/sounds/ChickenBite.m4a");
        String strExit = "are you sure you want to exit?";
        String buttonOut = "Yes, of course!\n" +
                "Close the game";
        String buttonStay = "No,I regretted it\n" +
                "Keep playing";
        exitPopWindow("exit window", strExit, buttonOut, buttonStay);

    }
    @Override
    public void helpGame() {
        playSound("resources/sounds/popSong.m4a");
        String strHelp = "The rules of the game:\n" +
                "Move the rocket until\n" +
                "you get to the end of the board.\n" +
                "Be careful not to hit the Rocks. \n" +
                "You have 3 lives\n" +
                "each time you hit a rock you lose 1\n" +
                "the game is over when you have 0 lives.";
        popWindow("Help window", strHelp);
    }

    @Override
    public void aboutGame() {
        playSound("resources/sounds/popSong.m4a");
        String strAbout = "This game is brought to you by:\n" +
                "Yuval Mor Yosef and Adi Ashkenazi\n" +
                "In the course of advanced topic in programing.\n" +
                "We create mazes using Prim's algorithm.\n" +
                "We solve the mazes using the algorithms:\n" +
                "* Breadth-first search and its expansion - Best-first search. \n" +
                "* Depth-first search.\n" +
                "We compress mazes using decimal method.\n" +
                "We use thread pool in order to manage multiple clients.";
        popWindow("About the game", strAbout);
    }
    //endregion

    //region media player
    public void playSound(String musicFile) {
        Media sound = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        if (!isMute)
            mediaPlayer.play();
    }

    public void muteSound() {
        if (isMute) {
            isMute=false;
        }
        else {
            isMute=true;
            mediaPlayer.pause();

        }
    }
    private StackPane root;

    /*public void playVideo(String video){
        Media media = null;
        try{
            File videoFile = new File(video);
//            String url = videoFile.toURI().toURL().toString();
            String url = videoFile.toURI().toURL().toExternalForm();
            System.out.println("URL: "+url);
            media = new Media(url);
        }catch(Exception e){
            System.err.println(e.toString());
        }
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        MediaView mediaView = new MediaView(mediaPlayer);
        root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color : white;");
        root.getChildren().add(mediaView);

//        StackPane root = new StackPane();
//
//        mediaPlayer = new MediaPlayer( new Media(getClass().getResource(video).toExternalForm()));
//        MediaView mediaView = new MediaView(mediaPlayer);
//
//        root.getChildren().add(mediaView);
//
//        Scene scene = new Scene(root, 1024, 768);
//
//        stage.setScene(scene);
//        stage.show();
//
//        mediaPlayer.play();


    }*/
    //endregion



    //region zoom
    public void setZoom(Scene scene) {
        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.isControlDown()) {
                    //  finishZoom();
                    isZoom = true;
                    double xOffset = event.getDeltaY();
                    double yOffset = event.getDeltaY();
                    //System.out.println(xOffset + " , " + yOffset);
                    double zoomFactor = 1.1;
                    double deltaY = event.getDeltaY();

                    if (deltaY < 0) {
                        zoomFactor = 0.9;
                    }
                    pane.setScaleX(pane.getScaleX() * zoomFactor);
                    pane.setScaleY(pane.getScaleY() * zoomFactor);

//                pane.setTranslateX(((pane.getWidth() / 2 - (mazeDisplayer.getCharacterPositionColumn()
//                        * (pane.getWidth()/myViewModel.getMaze().getrowSize()))) * pane.getScaleX()) - 167);
//                pane.setTranslateY(((pane.getHeight() / 2 - (mazeDisplayer.getCharacterPositionRow()
//                        * (pane.getHeight()/myViewModel.getMaze().getcolSize()))) * pane.getScaleY()));
                    doZoom();
                    event.consume();

                }
            }

        });

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isZoom = false;
                finishZoom();
            }
        });
    }

    public void finishZoom(){
        pane.setScaleX(scaleX);
        pane.setScaleY(scaleY);

        pane.setTranslateX(transX);
        pane.setTranslateY(transY);
    }

    public void doZoom(){
        pane.setTranslateX(((pane.getWidth() / 2 - (mazeDisplayer.getCharacterPositionColumn()
                * (pane.getHeight()/myViewModel.getMaze().getrowSize()))) * pane.getScaleX()) - 167);
        pane.setTranslateY(((pane.getHeight() / 2 - (mazeDisplayer.getCharacterPositionRow()
                * (pane.getWidth()/myViewModel.getMaze().getcolSize()))) * pane.getScaleY()));

    }
    //endregion
}

