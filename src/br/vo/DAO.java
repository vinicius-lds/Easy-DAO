package br.vo;

import br.enumeradores.Colecao;
import br.enumeradores.Mapa;
import br.util.StringUtil;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Vinícius Luis da Silva
 */
public class DAO {

    private final String USERNAME;
    private final String PASSWORD;
    private final String URL;

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
    public void insert(String tabela, String[] colunas, String[] valores) throws SQLException {
        this.insert(tabela, colunas, new String[][]{valores});
    }

    /**
     * Executa um comando do tipo INSERT no banco de dados definido nesse objeto DAO
     * @param tabela Nome da tabela em que será executada a operação
     * @param colunas Vetor com o nome das colunas que receberão novos valores
     * @param valores Vetor bidimensional com os novos valores
     * @throws SQLException Caso ocorrer algum erro na execução do comando
     */
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
    
    /**
     * Executa um comando UPDATE no banco de dados definido nesse objeto DAO
     * @param tabela Nome da tabela em que será executada a operação
     * @param colunas Vetor com o nome das colunas que receberão novos valores
     * @param valores Vetor com os novos valores
     * @param condicao Condição que será aplicada no comando
     * @throws SQLException Caso ocorrer algum erro na execução do comando
     */
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

    /**
     * @deprecated É recomendado usar outra versão desse método, que retorna uma Lista ou um Mapa. Caso não for possível você deve se lembrar de fechar a conexão usando o método close() da classe ResultSet
     * <br><br>
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um ResultSet com o conteúdo da pesquisa
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
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

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * <br><br>
     * <b>ATENÇÃO: A Primary Key deve ser a primeira coluna na tabela, caso contrário poderá ocorrer uma exception ou o Mapa final pode não ser o esperado!</b>
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String[] colunas, String tabela, Condicao condicao) throws SQLException {
        ResultSet rs = this.select(colunas, tabela, condicao);
        Object[] row;
        int collumnCount = rs.getMetaData().getColumnCount();
        Map<Object, Object[]> map = MapFactory.get(mapa);
        while (rs.next()) {
            row = new Object[collumnCount - 1];
            for (int coluna = 2, i = 0; coluna <= collumnCount; coluna++, i++) {
                row[i] = rs.getObject(coluna);
            }
            map.put(rs.getObject(1), row);
        }
        rs.close();
        return map;
    }

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O nome da coluna definida como Primary Key
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String primaryKey, String[] colunas, String tabela, Condicao condicao) throws SQLException {
        ResultSet rs = this.select(colunas, tabela, condicao);
        Map<Object, Object[]> map = MapFactory.get(mapa);
        if(!rs.next()) {
            return map;
        }
        Object[] row;
        ResultSetMetaData metaData = rs.getMetaData();
        int collumnCount = metaData.getColumnCount();
        int pk = this.getPrimaryKey(rs, metaData, collumnCount, primaryKey);
        do {
            row = new Object[collumnCount - 1];
            for (int coluna = 1, i = 0; coluna <= collumnCount; coluna++, i++) {
                if(coluna == pk) {
                    i--;
                    continue;
                }
                row[i] = rs.getObject(coluna);
            }
            map.put(rs.getObject(pk), row);
        } while (rs.next());
        rs.close();
        return map;
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O indice definida como Primary Key. O índice é calculdado como no Banco de Dados, ou seja, ele começa no 1, e não no0
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, int primaryKey, String[] colunas, String tabela, Condicao condicao) throws SQLException {
        ResultSet rs = this.select(colunas, tabela, condicao);
        Map<Object, Object[]> map = MapFactory.get(mapa);
        Object[] row;
        int collumnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            row = new Object[collumnCount - 1];
            for (int coluna = 1, i = 0; coluna <= collumnCount; coluna++, i++) {
                if(coluna == primaryKey) {
                    i--;
                    continue;
                }
                row[i] = rs.getObject(coluna);
            }
            map.put(rs.getObject(primaryKey), row);
        }
        rs.close();
        return map;
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param collection Enum representando o tipo de collection que será retornado com os resultados. (Casting necessário)
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Uma Coleção da sua escolha tendo como conteúdo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Collection<Object[]> select(Colecao collection, String[] colunas, String tabela, Condicao condicao) throws SQLException {
        ResultSet rs = this.select(colunas, tabela, condicao);
        Collection<Object[]> collectionResultante = CollectionFactory.get(collection);
        Object[] row;
        int collumnCount = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            row = new Object[collumnCount];
            for (int coluna = 1, i = 0; coluna <= collumnCount; coluna++, i++) {
                row[i] = rs.getObject(coluna);
            }
            collectionResultante.add(row);
        }
        rs.close();
        return collectionResultante;
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param collection Enum representando o tipo de collection que será retornado com os resultados. (Casting necessário)
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Uma Coleção da sua escolha tendo como conteúdo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Collection<Object[]> select(Colecao collection, String tabela, Condicao condicao) throws SQLException {
        return this.select(collection, null, tabela, condicao);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param collection Enum representando o tipo de collection que será retornado com os resultados. (Casting necessário)
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @return Uma Coleção da sua escolha tendo como conteúdo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Collection<Object[]> select(Colecao collection, String[] colunas, String tabela) throws SQLException {
        return this.select(collection, colunas, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param collection Enum representando o tipo de collection que será retornado com os resultados. (Casting necessário)
     * @param tabela Nome da tabela que será feita a consulta
     * @return Uma Coleção da sua escolha tendo como conteúdo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Collection<Object[]> select(Colecao collection, String tabela) throws SQLException {
        return this.select(collection, null, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * <br><br>
     * <b>ATENÇÃO: A Primary Key deve ser a primeira coluna na tabela, caso contrário poderá ocorrer uma exception ou o Mapa final pode não ser o esperado!</b>
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String tabela, Condicao condicao) throws SQLException {
        return this.select(mapa, (String[])null, tabela, condicao);
    }

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * <br><br>
     * <b>ATENÇÃO: A Primary Key deve ser a primeira coluna na tabela, caso contrário poderá ocorrer uma exception ou o Mapa final pode não ser o esperado!</b>
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String tabela) throws SQLException {
        return this.select(mapa, (String[])null, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O nome da coluna definida como Primary Key
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String primaryKey, String tabela, Condicao condicao) throws SQLException {
        return this.select(mapa, primaryKey, null, tabela, condicao);
    }

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O nome da coluna definida como Primary Key
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String primaryKey, String[] colunas, String tabela) throws SQLException {
        return this.select(mapa, primaryKey, null, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O nome da coluna definida como Primary Key
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, String primaryKey, String tabela) throws SQLException {
        return this.select(mapa, primaryKey, null, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O indice definida como Primary Key. O índice é calculdado como no Banco de Dados, ou seja, ele começa no 1, e não no0
     * @param tabela Nome da tabela que será feita a consulta
     * @param condicao Condicao que será aplicada na consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, int primaryKey, String tabela, Condicao condicao) throws SQLException {
        return this.select(mapa, primaryKey, null, tabela, condicao);
    }

    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O indice definida como Primary Key. O índice é calculdado como no Banco de Dados, ou seja, ele começa no 1, e não no0
     * @param colunas Vetor com o nome das colunas que serão pesquisadas
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, int primaryKey, String[] colunas, String tabela) throws SQLException {
        return this.select(mapa, primaryKey, colunas, tabela, null);
    }
    
    /**
     * Será executado um comando do tipo SELECT no banco de dados definido nesse objeto DAO.
     * @param mapa Enum representando o tipo de mapa que será retornado com os resultados. (Casting necessário)
     * @param primaryKey O indice definida como Primary Key. O índice é calculdado como no Banco de Dados, ou seja, ele começa no 1, e não no0
     * @param tabela Nome da tabela que será feita a consulta
     * @return Um Map da sua escolha tendo como Primary Key o rgistro na primeira coluna da tabela, e como conteudo um vetor de objetos com os registros
     * @throws SQLException Caso ocorreu algo de errado na consulta
     */
    public Map<Object, Object[]> select(Mapa mapa, int primaryKey, String tabela) throws SQLException {
        return this.select(mapa, primaryKey, null, tabela, null);
    }
    
    /**
     * Executa um comando do tipo DELETE no banco de dados dfinido nesse objeto DAO
     * @param tabela Nome da tabela em que será executado o comando
     * @param condicao Condição que será aplicada na exclusão
     * @throws SQLException Caso ocorrer algo de errado na exclusão
     */
    public void delete(String tabela, Condicao condicao) throws SQLException {
        String sql = "DELETE FROM " + tabela + condicao;
        this.executarComando(sql).close();
    }
    
    private Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    private PreparedStatement executarComando(String sql) throws SQLException {
        System.out.println(sql);
        PreparedStatement pstmt = this.get().prepareStatement(sql);
        pstmt.execute();
        return pstmt;
    }
    
    private int getPrimaryKey(ResultSet rs, ResultSetMetaData metaData, int collumnCount, String primaryKey) throws SQLException {
        for (int i = 1; i <= collumnCount; i++) {
            if (metaData.getColumnName(i).equals(primaryKey)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Não existem nenhuma coluna com o nome " + primaryKey);
    }

}
