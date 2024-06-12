/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.petshop_db.models;

import java.math.BigDecimal;

/**
 *
 * @author eduardo.cardozo
 */
public class OrcamentoTabela {
    private int id;
    private String cpf;
    private int quantidade;
    private BigDecimal preco;
    private int produtoId;
    private String formaPagamento;
    private String data;
    private int petId;

    // Construtor
    public OrcamentoTabela(int id, String cpf, int quantidade, BigDecimal preco, int produtoId, String formaPagamento, String data, int petId) {
        this.id = id;
        this.cpf = cpf;
        this.quantidade = quantidade;
        this.preco = preco;
        this.produtoId = produtoId;
        this.formaPagamento = formaPagamento;
        this.data = data;
        this.petId = petId;
    }
    public OrcamentoTabela(int id, int produtoId, int quantidade, BigDecimal preco) {
        this.id = id;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.preco = preco;
    }
    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
}