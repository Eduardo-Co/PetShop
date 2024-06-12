package com.mycompany.petshop_db.models;

/**
 * Modelo da classe produtos.
 * Representa os produtos do sistema Petshop.
 * 
 * @autor eduardo.cardozo
 */
public class produtos {
    private int id;
    private String nome;
    private String descricao;
    private Double preco;
    private String tipo;
    private String crmv;

    // Construtor para criar novos produtos (sem ID)
    public produtos(String nome, String descricao, Double preco, String tipo, String crmv) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tipo = tipo;
        this.crmv = crmv;
    }

    // Construtor para produtos existentes (com ID)
    public produtos(int id, String nome, String descricao, Double preco, String tipo, String crmv) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.tipo = tipo;
        this.crmv = crmv;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }
}
