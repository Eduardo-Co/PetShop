package com.mycompany.petshop_db.dao;

import com.mycompany.petshop_db.models.veterinarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

public class DatabaseVeterinarios {
    public static List<veterinarios> getVeterinarios() {
        List<veterinarios> veterinarios = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM VETERINARIOS");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String crmv = resultSet.getString("CRMV");
                String nome = resultSet.getString("NOME");
                String especializacao = resultSet.getString("ESPECIALIZACAO");
                String telefone = resultSet.getString("TELEFONE");

                veterinarios veterinario = new veterinarios(crmv, nome, especializacao, telefone);
                veterinarios.add(veterinario);
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro ao obter veterinário", e);        
        }
        return veterinarios;
    }
    
    public static void salvarVeterinarios(List<veterinarios> veterinarios) {
        String sql = "INSERT INTO VETERINARIOS (CRMV, NOME, ESPECIALIZACAO, TELEFONE) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (veterinarios veterinario : veterinarios) {
                statement.setString(1, veterinario.getCrmv());
                statement.setString(2, veterinario.getNome());
                statement.setString(3, veterinario.getEspecializacao());
                statement.setString(4, veterinario.getTelefone());
                statement.execute();
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao salvar veterinário", e);        
        }
    }
    
    public static void excluirVeterinario(veterinarios veterinario) {
        String sql = "DELETE FROM VETERINARIOS WHERE CRMV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, veterinario.getCrmv());
            statement.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir veterinário", e);        
        }
    }
    
    public static void editarVeterinario(veterinarios veterinario) {
        String sql = "UPDATE VETERINARIOS SET NOME = ?, ESPECIALIZACAO = ?, TELEFONE = ? WHERE CRMV = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, veterinario.getNome());
            statement.setString(2, veterinario.getEspecializacao());
            statement.setString(3, veterinario.getTelefone());
            statement.setString(4, veterinario.getCrmv());
            statement.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao editar veterinário", e);        
        }
    }
    
    public static List<veterinarios> procurarVeterinario(String nomeSearch, String especializacaoSearch, String crmvSearch, String telefoneSearch) {
        List<veterinarios> resultados = new ArrayList<>();
        String sql = "SELECT * FROM VETERINARIOS WHERE NOME LIKE ? AND ESPECIALIZACAO LIKE ? AND CRMV LIKE ? AND TELEFONE LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + nomeSearch + "%");
            statement.setString(2, "%" + especializacaoSearch + "%");
            statement.setString(3, "%" + crmvSearch + "%");
            statement.setString(4, "%" + telefoneSearch + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String crmv = resultSet.getString("CRMV");
                    String nome = resultSet.getString("NOME");
                    String especializacao = resultSet.getString("ESPECIALIZACAO");
                    String telefone = resultSet.getString("TELEFONE");

                    veterinarios veterinario = new veterinarios(crmv, nome, especializacao, telefone);
                    resultados.add(veterinario);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao procurar veterinário", e);        
        }
        return resultados;
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
