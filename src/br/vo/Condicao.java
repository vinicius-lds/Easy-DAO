package br.vo;

import br.enumeradores.Comando;
import br.util.StringUtil;
import java.util.Stack;

/**
 * @author Vinícius Luis da Silva
 */
public class Condicao {

    private String info;
    private Stack<NoCondicao> coordenadas;
        
    public Condicao() {
        info = "";
        coordenadas = new Stack();
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return this.info;
    }
    
    private void addNo(Comando novo) {
        Comando ultimo = null;
        if(!coordenadas.empty()) {
            ultimo = this.coordenadas.peek().getComando();
        }
        if(ultimo == Comando.LIMIT) {
            throw new IllegalArgumentException("Não é possível adicionar nenhum comando após o LIMIT!");
        }
        if(ultimo == Comando.GROUP_BY && novo != Comando.LIMIT) {
            throw new IllegalArgumentException("Após o comando GROUP BY, só é possível adicionar o comando LIMIT!");
        }
        if(novo == Comando.WHERE && coordenadas.size() > 0) {
            throw new IllegalArgumentException("O comando WHERE deve ser o primeiro a ser adicionado!");
        }
        coordenadas.add(new NoCondicao(info.length(), novo));
    }
    
    public Condicao addLike(String coluna, Object valor) {
        this.addNo(Comando.LIKE);
        info += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas("%" + valor + "%");
        return this;
    }
    
    public Condicao addLikeFirst(String coluna, Object valor) {
        this.addNo(Comando.LIKE);
        info += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas("%" + valor);
        return this;
    }
    
    public Condicao addLikeLast(String coluna, Object valor) {
        this.addNo(Comando.LIKE);
        info += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas(valor + "%");
        return this;
    }
    
    public Condicao addWhere() {
        this.addNo(Comando.WHERE);
        info += " " + Comando.WHERE;
        return this;
    }
    
    public Condicao addInnerJoin(String tabelaAtual, String tabelaJoin, String primeiraColuna, String segundaColuna) {
        this.addNo(Comando.INNER_JOIN);
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
        return this;
    }
    
    public Condicao addInnerJoin(String tabelaAtual, String tabelaJoin, String coluna) {
        this.addNo(Comando.INNER_JOIN);
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
        return this;
    }
    
    public Condicao addEquals(String coluna, Object valor) {
        this.addNo(Comando.EQUALS);
        info += " " 
                + coluna 
                + (valor == null || valor.equals("null") ? 
                    " " + Comando.IS + " null" 
                :
                    Comando.EQUALS + StringUtil.setAspas(valor.toString()));
        return this;
    }
    
    public Condicao addAnd() {
        this.addNo(Comando.AND);
        info += " " + Comando.AND;
        return this;
    }
    
    public Condicao addOr() {
        this.addNo(Comando.OR);
        info += " " + Comando.OR;
        return this;
    }

    public Condicao addGroupBy(String coluna) {
        this.addNo(Comando.GROUP_BY);
        info += " " + Comando.GROUP_BY + " " + coluna;
        return this;
    }
    
    public Condicao addLimit(int limite) {
        this.addNo(Comando.LIMIT);
        info += " " + Comando.LIMIT + " " + limite;
        return this;
    }
    
    public void undo() {
        try {
            info = this.info.substring(0, this.coordenadas.pop().getInicio());
        } catch (Exception e) {}
    }
    
}
