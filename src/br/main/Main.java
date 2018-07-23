package br.main;

import br.interfaces.Bean;
import br.vo.CollectionFactory;
import br.vo.Condicao;
import br.vo.dao.DAO;
import br.vo.dao.SelectDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Vinícius Luis da Silva
 */
public class Main {

    public static void main(String[] args) throws Exception {
        DAO d = new DAO("root", "", "jdbc:mysql://localhost:3306/teste");
        long inicio = System.nanoTime();
        HashMap<Object, Teste> rs = d.select(new String[] {"id"}, "teste", new Condicao().where().equals("id", "\\\\\" or 1=1 #")).setBean(Teste.class).toMap();
        //HashMap<Object, Object[]> rs = d.select(new String[] {"id"}, "teste").toMap();
        //ArrayList<Teste> rs = d.select("teste").setBean(Teste.class).toCollection();
        //ArrayList<Object[]> rs = d.select("teste").toCollection();
        System.out.println(((System.nanoTime() - inicio) / 1000000000.0) + " segundos!");
        System.out.println(rs);
    }
    
    public static void teste(Class<? extends Main> classe) {
        
    }

}
