package br.vo;

import br.enumeradores.Colecao;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

/**
 * @author Vinícius Luis da Silva
 */
public class CollectionFactory {
    
    public static Collection get(Colecao collection) {
        switch(collection) {
            case TREE_SET:
                return new TreeSet();
            case HASH_SET:
                return new HashSet();
            case LINKED_HASH_SET:
                return new LinkedHashSet();
            case VECTOR:
                return new Vector();
            case STACK:
                return new Stack();
            case ARRAY_LIST:
                return new ArrayList();
            case LINKED_LIST:
                return new LinkedList();
            case PRIORITY_QUEUE:
                return new PriorityQueue();
            default:
                return null;
        }
    }
    
}
