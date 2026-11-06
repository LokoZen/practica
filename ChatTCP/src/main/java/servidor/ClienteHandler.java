/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

/**
 * Clientehandler
 * 
 * Escucha mensajes.
 * Procesarlos.
 * Informa al servidor.
 * 
 * @author frikz
 */
public class ClienteHandler {

    public ClienteHandler(Socket socket,
                          ChatServer servidor) {
    }

    @Override
    public void run() {
    
    }

    public void enviarMensaje(String mensaje);
}
