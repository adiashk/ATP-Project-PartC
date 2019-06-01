import Model.*;
import View.MyViewController;
import ViewModel.MyViewModel;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Model:
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //--------------
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("/View/MyView.fxml"));
        primaryStage.setTitle("The Chicken Invaders Maze!!!");
        //--------------
        Scene scene = new Scene(root,800,700);
        scene.getStylesheets().add("View/MyViewStyle.css");
        primaryStage.setScene(scene);
        //--------------
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.setResizeEvent(scene);
        myViewController.setViewModel(viewModel);
        viewModel.addObserver(myViewController);
        //--------------
        SetStageCloseEvent(primaryStage);
        primaryStage.show();

        //Rise Servers
    }

    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
