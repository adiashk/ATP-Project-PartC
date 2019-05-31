package View;

import Server.Configurations;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class MyViewController implements IView{

    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.Node GridPane_newMaze;


    @Override
    public void newGame() {
        GridPane_newMaze.setVisible(true);
    }

    public void generateMaze(){

        try {
            int rows = Integer.valueOf(txtfld_rowsNum.getText());
            int columns = Integer.valueOf(txtfld_columnsNum.getText());
        } catch (NumberFormatException e) {
            errorWindow("Wrong row/column","Please enter valid numbers.");
        }
    }

    public void mazePrintWindow(int row, int col ){


    }

    public void errorWindow(String title, String message ){
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(400);
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

        errorWindow("maze properties",str);

}

    @Override
    public void exitGame() {

    }

    @Override
    public void helpGame() {

    }

    @Override
    public void aboutGame() {

    }
}
