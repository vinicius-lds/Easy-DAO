package br.enumeradores;

/**
 * @author Vinícius Luis da Silva
 */
public enum Comando {
    
    WHERE("WHERE"), 
    EQUALS("="), 
    LIKE("LIKE"), 
    INNER_JOIN("INNER JOIN"), 
    AND("AND"), 
    OR("OR"), 
    ON("ON"), 
    GROUP_BY("GROUP BY"), 
    IS("IS"),
    LIMIT("LIMIT");
    
    private String info;
    
    private Comando(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return this.info;
    }
    
}
