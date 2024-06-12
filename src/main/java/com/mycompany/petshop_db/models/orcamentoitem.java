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
public class orcamentoitem {
    private int id;
    private int orcamentoCabecalhoId;
    private int produtoId;
    private int quantidade;
    private BigDecimal precoUnitario;

    // Getters e Setters
    public orcamentoitem(int id, int orcamentoCabecalhoId, int produtoId, int quantidade, BigDecimal precoUnitario) {
        this.id = id;
        this.orcamentoCabecalhoId = orcamentoCabecalhoId;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }
    public orcamentoitem(BigDecimal precoUnitario, int quantidade, int produtoId) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrcamentoCabecalhoId() {
        return orcamentoCabecalhoId;
    }

    public void setOrcamentoCabecalhoId(int orcamentoCabecalhoId) {
        this.orcamentoCabecalhoId = orcamentoCabecalhoId;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    
}
