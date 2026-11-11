package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clientehandler
 * 
 * Cada cliente conectado tendrá una instancia
 * de esta clase ejecutandose en un hilo propio.
 * 
 * Se encarga de:
 * 
 * -Registra usuarios.
 * -Escucha mensajes.
 * -Procesar mensajes públicos y privados.
 * -Gestiona desconexiones.
 * 
 * @author frikz
 */
public class ClienteHandler implements Runnable {
    
    /**
     * Socket asociado al cliente.
     */
    private Socket socket;
    
    /**
     * Referencia al servidor.
     */
    private ChatServer servidor;
    
    /**
     * Flujo de entrada
     */
    private BufferedReader entrada;
    
    /**
     * Flujo de salida.
     */
    private PrintWriter salida;
    
    /**
     * Nombre del usuario.
     */
    private String nombreUsuario;
    
    /**
     * Constructor.
     * 
     * @param socket socket del cliente.
     * @param servidor servidor principal.
     */
    public ClienteHandler(Socket socket, ChatServer servidor) {
        
        this.socket = socket;
        this.servidor = servidor;
    }
    
    /**
     * Método principal de hilo.
     */
    @Override
    public void run() {
        try {
            
            entrada = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
             
            salida = new PrintWriter(
                    socket.getOutputStream(), 
                    true);
             
            registrarUsuario();
             
            escucharMensajes();
         
        } catch (IOException e) {
            
            System.out.println(
                    "Error de comunicación. ");
        } finally {
            cerrarConexion();
        }
    }
    
    /**
     * Solicita y registra un usuario.
     */
    private void registrarUsuario() throws IOException {
        
        while (true) {
            salida.println(
                    "INGRESA_USUARIO");
            String nombre = 
                    entrada.readLine();
            
            if (nombre == null) {
                throw new IOException();
            }
            
            boolean registrado = servidor.registrarUsuario(nombre, this);
            
            if (registrado) {
                nombreUsuario = nombre;
                
                salida.println("REGISTRO_EXITOSO");
                
                servidor.enviarATodos( mensajeSistema(
                                nombre 
                                + "Se ha unido al chat."));
                
                break;
                
            } else {
                
                salida.println(
                        "USUARIO_EXISTENTE");
            }
        }
    }
    
    /**
     * Escucha mensajes constantemente.
     */
    private void escucharMensajes() throws IOException {
        
        String mensaje;
        
        while ((mensaje = 
                entrada.readLine()) != null) {
            
            if (mensaje.startsWith("/privado")) {
                procesarPrivado(mensaje);
            } else {
                procesarPublico(mensaje);
            }
        }
    }
    
    /**
     * Procesa mensajes grupales.
     * 
     * @parem mensaje contenido.
     */
    private void procesarPublico(String mensaje) {
        
        String mensajeFinal = 
                "["
                + FechaUtil.obtenerFechaHora()
                + "]"
                + nombreUsuario
                + ": "
                + mensaje;
        
        servidor.enviarATodos(mensajeFinal);
    }
    
    /**
     * Procesa mensajes privados.
     * 
     * Formato:
     * /privado usuario mensaje
     * 
     * @param mensaje mensaje recibido. 
     */
    private void procesarPrivado(String mensaje) {
        
        try {
            String[] partes = mensaje.split("", 3);
            
            String destino = partes[1];
            
            String contenido = partes[2];
            
            String mensajePrivado = 
                    "["
                    + FechaUtil.obtenerFechaHora()
                    + "] [PRIVADO] "
                    + nombreUsuario
                    + ": "
                    + contenido;
            
            servidor.enviarPrivado(destino, mensajePrivado);

        } catch (Exception e) {
            
            salida.println("Formato válido:");
            salida.println("/private usuario mensaje");
            
        }
    }
    
    /**
     * Envíar un mensaje al cliente.
     * 
     * @param mensaje mensaje enviado.
     */
    public void enviarMensaje(String mensaje) {
        salida.println(mensaje);
    }
    
    /**
     * Genera mensajes informativos del sistema.
     * 
     * @param texto contenido.
     * @return mensaje formateado.
     */
    private String mensajeSistema(String texto) {
        
        return "["
                + FechaUtil.obtenerFechaHora()
                + "] SISTEMA: "
                + texto;
        
    }
    
    /**
     * Cierra recursos y elimina al usuario.
     */
    public void cerrarConexion() {
        
        try {
            
            if(nombreUsuario != null) {
                servidor.eliminarUsuario(nombreUsuario);
                servidor.enviarATodos(mensajeSistema(
                        nombreUsuario + "abandonó el chat."));
            }
            
            if (entrada != null) {
                entrada.close();
            }
        
            if (salida != null) {
                salida.close();
            }
        
            if (socket != null) {
                socket.close();
            }
        
        } catch (IOException e) {
            
            System.out.println("Error al cerrar conexión");
        }
    }
    
    /**
     * Obtener el nombre del usuario.
     * 
     * @return nombre del usuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
