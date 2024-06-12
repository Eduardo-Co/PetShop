package com.mycompany.petshop_db.dao;

import com.mycompany.petshop_db.models.produtos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DatabaseProdutos {
    public static List<produtos> getProdutos() {
        List<produtos> produtos = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM PRODUTOS");
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nome = resultSet.getString("NOME");
                double preco = resultSet.getDouble("PRECO");
                String tipo = resultSet.getString("TIPO");
                String crmv = resultSet.getString("CRMV");
                String descricao = resultSet.getString("DESCRICAO");
                
                produtos produto = new produtos(id, nome, descricao, preco, tipo, crmv);
                produtos.add(produto);
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao obter produtos", e);
        }
        return produtos;
    }
    
    public static void salvarProduto(List<produtos> produtos) {
        String sql = "INSERT INTO PRODUTOS (NOME, PRECO, TIPO, CRMV, DESCRICAO) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (produtos produto : produtos) {
                statement.setString(1, produto.getNome());
                statement.setDouble(2, produto.getPreco());
                statement.setString(3, produto.getTipo());
                statement.setString(4, produto.getCrmv());
                statement.setString(5, produto.getDescricao());

                statement.executeUpdate();
            }
                       
        } catch (SQLException e) {
            mostrarAlerta("Erro ao salvar produtos", e);
        }
    }
    
    public static void excluirProduto(produtos produto) {
        String sql = "DELETE FROM PRODUTOS WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, produto.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir produtos", e);
        }
    }

    public static List<produtos> procurarProduto(String nome, String preco, String tipo, String crmv, String descricao) {
        List<produtos> resultados = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM PRODUTOS WHERE 1=1 ");

        // Debugging - Verifique os valores de entrada
        System.out.println("Parametros de Entrada: ");
        System.out.println("Nome: " + nome);
        System.out.println("Preco: " + preco);
        System.out.println("Tipo: " + tipo);
        System.out.println("CRMV: " + crmv);
        System.out.println("Descricao: " + descricao);

        if (nome != null && !nome.isEmpty()) {
            sql.append("AND NOME LIKE ? ");
        }
        if (preco != null && !preco.isEmpty()) {
            sql.append("AND PRECO = ? ");
        }
        if (tipo != null && !tipo.isEmpty()) {
            sql.append("AND TIPO = ? ");
        }
        if (crmv != null && !crmv.isEmpty()) {
            sql.append("AND CRMV = ? ");
        }
        if (descricao != null && !descricao.isEmpty()) {
            sql.append("AND DESCRICAO LIKE ? ");
        }

        // Debugging - Verifique a query SQL final
        System.out.println("Query SQL: " + sql.toString());

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            int index = 1;
            if (nome != null && !nome.isEmpty()) {
                statement.setString(index++, "%" + nome + "%");
            }
            if (preco != null && !preco.isEmpty()) {
                statement.setString(index++, preco);
            }
            if (tipo != null && !tipo.isEmpty()) {
                statement.setString(index++, tipo);
            }
            if (crmv != null && !crmv.isEmpty()) {
                statement.setString(index++, crmv);
            }
            if (descricao != null && !descricao.isEmpty()) {
                statement.setString(index++, "%" + descricao + "%");
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String nomeProduto = resultSet.getString("NOME");
                String descricaoProduto = resultSet.getString("DESCRICAO");
                double precoProduto = resultSet.getDouble("PRECO");
                String tipoProduto = resultSet.getString("TIPO");
                String crmvProduto = resultSet.getString("CRMV");

                produtos produto = new produtos(id, nomeProduto, descricaoProduto, precoProduto, tipoProduto, crmvProduto);
                resultados.add(produto);
                            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao procurar produtos", e);
        }
        return resultados;
    }

    public static void editarProduto(produtos produto) {
        String sql = "UPDATE PRODUTOS SET NOME = ?, PRECO = ?, TIPO = ?, CRMV = ?, DESCRICAO = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, produto.getNome());
            statement.setDouble(2, produto.getPreco());
            statement.setString(3, produto.getTipo());
            statement.setString(4, produto.getCrmv());
            statement.setString(5, produto.getDescricao());
            statement.setInt(6, produto.getId());
            statement.executeUpdate();


            
        } catch (SQLException e) {
            mostrarAlerta("Erro ao editar produtos", e);
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
