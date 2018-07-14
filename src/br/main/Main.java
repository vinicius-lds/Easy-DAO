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
        //Collection resultado = d.select(Colecao.ARRAY_LIST, "teste");
        //resultado = resultado.stream().filter((t) -> (Integer)t[0] < 5).limit(1).collect(Collectors.toList());
        //System.out.println(((ArrayList<Object[]>)resultado).get(0)[1]);;
        
        HashMap<Object, Teste> mapa = new HashMap();
        ArrayList<Teste> collecion = new ArrayList<>();
        d.select(null, "teste", null).setBean(Teste.class).populateMap(mapa);
        System.out.println(mapa);
        
        d.select("teste").setBean(Teste.class).populateCollection(collecion);
        System.out.println(collecion);
        
        /*SelectDAO sd = new SelectDAO();
        sd.getPrimaryKey("teste");*/
    }
    
    public static void teste(Class<? extends Main> classe) {
        
    }

}
