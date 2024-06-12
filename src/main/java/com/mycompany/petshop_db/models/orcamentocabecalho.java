/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.petshop_db.models;

import java.time.LocalDate;

/**
 *
 * @author eduardo.cardozo
 */
public class orcamentocabecalho {
    private int id;
    private LocalDate dataOrcamento;
    private String clienteId;
    private String formaPagamentoId;
    private int petId;

    // Getters e Setters
    public orcamentocabecalho(int id,LocalDate dataCadastro,String pagamento,String cpf,int petId){
        this.id = id;
        this.dataOrcamento = dataCadastro;
        this.clienteId = cpf;
        this.formaPagamentoId = pagamento;
        this.petId = petId;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDataOrcamento() {
        return dataOrcamento;
    }

    public void setDataOrcamento(LocalDate dataOrcamento) {
        this.dataOrcamento = dataOrcamento;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getFormaPagamentoId() {
        return formaPagamentoId;
    }

    public void setFormaPagamentoId(String formaPagamentoId) {
        this.formaPagamentoId = formaPagamentoId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }
   
}
