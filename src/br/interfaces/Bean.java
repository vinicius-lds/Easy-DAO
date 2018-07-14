package br.interfaces;

/**
 * @author Vinícius Luis da Silva
 */
public interface Bean {
    
    /**
     * Esse método deve ser implementado afim de inicializar todos os atributos atraves de um array de objetos contendo os 
     * registros buscados no banco, o tamanho do array será sempre a quantidade de atributos que a classe contem. Portato,
     * caso a consulta não traga algum deles, o conteúdo daquela posição do array será null.
     * @param atributos Array de Objetos contendo registros de um banco de dados
     */
    public void initialize(Object[] atributos);
    
    public Object getPrimaryKey();
    
}
