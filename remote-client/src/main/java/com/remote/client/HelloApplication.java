package com.remote.client;

import com.remote.client.service.ClientService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private ClientService clientService;

    @Override
    public void start(Stage stage) throws IOException {
        //clientService = new ClientService("localhost", 8080);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 350);
        String css = this.getClass().getResource("design/main.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void stop() throws Exception {
        if (clientService != null) {
            clientService.closeConnection();
        }
        super.stop();
    }
    public static void main(String[] args) {
        launch();
    }
}