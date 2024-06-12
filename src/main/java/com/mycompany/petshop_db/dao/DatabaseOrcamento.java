package com.mycompany.petshop_db.dao;

import com.mycompany.petshop_db.models.OrcamentoTabela;
import com.mycompany.petshop_db.models.orcamentocabecalho;
import com.mycompany.petshop_db.models.orcamentoitem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DatabaseOrcamento {
    
    private static int LastOrcamento;
    
    public static List<orcamentocabecalho> getCabecalho() {
        List<orcamentocabecalho> cabecalho = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM orcamentocabecalho");
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                LocalDate dataCadastro = resultSet.getDate("DATA_ORCAMENTO").toLocalDate();
                String pagamento = resultSet.getString("FORMA_PAGAMENTO_ID");
                String cpf = resultSet.getString("CLIENTE_ID");
                int petId = resultSet.getInt("PET_ID");
                
                orcamentocabecalho orcamentoC = new orcamentocabecalho(id, dataCadastro, pagamento, cpf, petId);
                cabecalho.add(orcamentoC);
            }
           
        } catch (SQLException e) {
            System.err.println("Erro ao obter cabeçalhos de orçamento: " + e.getMessage());
        }
        return cabecalho;
    }
    
    public static int salvarOrcamentoCabecalho(String cpfCliente, String formaPagamento, LocalDate data, int idPet) {
        if (!clienteExists(cpfCliente)) {
            exibirPopup("Não existe cliente com o CPF informado.");
            return 0;
        }
        if (!petExists(idPet)) {
            exibirPopup("Não existe pet com o ID informado.");
            return 0;
        }
        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertSQL = "INSERT INTO ORCAMENTOCABECALHO (CLIENTE_ID, FORMA_PAGAMENTO_ID, DATA_ORCAMENTO, PET_ID) VALUES (?,?,?,?)";

            try (PreparedStatement statement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, cpfCliente);
                statement.setString(2, formaPagamento);
                statement.setDate(3, java.sql.Date.valueOf(data));
                statement.setInt(4, idPet);

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao salvar o cabeçalho do orçamento, nenhuma linha afetada.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Sucesso");
                        alert.setHeaderText(null);
                        alert.setContentText("Orçamento(s) salvo(s) com sucesso");
                        alert.showAndWait();
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Falha ao salvar o cabeçalho do orçamento, nenhum ID obtido.");
                    }
                }
            }      
        } catch (SQLException e) {
            System.err.println("Erro ao salvar o cabeçalho do orçamento: " + e.getMessage());
            return -1;
        }
    }

    public static void salvarOrcamentoItem(OrcamentoTabela item, int idCabecalho) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertSQL = "INSERT INTO ORCAMENTOITEM (ORCAMENTOCABECALHOID, PRODUTOID, QUANTIDADE, PRECOUNITARIO) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setInt(1, idCabecalho);
                statement.setInt(2, item.getProdutoId());
                statement.setInt(3, item.getQuantidade());
                statement.setBigDecimal(4, item.getPreco());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao salvar o item do orçamento, nenhuma linha afetada.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar o item do orçamento: " + e.getMessage());
        }
    }
    public static void salvarOrcamentoItem(orcamentoitem item, int idCabecalho) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String insertSQL = "INSERT INTO ORCAMENTOITEM (ORCAMENTOCABECALHOID, PRODUTOID, QUANTIDADE, PRECOUNITARIO) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
                statement.setInt(1, idCabecalho);
                statement.setInt(2, item.getProdutoId());
                statement.setInt(3, item.getQuantidade());
                statement.setBigDecimal(4, item.getPrecoUnitario());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Falha ao salvar o item do orçamento, nenhuma linha afetada.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar o item do orçamento: " + e.getMessage());
        }
    }
    
    private static boolean clienteExists(String cpfCliente) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS count FROM CLIENTE WHERE CPF = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, cpfCliente);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar a existência do cliente: " + e.getMessage());
        }
        return false;
    }

    private static boolean petExists(int idPet) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS count FROM PET WHERE ID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idPet);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar a existência do pet: " + e.getMessage());
        }
        return false;
    }
    
    public static void editarCabecalho(orcamentocabecalho cabecalho) {
        String sql = "UPDATE orcamentocabecalho SET DATA_ORCAMENTO = ?, FORMA_PAGAMENTO_ID = ?, CLIENTE_ID = ?, PET_ID = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, java.sql.Date.valueOf(cabecalho.getDataOrcamento()));
            statement.setString(2, cabecalho.getFormaPagamentoId());
            statement.setString(3, cabecalho.getClienteId());
            statement.setInt(4, cabecalho.getPetId());
            statement.setInt(5, cabecalho.getId());

            statement.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Cabeçalho de orçamento editado com sucesso");
            alert.showAndWait();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao editar cabeçalho de orçamento", e);
        }
    }

    public static void excluirCabecalho(orcamentocabecalho cabecalho) {
        String sql = "DELETE FROM orcamentocabecalho WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, cabecalho.getId());
            statement.executeUpdate();

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Cabeçalho de orçamento excluído com sucesso");
            alert.showAndWait();

        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir cabeçalho de orçamento", e);
        }
    }

    public static List<orcamentocabecalho> procurarCabecalho(String formaPagamentoId, String clienteId, LocalDate data, String petId) {
        List<orcamentocabecalho> resultados = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM orcamentocabecalho WHERE 1=1");

        if (formaPagamentoId != null && !formaPagamentoId.isEmpty()) {
            sqlBuilder.append(" AND FORMA_PAGAMENTO_ID LIKE ?");
        }
        if (clienteId != null && !clienteId.isEmpty()) {
            sqlBuilder.append(" AND CLIENTE_ID LIKE ?");
        }
        if (data != null) {
            sqlBuilder.append(" AND DATA_ORCAMENTO = ?");
        }
        if (petId != null && !petId.isEmpty()) {
            sqlBuilder.append(" AND PET_ID = ?");
        }

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString())) {

            int parameterIndex = 1;
            if (formaPagamentoId != null && !formaPagamentoId.isEmpty()) {
                statement.setString(parameterIndex++, "%" + formaPagamentoId + "%");
            }
            if (clienteId != null && !clienteId.isEmpty()) {
                statement.setString(parameterIndex++, "%" + clienteId + "%");
            }
            if (data != null) {
                statement.setDate(parameterIndex++, java.sql.Date.valueOf(data));
            }
            if (petId != null && !petId.isEmpty()) {
                statement.setString(parameterIndex++, petId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    LocalDate dataOrcamento = resultSet.getDate("DATA_ORCAMENTO").toLocalDate();
                    String formaPagamento = resultSet.getString("FORMA_PAGAMENTO_ID");
                    String cpfCliente = resultSet.getString("CLIENTE_ID");
                    int petIdResult = resultSet.getInt("PET_ID");

                    orcamentocabecalho cabecalho = new orcamentocabecalho(id, dataOrcamento, formaPagamento, cpfCliente, petIdResult);
                    resultados.add(cabecalho);
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Erro ao procurar cabeçalho de orçamento", e);
        }
        return resultados;
    }
    
    private static void exibirPopup(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
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
    
    public static List<orcamentoitem> getItens() {
        
        List<orcamentoitem> itens = new ArrayList<>();

        String sql = "SELECT * FROM ORCAMENTOITEM WHERE ORCAMENTOCABECALHOID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, LastOrcamento);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    int cabecalhoId = resultSet.getInt("ORCAMENTOCABECALHOID");
                    int produtoId = resultSet.getInt("PRODUTOID");
                    int quantidade = resultSet.getInt("QUANTIDADE");
                    BigDecimal preco = resultSet.getBigDecimal("PRECOUNITARIO");

                    orcamentoitem item = new orcamentoitem(id, cabecalhoId, produtoId, quantidade, preco);
                    itens.add(item);
                    
                    System.out.print(LastOrcamento);
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao obter itens de orçamento", e);
        }
        return itens;
    }
    
    public static void excluirItem(orcamentoitem item) {
        String sql = "DELETE FROM ORCAMENTOITEM WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, item.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao excluir item de orçamento", e);
        }
    }

    public static void editarItem(orcamentoitem item) {
        String sql = "UPDATE ORCAMENTOITEM SET PRODUTOID = ?, QUANTIDADE = ?, PRECOUNITARIO = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, item.getProdutoId());
            statement.setInt(2, item.getQuantidade());
            statement.setBigDecimal(3, item.getPrecoUnitario());
            statement.setInt(4, item.getId());

            statement.executeUpdate();


        } catch (SQLException e) {
            mostrarAlerta("Erro ao editar item de orçamento", e);
        }
    }
    
    public static void setLast(int Last) {
        LastOrcamento = Last;
    }
    public static int getLast() {
        return LastOrcamento;
    }
}
