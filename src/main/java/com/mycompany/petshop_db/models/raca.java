/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.petshop_db.models;

/**
 *
 * @author eduardo.cardozo
 */
public class raca {
    private int id;
    private String descricao;

    // Getters e Setters
    public raca(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public raca() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
