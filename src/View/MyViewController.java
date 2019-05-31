package View;

import Server.Configurations;
import algorithms.mazeGenerators.IMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyViewController implements IView{

    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.Node GridPane_newMaze;
    //public javafx.scene.image.Image back;


    @Override
    public void newGame() {
        GridPane_newMaze.setVisible(true);
    }

    public void generateMaze(){

        try {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
        } catch (NumberFormatException e) {
            popWindow("Wrong row or column","Please enter valid numbers.");
        }
        GridPane_newMaze.setVisible(false);
    }


    public void mazePrintWindow(int row, int col ){
        IMazeGenerator mg = new MyMazeGenerator();
        Maze maze = mg.generate(row, col);
/*
        for (int i )
*/
    }

    public void popWindow(String title, String message ){

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
    public void saveGame() {

    }

    @Override
    public void loadGame() {

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
        String str="Tread = ";
        str+=prop.getProperty("Tread");
        str+="\n";
        str+="Solution = ";
        str+=prop.getProperty("sol");
        str+="\n";
        str+="Maze = ";
        str+=prop.getProperty("maze");

        popWindow("maze properties",str);

}

    @Override
    public void exitGame() {
        String strExit="are you sure you want to exit?";
        exitPopWindow("exit window",strExit);
    }
    public void exitPopWindow(String title, String message ){

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
        layout.getChildren().addAll(label, yesButton,noButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    @Override
    public void helpGame() {
        String strHelp="roles of the game:\n" +
                "move the racket on the eggs and break them until you get to the end. ";
        popWindow("Help window",strHelp);
    }

    @Override
    public void aboutGame() {
        String strAbout="this game brought you by Yuval Mor Yosef and Adi Ashkenazi\n" +
                "in the course of advanced topic in programing\n"+
                "We build the maze whit the algorithm of Prim\n" +
                "We build solutions for the maze whit the algorithms of :\n" +
                "Breadth-first search and his expansion, Best-first search and depth-first search\n"+
                "We compress the maze in a decimal method\n" +
                "We use thread pool to to manage multiple client."
                ;
        popWindow("About the game",strAbout);
    }
}
