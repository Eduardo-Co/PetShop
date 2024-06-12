/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.petshop_db.models;

/**
 *
 * @author eduardo.cardozo
 */
public class pet {
    private int id;
    private String nome;
    private Sexo sexo;
    private String clienteId;
    private int racaId;
    
    public pet(int id, String nome, Sexo sexo, String clienteId, int racaId) {
        this.id = id;
        this.nome = nome;
        this.sexo = sexo;
        this.clienteId = clienteId;
        this.racaId = racaId;
    }
    public pet(String cpf, String nome, Sexo sexo, int racaId) {
        this.nome = nome;
        this.sexo = sexo;
        this.clienteId = cpf; 
        this.racaId = racaId;
    }
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

    public Sexo getSexo() {
        return sexo;
    }
 
    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public int getRacaId() {
        return racaId;
    }

    public void setRacaId(int racaId) {
        this.racaId = racaId;
    }
    public enum Sexo {
        MACHO,
        FEMEA
    }
}
