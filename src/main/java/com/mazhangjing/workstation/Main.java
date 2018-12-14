package com.mazhangjing.workstation;

import com.mazhangjing.workstation.Config;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main extends Application {

    private Config config;

    private List<Map<String,Object>> beans;

    private Task<Object> task;

    private List<Label> list = new LinkedList<>();

    private void initConfig() {
        Yaml yaml = new Yaml();
        Config config = new Config();
        try {
            FileInputStream stream = new FileInputStream(System.getProperty("user.dir") + File.separator + "config.yaml");
            config = yaml.loadAs(stream, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(config);
        this.config = config;
        this.beans = Optional.ofNullable(config.getAppList()).orElse(new LinkedList<>()).stream().map(
                map -> {
                    Map.Entry<String, Object> entry = map.entrySet().iterator().next();
                    Map<String, Object> bean = Optional.ofNullable((Map<String, Object>) entry.getValue()).orElse(new HashMap<>());
                    return bean;
                }
        ).collect(Collectors.toList());
    }

    private void initTask() {
        task = new Task<Object>() {
            @Override
            protected Object call() {
                try {
                    TimeUnit.SECONDS.sleep(config.getStartDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                beans.forEach(map -> {
                    try {
                        TimeUnit.SECONDS.sleep(Optional.ofNullable(config.getEachAppDelay()).orElse(2));
                        String name = (String) map.getOrDefault("name","Application");
                        updateValue(name);
                        updateMessage("Running " + name + " now...");
                        Runtime.getRuntime().exec((String) map.get("path"));
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                updateMessage("Done! System will exit in " + Optional.ofNullable(config.getExitSecs()).orElse(3) +" secs");
                try {
                    TimeUnit.SECONDS.sleep(Optional.ofNullable(config.getExitSecs()).orElse(3));
                    updateValue("999");
                    System.exit(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    public Main() {
        initConfig();
        initTask();
    }

    private Parent drawWindow() {
        FlowPane root = new FlowPane(); root.setAlignment(Pos.CENTER);
        VBox box = new VBox(); root.getChildren().add(box); box.setStyle("-fx-spacing: 8;-fx-padding: 10");
        Label info = new Label("Application Status"); info.setFont(Font.font(20)); info.setPadding(new Insets(0,0,20,0));
        box.getChildren().add(info);
        beans.forEach(map -> {
            String name = (String) map.getOrDefault("name","Application");
            String path = (String) map.getOrDefault("path","Not Found Path...");
            Label label = new Label(name);
            list.add(label);
            String shortPath = path.length() > 50 ? path.substring(0,49) + "..." : path;
            Label pathLabel = new Label(shortPath);
            HBox set = new HBox();
            set.setSpacing(20.0);
            set.getChildren().add(label);
            set.getChildren().add(pathLabel);
            box.getChildren().add(set);
        });

        Label message = new Label();
        message.textProperty().bind(task.messageProperty());
        box.getChildren().add(message);

        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(oldValue + "  " + newValue);
            list.forEach(label -> {
                if (Optional.ofNullable(label.getText()).orElse("").equals(newValue)) {
                    label.setTextFill(Color.TOMATO);
                } else {
                    label.setTextFill(Color.BLACK);
                }
            });
        });


        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = drawWindow();
        primaryStage.setTitle("Windows App Starter " + Optional.ofNullable(config.getVersion()).orElse(""));
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(new Scene(root, 500, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
