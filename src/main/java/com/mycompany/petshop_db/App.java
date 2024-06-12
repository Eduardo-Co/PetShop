package com.mycompany.petshop_db;

import com.mycompany.petshop_db.dao.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseInitializer.initializeDatabase(); // Interrompe a execução se ocorrer um erro ao inicializar o banco de dados
        System.out.println("Banco de dados inicializado com sucesso!");
       
        try {
            StageManager.setPrimaryStage(primaryStage);

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/TelaPrincipal.fxml"));
            System.out.println("FXML carregado com sucesso!");

            Scene scene = new Scene(root);
            scene.getStylesheets().add("/Styles/telaprincipal.css");
            primaryStage.setTitle("PetShop");

            // Definindo o tamanho da janela
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200); // Defina a largura mínima da janela
            primaryStage.setMinHeight(646); // Defina a altura mínima da janela
            primaryStage.setMaxWidth(1200); // Defina a largura máxima da janela
            primaryStage.setMaxHeight(646); // Defina a altura máxima da janela
            primaryStage.setResizable(false); // Impede redimensionamento da janela

            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("Janela exibida com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar o FXML ou inicializar a janela: " + e.getMessage());
        }    
    }

    public static void main(String[] args) {
        launch();
    }
}
