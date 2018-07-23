package br.vo.dao;

import br.enumeradores.Colecao;
import br.enumeradores.Mapa;
import br.interfaces.Bean;
import br.vo.CollectionFactory;
import br.vo.MapFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vinícius Luis da Silva
 */
public class SelectDAO {

    private static SelectDAO obj = null;

    protected static SelectDAO getInstace() {
        if (obj == null) {
            obj = new SelectDAO();
        }
        return obj;
    }

    private Class<? extends Bean> beanClass;
    private ResultSet mainResultSet;
    private Statement emptyStatement;
    private DAO daoObject;
    private String queryTable;
    private String tablePrimaryKeyName;
    private Integer tableColumnCount;
    private Integer primaryKeyIndex;
    private Integer tablePrimaryKeyIndex;

    public SelectDAO setBean(Class<? extends Bean> classeBean) {
        this.beanClass = classeBean;
        return this;
    }

    public SelectDAO setBean(String classeBean) throws ClassNotFoundException, ClassCastException {
        Class aux = Class.forName(classeBean);
        if (Bean.class.isAssignableFrom(aux)) {
            this.setBean(aux);
        } else {
            throw new ClassCastException(classeBean + " não pode ser convertida para Bean!");
        }
        return this;
    }

    private Bean getBeanInstace() {
        try {
            return this.beanClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    protected SelectDAO setResultSet(ResultSet rs) {
        this.mainResultSet = rs;
        return this;
    }

    protected void setDaoObject(DAO object) {
        this.daoObject = object;
    }

    protected void setTabelaQuery(String tabela) {
        this.queryTable = tabela;
    }

    public ArrayList toCollection() throws SQLException {
        ArrayList list = new ArrayList();
        this.populateCollection(list);
        return list;
    }

    public Collection generateCollection(Colecao colecao) throws SQLException {
        Collection c = CollectionFactory.get(colecao);
        this.populateCollection(c);
        return c;
    }

    public void populateCollection(Collection colecao) throws SQLException {
        if (this.beanClass == null) {
            this.populateWithObject(colecao);
        } else {
            this.initializeTableStructure();
            this.initiazileTableColumnCount();
            this.populateWithBean(colecao);
            this.emptyStatement.close();
        }
        this.mainResultSet.close();
    }

    public HashMap toMap() throws SQLException {
        HashMap map = new HashMap();
        this.populateMap(map);
        return map;
    }

    public Map generateMap(Mapa mapa) throws SQLException {
        Map map = MapFactory.get(mapa);
        this.populateMap(map);
        return map;
    }

    public void populateMap(Map mapa) throws SQLException {
        this.initializeTableStructure();
        this.initiazileTableColumnCount();
        this.initializePrimaryKeyName();
        this.initializePrimaryKeyIndex();
        if (this.beanClass == null) {
            this.populateWithObject(mapa);
        } else {
            this.initializeTablePrimaryKeyIndex();
            this.populateWithBean(mapa);
        }
        this.close();
    }

    public ResultSet toResultSet() throws SQLException {
        this.emptyStatement.close();
        return this.mainResultSet;
    }

    private void populateWithBean(Collection colecao) throws SQLException {
        Object[] row;
        Bean bean;
        while (mainResultSet.next()) {
            row = this.getBeanRow();
            bean = this.getBeanInstace();
            bean.initialize(row);
            colecao.add(bean);
        }
    }

    private void populateWithObject(Collection colecao) throws SQLException {
        Object[] row;
        while (mainResultSet.next()) {
            row = this.getShortRow();
            colecao.add(row);
        }
    }

    private void populateWithBean(Map mapa) throws SQLException {
        Object[] row;
        Bean bean;
        while (mainResultSet.next()) {
            row = this.getBeanRow();
            bean = this.getBeanInstace();
            bean.initialize(row);
            mapa.put(row[this.tablePrimaryKeyIndex - 1], bean);
        }
    }

    private void populateWithObject(Map mapa) throws SQLException {
        Object[] row;
        while (mainResultSet.next()) {
            row = this.getShortRow();
            mapa.put(row[this.primaryKeyIndex - 1], row);
        }
    }

    private Object[] getBeanRow() throws SQLException {
        Object[] row = new Object[this.tableColumnCount];
        String colunaMainResultSet, colunaTabela;
        for (int coluna = 1, i = 0; coluna <= this.mainResultSet.getMetaData().getColumnCount(); coluna++, i++) {
            colunaMainResultSet = mainResultSet.getMetaData().getColumnName(coluna);
            colunaTabela = this.emptyStatement.getResultSet().getMetaData().getColumnName(i + 1);
            if (!colunaMainResultSet.equals(colunaTabela)) {
                coluna--;
                continue;
            }
            row[i] = mainResultSet.getObject(coluna);
        }
        return row;
    }

    private Object[] getShortRow() throws SQLException {
        Object[] row = new Object[this.mainResultSet.getMetaData().getColumnCount()];
        for (int i = 0; i < row.length;) {
            row[i] = mainResultSet.getObject(++i);
        }
        return row;
    }

    private void initializePrimaryKeyIndex() throws SQLException {
        for (int i = 1; i <= this.mainResultSet.getMetaData().getColumnCount(); i++) {
            if (this.mainResultSet.getMetaData().getColumnName(i).equals(this.tablePrimaryKeyName)) {
                this.primaryKeyIndex = i;
                return;
            }
        }
        throw new IllegalArgumentException("A consulta deve retornar a chave primária para poder ser colocada em um Mapa!");
    }

    private void initializeTablePrimaryKeyIndex() throws SQLException {
        for (int i = 1; i <= this.tableColumnCount; i++) {
            if (this.emptyStatement.getResultSet().getMetaData().getColumnName(i).equals(this.tablePrimaryKeyName)) {
                this.tablePrimaryKeyIndex = i;
                return;
            }
        }
    }

    private void initializePrimaryKeyName() throws SQLException {
        Connection conn = this.daoObject.get();
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getPrimaryKeys(null, null, queryTable);
        rs.next();
        tablePrimaryKeyName = rs.getString(4);
        rs.close();
        conn.close();
    }

    private void initiazileTableColumnCount() throws SQLException {
        tableColumnCount = emptyStatement.getResultSet().getMetaData().getColumnCount();
    }

    private void initializeTableStructure() throws SQLException {
        emptyStatement = this.daoObject.get().createStatement();
        emptyStatement.execute("SELECT * FROM " + queryTable + " LIMIT 0");
    }

    protected void clear() {
        this.beanClass = null;
        this.daoObject = null;
        this.emptyStatement = null;
        this.mainResultSet = null;
        this.queryTable = null;
        this.tableColumnCount = null;
        this.tablePrimaryKeyName = null;
        this.primaryKeyIndex = null;
        this.tablePrimaryKeyIndex = null;
    }

    private void close() throws SQLException {
        this.emptyStatement.close();
        this.mainResultSet.close();
    }

}
