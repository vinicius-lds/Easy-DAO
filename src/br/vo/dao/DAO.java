package br.vo.dao;

import br.util.StringUtil;
import br.vo.Condicao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe com o intuito de controlar todas as ações com o Banco de Dados MySQL
 * @author Vinícius Luis da Silva
 */
public class DAO {

    private final String USERNAME;
    private final String PASSWORD;
    private final String URL;

    /**
     * Cria uma insatancia da classe DAO, que tem como objetivo facilitar o uso do Banco de Dados MySQL
     * @param username Nome do usuario para conexão com o Banco de Dados desejado
     * @param password Senha para conexão com o Banco de Dados desejado
     * @param url Url para conexão com o Banco de Dados desejado
     * @throws SQLException Quando não for possível estabelecer a conexão com o Banco de Dados
     */
    public DAO(String username, String password, String url) throws SQLException {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.URL = url;
        this.get().close();
    }

    /**
     * Executa um comando do tipo INSERT no banco de dados definido nesse objeto DAO
     * @param tabela Nome da tabela em que será executada a operação
     * @param colunas Vetor com o nome das colunas que receberão novos valores
     * @param valores Vetor com os novos valores
     * @throws SQLException Caso ocorrer algum erro na execução do comando
     */
    public void insert(String tabela, String[] colunas, Object[] valores) throws SQLException {
        this.insert(tabela, colunas, new Object[][]{valores});
    }

    /**
     * Executa um comando do tipo INSERT no banco de dados definido nesse objeto DAO
     * @param tabela Nome da tabela em que será executada a operação
     * @param colunas Vetor com o nome das colunas que receberão novos valores
     * @param valores Vetor bidimensional com os novos valores
     * @throws SQLException Caso ocorrer algum erro na execução do comando
     */
    public void insert(String tabela, String[] colunas, Object[][] valores) throws SQLException {
        if (tabela == null) {
            throw new IllegalArgumentException("O nome da tabela não pode ser nulo!");
        }
        if (tabela.toString().isEmpty()) {
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
                sql += ((valores[i][j] == null) ? "null" : StringUtil.setAspas(valores[i][j].toString())) + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + "), ";
        }
        sql = sql.substring(0, sql.length() - 2) + ";";
        this.executarComando(sql).close();
    }
    
    /**
     * Executa um comando UPDATE no banco de dados definido nesse objeto DAO
     * @param tabela Nome da tabela em que será executada a operação
     * @param colunas Vetor com o nome das colunas que receberão novos valores
     * @param valores Vetor com os novos valores
     * @param condicao Condição que será aplicada no comando
     * @throws SQLException Caso ocorrer algum erro na execução do comando
     */
    public void update(String tabela, String[] colunas, Object[] valores, Condicao condicao) throws SQLException {
        if (tabela == null) {
            throw new IllegalArgumentException("O nome da tabela não pode ser nulo!");
        }
        if (tabela.toString().isEmpty()) {
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
            if (colunas[i].toString().isEmpty()) {
                throw new IllegalArgumentException("Nenhuma coluna pode conter uma string vazia!");
            }
            if (valores[i].toString().isEmpty()) {
                throw new IllegalArgumentException("Nenhum valor pode conter uma string vazia!");
            }
            sql += colunas[i] + " = " + StringUtil.setAspas(valores[i].toString()) + ", ";
        }
        sql = sql.substring(0, sql.length() - 2) + condicao;
        this.executarComando(sql).close();
    }

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um objeto SelectDAO onde será possível tranformar o resultado da pesquisa em uma coleção ou mapa
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public SelectDAO select(String[] colunas, String tabela, Condicao condicao) throws SQLException {
        if (tabela == null) {
            throw new IllegalArgumentException("O nome da tabela não pode ser nulo!");
        }
        if (tabela.toString().isEmpty()) {
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
        ResultSet rs = this.executarComando(sql).getResultSet();
        SelectDAO sd = SelectDAO.getInstace();
        sd.clear();
        sd.setDaoObject(this);
        sd.setResultSet(rs);
        sd.setTabelaQuery(tabela.toString());
        return sd;
    }

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um objeto SelectDAO onde será possível tranformar o resultado da pesquisa em uma coleção ou mapa
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public SelectDAO select(String tabela, Condicao condicao) throws SQLException {
        return this.select(null, tabela, condicao);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um objeto SelectDAO onde será possível tranformar o resultado da pesquisa em uma coleção ou mapa
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public SelectDAO select(String[] colunas, String tabela) throws SQLException {
        return this.select(colunas, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um objeto SelectDAO onde será possível tranformar o resultado da pesquisa em uma coleção ou mapa
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public SelectDAO select(String tabela) throws SQLException {
        return this.select(null, tabela, null);
    }
    
    /**
     * Executa um comando do tipo DELETE no banco de dados dfinido nesse objeto DAO
     * @param tabela Nome da tabela em que será executado o comando
     * @param condicao Condição que será aplicada na exclusão
     * @throws SQLException Caso ocorrer algo de errado na exclusão
     */
    public void delete(Object tabela, Condicao condicao) throws SQLException {
        String sql = "DELETE FROM " + tabela + condicao;
        this.executarComando(sql).close();
    }
    
    protected Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    private PreparedStatement executarComando(String sql) throws SQLException {
        System.out.println(sql);
        PreparedStatement pstmt = this.get().prepareStatement(sql);
        pstmt.execute();
        return pstmt;
    }

}
