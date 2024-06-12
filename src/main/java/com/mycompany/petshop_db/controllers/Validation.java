package com.mycompany.petshop_db.controllers;

import javafx.scene.control.Alert;

public class Validation {
    public static boolean isNumeroInteiro(String numero, String teste) {
        try {
            Integer.valueOf(numero);
            return true;
        } catch (NumberFormatException e) {
            mostrarAlerta(teste + " deve ser um valor inteiro");
            return false;
        }
    }

    public static boolean isCPFValido(String cpf) {
        try {
            if (cpf.length() != 11) {
                mostrarAlerta("CPF deve ter 11 dígitos");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar CPF");
            return false;
        }
    }

    public static boolean isTelefoneValido(String telefone) {
        for (int i = 0; i < telefone.length(); i++) {
            if (!Character.isDigit(telefone.charAt(i))) {
                mostrarAlerta("Telefone deve conter apenas números");
                return false;
            }
        }

        if (telefone.length() < 10 || telefone.length() > 11) {
            mostrarAlerta("Insira um número de telefone válido");
            return false;
        }

        return true;
    }

    public static boolean isCepValido(String cep) {
        try {
            if (cep.length() != 8) {
                mostrarAlerta("CEP deve ter 8 dígitos");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar CEP");
            return false;
        }
    }

    private static void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static boolean isAnyFieldEmpty(String cpf, String nome, String email, String telefone, int numero, String complemento, String cep) {
        try {
            if (cpf == null || cpf.trim().isEmpty() ||
                nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                telefone == null || telefone.trim().isEmpty() ||
                complemento == null || complemento.trim().isEmpty() || 
                cep == null || cep.trim().isEmpty() || numero <= 0) {
                    mostrarAlerta("Todos os campos devem ser preenchidos.");
                    return true;
            }
            return false;
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar campos");
            return false;
        }
    }
    public static boolean isAnyFieldEmpty(String cpf, String nome, String email, String telefone, String numero, String cep) {
        try {
            if (cpf == null || cpf.trim().isEmpty() ||
                nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                telefone == null || telefone.trim().isEmpty() ||
                cep == null || cep.trim().isEmpty()) {

                mostrarAlerta("Todos os campos devem ser preenchidos.");
                return true;
            }

            int numeroInt = Integer.parseInt(numero);
            if (numeroInt <= 0) {
                mostrarAlerta("O campo 'Número' deve ser um número inteiro maior que zero.");
                return true;
            }

            return false; 
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar campos");
            return true; 
        }
    }

    public static boolean isAnyFieldEmpty(String cpf, String nome, String sexo, String raca){
        try{
            if (cpf == null || cpf.trim().isEmpty() ||
                nome == null || nome.trim().isEmpty() ||
                sexo == null || sexo.trim().isEmpty() ||
                raca == null || raca.trim().isEmpty()) {
                    mostrarAlerta("Todos os campos devem ser preenchidos.");
                    return true;
            }
            return false;
        }catch(NumberFormatException e){
            mostrarAlerta("Erro ao validar campos");
            return false;
        }
    }

    public static boolean isAnyFieldEmpty(String cpf, String quantidade, String preco, String produtoId, String formaPagamento, String data, String petId) {
        try {
            if (cpf == null || cpf.trim().isEmpty() ||
                quantidade == null || quantidade.trim().isEmpty() ||
                preco == null || preco.trim().isEmpty() ||
                produtoId == null || produtoId.trim().isEmpty() ||
                formaPagamento == null || formaPagamento.trim().isEmpty() ||
                data == null || data.trim().isEmpty() ||
                petId == null || petId.trim().isEmpty()) {
                    mostrarAlerta("Todos os campos devem ser preenchidos.");
                    return true;
            }
            return false;
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar campos");
            return false;
        }
    }
    public static boolean isAnyFieldEmpty(String nome, String preco, String tipo, String crmv, String descricao) {
        try {
            if (nome == null || nome.trim().isEmpty() ||
                preco == null || preco.trim().isEmpty() ||
                tipo == null || tipo.trim().isEmpty() ||
                crmv == null || crmv.trim().isEmpty() ||
                descricao == null || descricao.trim().isEmpty()) {
                    mostrarAlerta("Todos os campos devem ser preenchidos.");
                    return true;
            }
            return false;
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar campos");
            return false;
        }
    }
    public static boolean isAnyFieldEmpty(String nome, String preco, String tipo) {
        try {
            if (nome == null || nome.trim().isEmpty() ||
                preco == null || preco.trim().isEmpty() ||
                tipo == null || tipo.trim().isEmpty()) {
                    mostrarAlerta("Todos os campos devem ser preenchidos.");
                    return true;
            }
            return false;
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro ao validar campos");
            return false;
        }
    }

}
