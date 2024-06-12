/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.petshop_db.models;

/**
 *
 * @author eduardo.cardozo
 */
public class veterinarios {
    private String crmv;
    private String nome;
    private String especializacao;
    private String telefone;
    
    public veterinarios(String crmv, String nome, String especializacao, String telefone) {
        this.crmv = crmv;
        this.nome = nome;
        this.especializacao = especializacao;
        this.telefone = telefone;
    }
    // Getters e Setters
    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecializacao() {
        return especializacao;
    }

    public void setEspecializacao(String especializacao) {
        this.especializacao = especializacao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
}
