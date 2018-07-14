/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.main;

import br.interfaces.Bean;

/**
 *
 * @author vinic
 */
public class Teste implements Bean {
    
    private int id;
    private String nome;

    @Override
    public void initialize(Object[] atributos) {
        System.out.println("oi");
        this.id = (atributos[0] == null) ? 0 : (int) atributos[0];
        this.nome = (String) atributos[1];
    }

    @Override
    public Object getPrimaryKey() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.id + " - " + this.nome; 
    }
    
    
    
}
