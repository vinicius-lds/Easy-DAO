package br.main;

import br.enumeradores.Mapa;
import br.vo.Condicao;
import br.vo.DAO;
import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 * @author Vinícius Luis da Silva
 */
public class Main {

    public static void main(String[] args) throws SQLException {
        DAO d = new DAO("root", "", "jdbc:mysql://localhost:3306/teste");
        Condicao c = new Condicao().addWhere().addEquals("id", "3");
        LinkedHashMap<Object, Object[]> resultado = new LinkedHashMap<>();// = (LinkedHashMap<Object, Object[]>)d.select(Mapa.LINKED_HASH_MAP, 1, "teste", c);
        d.select(resultado, 1, null, "teste", null);
        System.out.println(resultado.get(3)[0]);
        //d.update("teste", new String[]{"nome"}, new String[]{"Vinícius Luis da Silva"}, c);
        //resultado = (LinkedHashMap<Object, Object[]>)d.select(Mapa.LINKED_HASH_MAP, 1, "teste", c);
        System.out.println(resultado.get(3)[0]);
        //d.insert("teste", new String[]{"nome"}, new String[][]{{"Larissa"},{"Bertrand"}, {"Thays"}, {"Matheus"}});
        //resultado = (LinkedHashMap<Object, Object[]>)d.select(Mapa.LINKED_HASH_MAP, 1, "teste");
        System.out.println(resultado.get(3)[0]);
        System.out.println(resultado.get(4)[0]);
        System.out.println(resultado.get(5)[0]);
        System.out.println(resultado.get(6)[0]);
        System.out.println(resultado.get(7)[0]);
//        System.out.println(resultado.get(8)[0]);
    }

}
