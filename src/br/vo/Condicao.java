package br.vo;

import br.enumeradores.Comando;
import br.util.StringUtil;

/**
 * @author Vinícius Luis da Silva
 */
public class Condicao {

    private String info;
    
    public Condicao() {
        info = "";
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return this.info;
    }
    
    public void addLike(String coluna, String valor) {
        info += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas("%" + valor + "%");
    }
    
    public void addLikeFirst(String coluna, String valor) {
        info += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas("%" + valor);
    }
    
    public void addLikeLast(String coluna, String valor) {
        info += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas(valor + "%");
    }
    
    public void addWhere() {
        info += " " + Comando.WHERE;
    }
    
    public void addInnerJoin(String tabelaAtual, String tabelaJoin, String primeiraColuna, String segundaColuna) {
        info += " " 
                + Comando.INNER_JOIN
                + " "
                + tabelaJoin
                + " "
                + Comando.ON 
                + " "
                + tabelaAtual + "." + primeiraColuna
                + Comando.EQUALS
                + tabelaJoin + "." + segundaColuna;
    }
    
    public void addInnerJoin(String tabelaAtual, String tabelaJoin, String coluna) {
        info += " " 
                + Comando.INNER_JOIN
                + " "
                + tabelaJoin
                + " "
                + Comando.ON 
                + " "
                + tabelaAtual + "." + coluna
                + Comando.EQUALS
                + tabelaJoin + "." + coluna;
    }
    
    public void addEquals(String coluna, String valor) {
        info += " " 
                + coluna 
                + (valor == null || valor.equals("null") ? 
                    " " + Comando.IS + " null" 
                :
                    Comando.EQUALS + StringUtil.setAspas(valor));
    }
    
    public void addAnd() {
        info += " " + Comando.AND;
    }
    
    public void addOr() {
        info += " " + Comando.OR;
    }
    
}
