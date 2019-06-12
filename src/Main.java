
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
    private double xOffset;
    private double yOffset;
    private double  scaleX;
    private double scaleY;
    private double  transX;
    private double transY;

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
        final ImageView selectedImage = new ImageView();
        Image image = new Image(new File("resources/images/open_manu.jpg").toURI().toURL().toExternalForm());
        selectedImage.setImage(image);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        //--------------
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.initStage(primaryStage);
 /*       scaleX = myViewController.pane.getScaleX();
        scaleY = myViewController.pane.getScaleY();
        transX = myViewController.pane.getTranslateX();
        transY = myViewController.pane.getTranslateY();
*/
   /*     scene.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle( ScrollEvent event) {
                xOffset=event.getDeltaY();
                yOffset=event.getDeltaY();
                System.out.println(xOffset+" , "+yOffset);
                double zoomFactor = 1.05;
                double deltaY = event.getDeltaY();

                if (deltaY < 0){
                    zoomFactor = 0.95;
                }
                myViewController.pane.setScaleX(myViewController.pane.getScaleX() * zoomFactor);
                myViewController.pane.setScaleY(myViewController.pane.getScaleY() * zoomFactor);

                System.out.println("before");
                System.out.println(myViewController.pane.getTranslateX()+" , "+myViewController.pane.getTranslateY());
                System.out.println("after");

                double widthPane = myViewController.pane.getWidth()/2;
                double heightPane = myViewController.pane.getHeight()/2;
                double sizeA = (widthPane*2/model.getMaze().getrowSize());
                double sizeB = (heightPane*2/model.getMaze().getcolSize());
                System.out.println("Size:: "+sizeA+" , "+sizeB);
                System.out.println("heightPane:: "+widthPane*2+" , "+heightPane*2);
                System.out.println("row:: "+myViewController.mazeDisplayer.getCharacterPositionRow()+" , "+myViewController.mazeDisplayer.getCharacterPositionColumn());
                System.out.println("scale:: "+myViewController.pane.getScaleX()+" , "+myViewController.pane.getScaleY());

                    myViewController.pane.setTranslateX(((myViewController.pane.getWidth()/2-(myViewController.mazeDisplayer.getCharacterPositionColumn()
                         *sizeB  ))*myViewController.pane.getScaleX() )- 167);
                    myViewController.pane.setTranslateY(((myViewController.pane.getHeight()/2-(myViewController.mazeDisplayer.getCharacterPositionRow()
                         *sizeA  ))*myViewController.pane.getScaleY() ));

                System.out.println(myViewController.pane.getTranslateX()+" , "+myViewController.pane.getTranslateY());
                event.consume();

            }

        });

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("scale:");
                myViewController.pane.setScaleX(scaleX);
                myViewController.pane.setScaleY(scaleY);

                myViewController.pane.setTranslateX(transX);
                myViewController.pane.setTranslateY(transY);
            }
        });


  *//*      scene.setOnScrollFinished(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                System.out.println("off");

                myViewController.pane.setTranslateX(myViewController.pane.getTranslateX()+xOffset);
                myViewController.pane.setTranslateY(myViewController.pane.getTranslateY()+yOffset);
                event.consume();

            }
        });*//*
*/


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

 /*   private void finishedScroll(MyViewController myViewController){
        myViewController.pane.setTranslateX(myViewController.mazeDisplayer.getX());
        myViewController.pane.setTranslateY(myViewController.mazeDisplayer.getY());
    }*/

    public static void main(String[] args) {
        launch(args);
    }
}
