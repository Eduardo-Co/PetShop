package com.mycompany.petshop_db.dao;

import com.mycompany.petshop_db.models.pet;
import com.mycompany.petshop_db.models.pet.Sexo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DatabasePet {
    
    public static List<pet> getPets() {
        List<pet> pets = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM PET");
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nome = resultSet.getString("NOME");
                Sexo sexo = Sexo.valueOf(resultSet.getString("SEXO"));
                String clienteId = resultSet.getString("CLIENTE_ID");
                int racaId = resultSet.getInt("RACA_ID");
                
                pet pet = new pet(id, nome, sexo, clienteId, racaId);
                pets.add(pet);
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao obter Pet", e);        
        }
        return pets;
    }
    
    public static void salvarPet(List<pet> pets) {
        String verifyFKCliente = "SELECT * FROM CLIENTE WHERE CPF = ?";
        String verifyFKRaca = "SELECT * FROM RACA WHERE ID = ?";
        String sql = "INSERT INTO PET (NOME, SEXO, CLIENTE_ID, RACA_ID) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement verifyStatementCliente = connection.prepareStatement(verifyFKCliente);
             PreparedStatement verifyStatementRaca = connection.prepareStatement(verifyFKRaca);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (pet pet : pets) {
                verifyStatementCliente.setString(1, pet.getClienteId());
                verifyStatementRaca.setInt(1, pet.getRacaId());

                ResultSet resultSetCliente = verifyStatementCliente.executeQuery();
                ResultSet resultSetRaca = verifyStatementRaca.executeQuery();

                if (!resultSetCliente.next() || !resultSetRaca.next()) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText(null);
                    alert.setContentText("Raça ou Cliente inválidos");
                    alert.showAndWait();
                    return; 
                }

                statement.setString(1, pet.getNome());
                statement.setString(2, pet.getSexo().name());
                statement.setString(3, pet.getClienteId());
                statement.setInt(4, pet.getRacaId());

                statement.executeUpdate();
                
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro ao salvar Pet", e);        
        }
    }
    
    public static void excluirPet(pet pet) {
         String sql = "DELETE FROM PET WHERE ID = ?";
         try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, pet.getId());
            statement.executeUpdate();
            statement.close();
             
         } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir Pet", e);        
         }
    }

    
    public static List<pet> procurarPet(String nome, String clienteId, String racaId, String sexo) {
        List<pet> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM pet WHERE 1=1 ");

        if (nome != null && !nome.isEmpty()) {
            sql.append("AND NOME LIKE '%").append(nome).append("%' ");
        }
        if (clienteId != null && !clienteId.isEmpty()) {
            sql.append("AND CLIENTE_ID = '").append(clienteId).append("' ");
        }
        if (racaId != null && !racaId.isEmpty()) {
            sql.append("AND RACA_ID = '").append(racaId).append("' ");
        }
        if (sexo != null && !sexo.isEmpty()) {
            sql.append("AND SEXO = '").append(sexo).append("' ");
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString());
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nomePet = resultSet.getString("NOME");
                Sexo sexoPet = Sexo.valueOf(resultSet.getString("SEXO"));
                String clienteIdPet = resultSet.getString("CLIENTE_ID");
                int racaIdPet = resultSet.getInt("RACA_ID");

                pet pet = new pet(id, nomePet, sexoPet, clienteIdPet, racaIdPet);
                resultados.add(pet);
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao procurar Pet", e);        
        }
        return resultados;
    }


    public static void editarPet(pet pet) {
        String sql = "UPDATE PET SET NOME = ?, SEXO = ?, CLIENTE_ID = ?, RACA_ID = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pet.getNome());
            statement.setString(2, pet.getSexo().toString());
            statement.setString(3, pet.getClienteId());
            statement.setInt(4, pet.getRacaId());
            statement.setInt(5, pet.getId());
            statement.executeUpdate();
            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao editar Pet", e);        
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
