package com.wojciech;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main extends Application {

    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("doitUI.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setTasksMap(readTaskMap());


        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Do It!!!");
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    private HashMap<Integer, Task> readTaskMap() {
        HashMap<Integer, Task> taskHashMap = new HashMap<>();
        try(InputStream is = Files.newInputStream(Paths.get("tasks.xml"));
             XMLDecoder decoder = new XMLDecoder(is)) {
            taskHashMap = (HashMap<Integer, Task>) decoder.readObject();
        }catch (IOException e){
            System.out.println(e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        return taskHashMap;
    }

    @Override
    public void stop() throws Exception {

        try (OutputStream out = Files.newOutputStream(Paths.get("tasks.xml"));
             XMLEncoder encoder = new XMLEncoder(out)) {
            encoder.writeObject(controller.getTaskMap());
        }catch (IOException e){
            System.out.println(e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
	// write your code here
    }
}
