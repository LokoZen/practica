/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * ChatServer 
 * 
 * Escucha conexiones.
 * Registra usuarios.
 * Controla el límite de clientes.
 * Mantener lista de clientes.
 * Envia mensajes grupales.
 * Envia mensajes privados.
 * Gestiona desconexiones.
 * 
 * @author frikz
 */
public class ChatServer {
    
    /**
     * Puerto donde escuchára el servidor
     */
    private static final int PUERTO = 5000;
    /**
     * Número máximo de clientes permitidos.
     */
    private static final int MAX_CLIENTES = 10;
    
    /**
     * Almacena los usuarios conectados.
     * 
     * Clave : Nombre del usuario.
     * Valor : ClienteHandler asociado.
     */
    private Map<String, ClienteHandler> clientes;
    
    /**
     * Constructor
     */
    public ChatServer() {
        clientes = new HashMap<>();
    }
    
    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Server iniciado en puerto " + PUERTO);
            
            while (true) {
                Socket socket = serverSocket.accept();
                
                // Verificar límite
                if (clientes.size() >= MAX_CLIENTES) {
                    System.out.println("Servidor lleno.");
                    
                    socket.close();
                    continue;
                }
                
                ClienteHandler cliente =
                        new ClienteHandler(socket, this);
                
                new Thread(cliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        ChatServer servidor = new ChatServer();
        servidor.iniciar();
    }
    
}

