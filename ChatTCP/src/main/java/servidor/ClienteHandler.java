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
 * se encarga de:
 * 
 * -Escucha conexiones.
 * -Registra usuarios.
 * -Controla el límite de clientes.
 * -Controlar el límite de clientes.
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

    /**
    * Inicia el servidor y queda escuchando conexiones.
    */
    public void iniciarServidor() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            
            System.out.println("====================================");
            System.out.println(" SERVIDOR DE CHAT INICIADO ");
            System.out.println("Puerto " + PUERTO);
            System.out.println("====================================");
            
            while (true) {
                
                // Se crea socket para cliente aceptado
                Socket socket = serverSocket.accept();

                System.out.println(
                    "Nueva conexión desde:"
                    + socket.getInetAddress());
                
                /**
                 * añade uno a las conexiones actuales
                 *  y verifica límite de usuarios.
                 */    
                if (clientes.size() >= MAX_CLIENTES) {
                    
                    System.out.println(
                            "Conexión rechazada: servidor lleno.");
                    
                    socket.close();
                    continue;
                }

                /** 
                * Se crea un hilo para atender al cliente.
                */
                ClienteHandler cliente =
                        new ClienteHandler(socket, this);
                
                new Thread(cliente).start();
            }
        } catch (IOException e) {
            
            System.out.println(
                    "Error al inniciar el servidor.");
            
            e.printStackTrace();
        }
    }

    /**
    * Registra un usuario en el servidor.
    *
    * @param nombreUsuario nombre elegido por el cliente.
    * @param clienteHandler Referencia al cliente.
    * @return true si el nombre es válido y único,
    * false si ya existe.
    */

    public synchronized boolean registrarUsuario(
        String nombreUsuario,
        ClienteHandler clienteHandler) {

        if (clientes.containsKey(nombreUsuario)) {
            return false;
        }

        clientes.put(nombreUsuario, clienteHandler);

        System.out.println(
            "Usuario conectado: " + nombreUsuario);
        return true;
    }

    /**
    * Elimina un usuario cuando se desconecta.
    *
    * @param nombreUsuario usuario desconectado.
    */
    public synchronized void eliminarUsuario(
        String nombreUsuario) {

        clientes.remove(nombreUsuario);

        System.out.println(
            "Usuario desconectado: " 
            + nombreUsuario);
    }

    /**
    * Envía un mensaje a todos los usuarios conectados.
    *
    * @param mensaje Mensaje formateado.
    */
    public synchronized void enviarATodos(
        String mensaje) {

        for (ClienteHandler cliente
             : clientes.values()) {
            
            cliente.enviarMensaje(mensaje);
        }
    }

    /**
    * Envía un mensaje privado a un usuario específico
    *
    * @param destinatario Usuario destino.
    * @param mensaje Mensaje a enviar.
    */
    public synchronized void enviarPrivado(
        String destinatario,
        String mensaje) {

        ClienteHandler cliente =
            clientes.get(destinatario);

        if (cliente != null) {
            cliente.enviarMensaje(mensaje);

        } else {
            System.out.println(
                "Usuario no encontrado: "
                + destinatario);
        }
    }

    /**
    * Verifica si un usuario ya existe.
    *
    * @param usuario Nombre a buscar
    * @return true si existe 
    */
    public synchronized boolean existeUsuario(
        String usuario) {

        return clientes.containsKey(usuario);
    }

    /**
    * Obtiene la cantidad actual de usuarios conectados.
    *
    * @return cantidad de usuarios.
    */
    public synchronized int getCantidadClientes() {
        return clientes.size();
    }

    /**
    * Punto de entrada del servidor.
    *
    * @param args argumentos.
    */
    public static void main(String[] args) {
        ChatServer servidor = new ChatServer();
        servidor.iniciarServidor();
    }
    
}
