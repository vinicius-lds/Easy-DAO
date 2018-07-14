package br.util;

import java.sql.Date;

/**
 * @author Vinícius Luis da Silva
 */
public abstract class StringUtil {
    
    /**
     * @return A prórpia String para como parametro entre Aspas Duplas ("")
     */
    public static String setAspas(String str) {
        return "\"" + str + "\"";
    }
    
    /**
     * @param data Uma String no formato 01/01/1900
     * @return A String passada como paramentro no formato aceito pelo banco de dados MySQL (1900-01-01)
     */
    public static String padronizarData(String data) {
        return data.substring(6) + "-" + data.substring(3, 5) + "-" + data.substring(0, 2);
    }
    
    /**
     * Tranforma o objeto date passado como paramentro em uma String
     * @param date O Date que será transformado
     * @return Uma String no formato 01/01/1900
     */
    public static String dateToString(Date date) {
        return (((date.getDate()) < 10) ? "0" + date.getDate() : date.getDate() + "") 
                + (((date.getMonth() + 1) < 10) ? "0" + (date.getMonth() + 1): (date.getMonth() + 1) + "")
                + (date.getYear() + 1900);
    }
    
}
