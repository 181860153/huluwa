import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

public class ClientMain extends Application {
    public static Client client;
    public static void main(String[] args) {
        launch(args);
    }
    public static Stage thisstage;


    @Override
    public void start(Stage primaryStage) throws Exception {
        thisstage = primaryStage;
        try{
            client = new Client("localhost",5000);
            client.setup();
        }
        catch (Exception e){
            System.out.println("Cannot connect to server.");
        }
        Pane pane = Map.pane;
        //System.out.println("here");
        Map.init();
        //System.out.println("here");
        primaryStage.setTitle("葫芦娃大战妖精");
        primaryStage.setOnCloseRequest((event) -> System.exit(0));
        Scene scene = new Scene(pane, 505, 505);
        scene.getRoot().requestFocus();
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
