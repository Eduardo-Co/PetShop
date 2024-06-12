package com.mycompany.petshop_db.dao;

import com.mycompany.petshop_db.models.raca;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DatabaseRaca {

    public static List<raca> getRacas() {
        List<raca> racas = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM RACA");
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String descricao = resultSet.getString("DESCRICAO");
                
                raca raca = new raca(id, descricao);
                racas.add(raca);
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao obter Raça", e);        
        }
        
        return racas;
    }

    public static void salvarRaca(List<raca> racas) {
        String sql = "INSERT INTO RACA (DESCRICAO) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (raca raca : racas) {
                statement.setString(1, raca.getDescricao());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro ao salvar Raça", e);        
        }
    }

    public static void excluirRaca(int racaId) {
        String sql = "DELETE FROM RACA WHERE ID = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, racaId);
            statement.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir Raça", e);        
        }
    }

    public static List<raca> procurarRaca(String pesquisar) {
        List<raca> resultados = new ArrayList<>();
        String sql = "SELECT * FROM RACA WHERE DESCRICAO LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "%" + pesquisar + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    String descricao = resultSet.getString("DESCRICAO");

                    raca raca = new raca(id, descricao);
                    resultados.add(raca);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao procurar Raça", e);        
        }
        return resultados;
    }

    public static void editarRaca(raca raca) {
        String sql = "UPDATE RACA SET DESCRICAO = ? WHERE ID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, raca.getDescricao());
            statement.setInt(2, raca.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao editar Raça", e);        
        }
    }
    private static void mostrarAlerta(String cabecalho, SQLException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro SQL");
        alert.setHeaderText(cabecalho);

        String mensagem;
        System.out.print(exception.getSQLState());
        
        switch (exception.getSQLState()) {
            case "23505":
                mensagem = "Já existe um registro com a mesma chave única.";
                break;
            case "23502":
                mensagem = "Um ou mais campos obrigatórios não foram preenchidos.";
                break;
            case "22001":
                mensagem = "O tamanho de uma ou mais strings excede o limite permitido.";
                break;
            case "23000":
                mensagem = "Esta operação viola uma restrição de integridade. Verifique se os dados fornecidos são válidos.";
                break;
            default:
                mensagem = "Ocorreu um erro SQL. Detalhes: " + exception.getMessage();
                break;
        }

        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
