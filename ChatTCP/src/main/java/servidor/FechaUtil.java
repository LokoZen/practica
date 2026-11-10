/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para obteer la fecha y hora actual
 * con el formato utilizado por el chat.
 * 
 * @author frikz
 */
public class FechaUtil {
    
    /**
     * Formato de decha y hora.
     */
    private static final DateTimeFormatter FORMATO = 
            DateTimeFormatter.ofPattern(
                    "dd/MM/yy HH:mm:ss");
    
    /**
     * Contructor privado para evitar instancias.
     */
    private FechaUtil() {     
    }
    
    /**
     * Obtener la fechan y la hora actual formateada.
     * 
     * @return fecha y hora.
     */
    public static String obtenerFechaHora() {
        return LocalDateTime.now().format(FORMATO);
    }
}
