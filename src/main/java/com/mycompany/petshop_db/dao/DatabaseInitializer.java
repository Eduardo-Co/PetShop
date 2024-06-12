package com.mycompany.petshop_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            String[] createTableSQLs = {
                "CREATE TABLE IF NOT EXISTS CLIENTE (" +
                "    CPF CHAR(11) PRIMARY KEY, " +
                "    NOME VARCHAR(30) NOT NULL, " +
                "    EMAIL VARCHAR(100) NOT NULL, " +
                "    TELEFONE VARCHAR(12) NOT NULL, " +
                "    NUMERO INT NOT NULL, " +
                "    COMPLEMENTO VARCHAR(100), " +
                "    CEP CHAR(8) NOT NULL, " +
                "    DATA_CADASTRO DATE NOT NULL " +
                ");",

                "CREATE TABLE IF NOT EXISTS RACA (" +
                "    ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "    DESCRICAO VARCHAR(100) NOT NULL " +
                ");",

                "CREATE TABLE IF NOT EXISTS PET (" +
                "    ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "    NOME VARCHAR(100), " +
                "    SEXO ENUM('MACHO','FEMEA') NOT NULL, " +
                "    CLIENTE_ID CHAR(11), " +
                "    RACA_ID INT, " +
                "    FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(CPF), " +
                "    FOREIGN KEY (RACA_ID) REFERENCES RACA(ID) " +
                ");",
                

                "CREATE TABLE IF NOT EXISTS FORMASPAGAMENTO (" +
                "    PAGAMENTO VARCHAR(100) PRIMARY KEY, " +
                "    DESCRICAO VARCHAR(255) NOT NULL " +
                ");",

                "CREATE TABLE IF NOT EXISTS VETERINARIOS (" +
                "    CRMV VARCHAR(20) PRIMARY KEY, " +
                "    NOME VARCHAR(100) NOT NULL, " +
                "    ESPECIALIZACAO VARCHAR(100) NOT NULL, " +
                "    TELEFONE VARCHAR(12) NOT NULL " +
                ");",

                "CREATE TABLE IF NOT EXISTS PRODUTOS (" +
                "    ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "    NOME VARCHAR(100), " +
                "    DESCRICAO VARCHAR(255), " +
                "    PRECO DECIMAL(10,2) NOT NULL, " +
                "    TIPO ENUM('PRODUTO','SERVICO') NOT NULL, " +
                "    CRMV VARCHAR(20), " +
                "    FOREIGN KEY (CRMV) REFERENCES VETERINARIOS(CRMV) " +
                ");",

                "CREATE TABLE IF NOT EXISTS ORCAMENTOCABECALHO (" +
                "    ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "    DATA_ORCAMENTO DATE, " +
                "    CLIENTE_ID CHAR(11), " +
                "    FORMA_PAGAMENTO_ID VARCHAR(100), " +
                "    PET_ID INT, " +
                "    FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE(CPF), " +
                "    FOREIGN KEY (FORMA_PAGAMENTO_ID) REFERENCES FORMASPAGAMENTO(PAGAMENTO), " +
                "    FOREIGN KEY (PET_ID) REFERENCES PET(ID) " +
                ");",

                "CREATE TABLE IF NOT EXISTS ORCAMENTOITEM (" +
                "    ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "    ORCAMENTOCABECALHOID INT NOT NULL, " +
                "    PRODUTOID INT NOT NULL, " +
                "    QUANTIDADE INT NOT NULL, " +
                "    PRECOUNITARIO DECIMAL(10,2) NOT NULL, " +
                "    FOREIGN KEY (ORCAMENTOCABECALHOID) REFERENCES ORCAMENTOCABECALHO(ID), " +
                "    FOREIGN KEY (PRODUTOID) REFERENCES PRODUTOS(ID) " +
                ");"
            };

            for (String createTableSQL : createTableSQLs) {
                statement.execute(createTableSQL);
            }

            System.out.println("Tabelas criadas com sucesso.");
            String checkExistingPaymentsSQL = "SELECT COUNT(*) FROM FORMASPAGAMENTO";
            ResultSet resultSet = statement.executeQuery(checkExistingPaymentsSQL);
            resultSet.next();
            int paymentCount = resultSet.getInt(1);
            if (paymentCount == 0) {
                String insertFormaPagamentoSQL = "INSERT INTO FORMASPAGAMENTO (PAGAMENTO, DESCRICAO) " +
                        "VALUES " +
                        "('Débito', 'Pagamento via cartão de débito'), " +
                        "('Crédito', 'Pagamento via cartão de crédito'), " +
                        "('Boleto', 'Pagamento via boleto bancário'), " +
                        "('PIX', 'Pagamento instantâneo via PIX')";

                statement.execute(insertFormaPagamentoSQL);
                System.out.println("Formas de pagamento inseridas com sucesso.");
            } else {
                System.out.println("As formas de pagamento já foram inseridas anteriormente.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }
    public static List<String> getFormasPagamento() {
        List<String> formasPagamento = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT PAGAMENTO FROM FORMASPAGAMENTO");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                formasPagamento.add(resultSet.getString("PAGAMENTO"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter formas de pagamento: " + e.getMessage());
        }

        return formasPagamento;
    }
}
