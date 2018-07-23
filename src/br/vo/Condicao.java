package br.vo;

import br.enumeradores.Comando;
import br.util.StringUtil;
import java.util.Stack;

/**
 * @author Vinícius Luis da Silva
 */
public class Condicao {

    private String sql;
    private Stack<NoCondicao> condicoes;

    public Condicao() {
        this.sql = "";
        this.condicoes = new Stack();
    }

    private void addNo(Comando novo) {
        Comando ultimo = null;
        if (!condicoes.empty()) {
            ultimo = this.condicoes.peek().getComando();
        }
        if (ultimo == Comando.LIMIT) {
            throw new IllegalArgumentException("Não é possível adicionar nenhum comando após o LIMIT!");
        }
        if (ultimo == Comando.GROUP_BY && novo != Comando.LIMIT) {
            throw new IllegalArgumentException("Após o comando GROUP BY, só é possível adicionar o comando LIMIT!");
        }
        if (novo == Comando.WHERE && condicoes.size() > 0) {
            throw new IllegalArgumentException("O comando WHERE deve ser o primeiro a ser adicionado!");
        }
        this.condicoes.add(new NoCondicao(sql.length(), novo));
    }

    public Condicao like(String coluna, Object valor) {
        this.addNo(Comando.LIKE);
        this.sql += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas("%" + valor + "%");
        return this;
    }

    public Condicao likeFirst(String coluna, Object valor) {
        this.addNo(Comando.LIKE);
        this.sql += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas("%" + valor);
        return this;
    }

    public Condicao likeLast(String coluna, Object valor) {
        this.addNo(Comando.LIKE);
        this.sql += " " + coluna + " " + Comando.LIKE + " " + StringUtil.setAspas(valor + "%");
        return this;
    }

    public Condicao where() {
        this.addNo(Comando.WHERE);
        this.sql += " " + Comando.WHERE;
        return this;
    }

    public Condicao innerJoin(String tabelaAtual, String tabelaJoin, String primeiraColuna, String segundaColuna) {
        this.addNo(Comando.INNER_JOIN);
        this.sql += " "
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

    public Condicao innerJoin(String tabelaAtual, String tabelaJoin, String coluna) {
        this.addNo(Comando.INNER_JOIN);
        this.sql += " "
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

    public Condicao equals(String coluna, Object valor) {
        this.addNo(Comando.EQUALS);
        this.sql += " "
                + coluna
                + (valor == null || valor.equals("null")
                ? " " + Comando.IS + " null"
                : Comando.EQUALS + StringUtil.setAspas(valor.toString()));
        return this;
    }

    public Condicao and() {
        this.addNo(Comando.AND);
        this.sql += " " + Comando.AND;
        return this;
    }

    public Condicao or() {
        this.addNo(Comando.OR);
        this.sql += " " + Comando.OR;
        return this;
    }

    public Condicao groupBy(String coluna) {
        this.addNo(Comando.GROUP_BY);
        this.sql += " " + Comando.GROUP_BY + " " + coluna;
        return this;
    }

    public Condicao limit(int limite) {
        this.addNo(Comando.LIMIT);
        this.sql += " " + Comando.LIMIT + " " + limite;
        return this;
    }

    public Comando undo() {
        try {
            NoCondicao no = this.condicoes.pop();
            this.sql = this.sql.substring(0, no.getInicio());
            return no.getComando();
        } catch (Exception e) {}
        return null;
    }
    
    public Comando lastComand() {
        Comando command = null;
        try {
            command = this.condicoes.peek().getComando();
        } catch (Exception e) {}
        return command;
    }

    @Override
    public String toString() {
        return this.sql;
    }

}
