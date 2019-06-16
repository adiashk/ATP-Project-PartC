
import Model.*;
import View.IView;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //MyViewModel->MyModel
        MyModel model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("The Chicken Invaders Maze!");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("View/MyView.fxml").openStream());
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(getClass().getResource("View/MyViewStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        //--------------
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.initStage(primaryStage);
        myViewController.setZoom(scene);
        myViewController.setResizeEvent(scene);
        myViewController.setViewModel(viewModel);
        viewModel.addObserver(myViewController);
        //--------------
        SetStageCloseEvent(primaryStage, myViewController);
        primaryStage.show();
    }

    private void SetStageCloseEvent(Stage primaryStage, IView view) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                view.exitGame();
                windowEvent.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
