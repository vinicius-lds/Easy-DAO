package br.com.proway.dao;


import br.com.proway.util.StringUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Vinícius Luis da Silva
 */
public class DAO {

    private final String USERNAME;// = "root";
    private final String PASSWORD;// = "";
    private final String URL;// = "jdbc:mysql://localhost:3306/mercado";

    public DAO(String username, String password, String url) throws SQLException {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.URL = url;
        this.get().close();
    }

    private Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public void insert(String tabela, String[] colunas, String[][] valores) throws SQLException {
        if (tabela == null) {
            throw new IllegalArgumentException("O nome da tabela não pode ser nulo!");
        }
        if (tabela.isEmpty()) {
            throw new IllegalArgumentException("O nome da tabela não pode ser vazio!");
        }
        if (valores == null) {
            throw new IllegalArgumentException("O array de valores não pode ser nulo!");
        }

        String sql = "INSERT INTO " + tabela;
        if (colunas != null) {
            sql += "(";
            for (int i = 0; i < colunas.length; i++) {
                sql += colunas[i] + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + ")";
        }
        sql += " VALUES ";
        for (int i = 0; i < valores.length; i++) {
            if (valores[i].length != colunas.length) {
                throw new IllegalArgumentException("O número de colunas e de valores passados não bate!");
            }
            sql += "(";
            for (int j = 0; j < valores[i].length; j++) {
                sql += ((valores[i][j] == null) ? "null" : StringUtil.setAspas(valores[i][j])) + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + "), ";
        }
        sql = sql.substring(0, sql.length() - 2) + ";";
        this.executarComando(sql).close();
    }

    public void update(String tabela, String[] colunas, String[] valores, Condicao condicao) throws SQLException {
        if (tabela == null) {
            throw new IllegalArgumentException("O nome da tabela não pode ser nulo!");
        }
        if (tabela.isEmpty()) {
            throw new IllegalArgumentException("O nome da tabela não pode ser vazio!");
        }
        if (valores == null) {
            throw new IllegalArgumentException("O array de valores não pode ser nulo!");
        }
        if (valores.length < 1) {
            throw new IllegalArgumentException("O array de valores não pode ser vazio!");
        }
        if (colunas == null) {
            throw new IllegalArgumentException("O array de colunas não pode ser nulo!");
        }
        if (colunas.length < 1) {
            throw new IllegalArgumentException("O array de colunas não pode ser vazio!");
        }
        if (valores.length != colunas.length) {
            throw new IllegalArgumentException("O número de colunas e de valores passados não bate!");
        }
        String sql = "UPDATE " + tabela + " SET ";
        for (int i = 0; i < colunas.length; i++) {
            if (colunas[i] == null) {
                throw new IllegalArgumentException("Nenhuma coluna pode ser nula!");
            }
            if (valores[i] == null) {
                throw new IllegalArgumentException("Nenhum valor pode ser nulo!");
            }
            if (colunas[i].isEmpty()) {
                throw new IllegalArgumentException("Nenhuma coluna pode conter uma string vazia!");
            }
            if (valores[i].isEmpty()) {
                throw new IllegalArgumentException("Nenhum valor pode conter uma string vazia!");
            }
            sql += colunas[i] + " = " + StringUtil.setAspas(valores[i]) + ", ";
        }
        sql = sql.substring(0, sql.length() - 2) + condicao;
        this.executarComando(sql).close();
    }

    public ResultSet select(String[] colunas, String tabela, Condicao condicao) throws SQLException {
        if (tabela == null) {
            throw new IllegalArgumentException("O nome da tabela não pode ser nulo!");
        }
        if (tabela.isEmpty()) {
            throw new IllegalArgumentException("O nome da tabela não pode ser vazio!");
        }
        String sql = "SELECT ";
        if (colunas == null) {
            sql += "* ";
        } else {
            for (int i = 0; i < colunas.length; i++) {
                sql += colunas[i] + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + " ";
        }
        sql += "FROM " + tabela;
        if (condicao != null) {
            sql += condicao;
        }
        return this.executarComando(sql).getResultSet();
    }

    public void delete(String tabela, Condicao condicao) throws SQLException {
        String sql = "DELETE FROM " + tabela + condicao;
        this.executarComando(sql).close();
    }

    private PreparedStatement executarComando(String sql) throws SQLException {
        System.out.println(sql);
        PreparedStatement pstmt = this.get().prepareStatement(sql);
        pstmt.execute();
        return pstmt;
    }
    
}
