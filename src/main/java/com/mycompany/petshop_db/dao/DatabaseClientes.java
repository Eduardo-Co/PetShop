package com.mycompany.petshop_db.dao;

import com.mycompany.petshop_db.models.clientes;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

public class DatabaseClientes {
    public static List<clientes> getUsuariosColuna() {
        List<clientes> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM CLIENTE");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String cpf = resultSet.getString("CPF");
                String nome = resultSet.getString("NOME");
                String email = resultSet.getString("EMAIL");
                String telefone = resultSet.getString("TELEFONE");
                int numero = resultSet.getInt("NUMERO");
                String complemento = resultSet.getString("COMPLEMENTO");
                String cep = resultSet.getString("CEP");
                LocalDate dataCadastro = resultSet.getDate("DATA_CADASTRO").toLocalDate();

                clientes cliente = new clientes(cpf, nome, email, telefone, numero, complemento, cep, dataCadastro);
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro ao inserir clientes", e);
        }
        return clientes;
    }

    public static void salvarClientes(List<clientes> clientes) {
        String sql = "INSERT INTO CLIENTE (CPF, NOME, EMAIL, TELEFONE, NUMERO, COMPLEMENTO, CEP, DATA_CADASTRO) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (clientes cliente : clientes) {
                statement.setString(1, cliente.getCpf());
                statement.setString(2, cliente.getNome());
                statement.setString(3, cliente.getEmail());
                statement.setString(4, cliente.getTelefone());
                statement.setInt(5, cliente.getNumero());
                statement.setString(6, cliente.getComplemento());
                statement.setString(7, cliente.getCep());
                statement.setDate(8, Date.valueOf(cliente.getDataCadastro()));
                statement.execute();
            }
            statement.close();
            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao inserir clientes", e);
        }
    }

    public static void excluirCliente(clientes cliente) {
        System.out.print("safdsafe");
        String sql = "DELETE FROM CLIENTE WHERE CPF = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cliente.getCpf());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.print("error: ");
            System.out.print(e.getErrorCode());
            mostrarAlerta("Erro ao inserir clientes", e);
        }
    }

    public static void editarCliente(clientes cliente) {
        String sql = "UPDATE CLIENTE SET NOME = ?, EMAIL = ?, TELEFONE = ?, NUMERO = ?, COMPLEMENTO = ?, CEP = ?, DATA_CADASTRO = ? WHERE CPF = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getEmail());
            statement.setString(3, cliente.getTelefone());
            statement.setInt(4, cliente.getNumero());
            statement.setString(5, cliente.getComplemento());
            statement.setString(6, cliente.getCep());
            statement.setDate(7, Date.valueOf(cliente.getDataCadastro()));
            statement.setString(8, cliente.getCpf());
            statement.executeUpdate();
            statement.close();            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao inserir clientes", e);
        }
    }

    public static List<clientes> procurarCliente(String cpf, String nome, String email, String telefone, String numero, String complemento, String cep) {
        List<clientes> resultados = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE WHERE CPF LIKE ? AND NOME LIKE ? AND EMAIL LIKE ? AND TELEFONE LIKE ? AND NUMERO LIKE ? AND COMPLEMENTO LIKE ? AND CEP LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + cpf + "%");
            statement.setString(2, "%" + nome + "%");
            statement.setString(3, "%" + email + "%");
            statement.setString(4, "%" + telefone + "%");
            statement.setString(5, "%" + numero + "%");
            statement.setString(6, "%" + complemento + "%");
            statement.setString(7, "%" + cep + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String cpfResult = resultSet.getString("CPF");
                    String nomeResult = resultSet.getString("NOME");
                    String emailResult = resultSet.getString("EMAIL");
                    String telefoneResult = resultSet.getString("TELEFONE");
                    int numeroResult = resultSet.getInt("NUMERO");
                    String complementoResult = resultSet.getString("COMPLEMENTO");
                    String cepResult = resultSet.getString("CEP");
                    LocalDate dataCadastro = resultSet.getDate("DATA_CADASTRO").toLocalDate();

                    clientes cliente = new clientes(cpfResult, nomeResult, emailResult, telefoneResult, numeroResult, complementoResult, cepResult, dataCadastro);
                    resultados.add(cliente);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao inserir clientes", e);
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
