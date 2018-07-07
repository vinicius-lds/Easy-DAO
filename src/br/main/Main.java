package br.main;

import br.enumeradores.Colecao;
import br.enumeradores.Mapa;
import br.vo.Condicao;
import br.vo.DAO;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Vinícius Luis da Silva
 */
public class Main {

    public static void main(String[] args) throws SQLException {
        DAO d = new DAO("root", "", "jdbc:mysql://localhost:3306/teste");
        System.out.println(d.select(Mapa.HASH_MAP, "id", new String[]{"id", "nome"}, "teste", null).get(3)[0]);
        Condicao c = new Condicao();
        c.addWhere();
        c.addEquals("id", "3");
        System.out.println(((ArrayList<Object[]>)d.select(Colecao.ARRAY_LIST, new String[] {"nome"}, "teste", c)).get(0)[0]);
    }

}
