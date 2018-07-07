package br.vo;

import br.enumeradores.Mapa;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Vinícius Luis da Silva
 */
public class MapFactory {
    
    public static Map get(Mapa mapa) {
        switch(mapa) {
            case HASH_MAP:
                return new HashMap();
            case TREE_MAP:
                return new TreeMap();
            case LINKED_HASH_MAP:
                return new LinkedHashMap();
            default:
                return null;
        }
    }
    
}
