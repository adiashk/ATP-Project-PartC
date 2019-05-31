import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("/View/MyView.fxml"));
        primaryStage.setTitle("The Chicken Invaders Maze!!!");
        Scene scene = new Scene(root,800,700);
        scene.getStylesheets().add("View/MyViewStyle.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Rise Servers
    }


    public static void main(String[] args) {
        launch(args);
    }
}
