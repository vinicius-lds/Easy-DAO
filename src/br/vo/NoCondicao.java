package br.vo;

import br.enumeradores.Comando;

/**
 * @author Vinícius Luis da Silva
 */
public class NoCondicao {
    
    private final int inicio;
    private final Comando comando;

    public NoCondicao(int inicio, Comando comando) {
        this.inicio = inicio;
        this.comando = comando;
    }

    public int getInicio() {
        return inicio;
    }

    public Comando getComando() {
        return comando;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NoCondicao other = (NoCondicao) obj;
        if (this.comando != other.comando) {
            return false;
        }
        return true;
    }
    
}
