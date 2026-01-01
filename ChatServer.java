import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ChatServer manages the main server logic for the multi-client chat system.
 * Handles client connections, message broadcasting, and connection management.
 * Uses thread-safe collections and concurrent primitives for multi-threaded access.
 */
public class ChatServer {
    private static final int PORT = 5555;
    private static final int MAX_CLIENTS = 50;
    
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> clients;
    private ExecutorService threadPool;
    private volatile boolean running;
    
    /**
     * Constructor initializes the server
     */
    public ChatServer() {
        clients = new ConcurrentHashMap<>();
        threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        running = false;
    }
    
    /**
     * Starts the server and begins listening for client connections
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            
            System.out.println("=================================");
            System.out.println("  Chat Server Started");
            System.out.println("  Listening on port: " + PORT);
            System.out.println("=================================");
            
            // Main server loop - accepts client connections
            while (running) {
                try {
                    // Wait for client connection
                    Socket clientSocket = serverSocket.accept();
                    
                    System.out.println("New connection from: " + 
                                     clientSocket.getInetAddress().getHostAddress());
                    
                    // Create a new ClientHandler for this connection
                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    
                    // Execute handler in thread pool
                    threadPool.execute(handler);
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Adds a client to the active clients map (thread-safe)
     * @param username The client's username
     * @param handler The ClientHandler for this client
     */
    public void addClient(String username, ClientHandler handler) {
        // Check if username already exists
        if (clients.containsKey(username)) {
            // Send error message to the client
            Message errorMsg = new Message(Message.MessageType.ERROR, 
                                          "Server", 
                                          "Username '" + username + "' is already taken");
            handler.sendMessage(errorMsg);
            handler.stop();
            return;
        }
        
        clients.put(username, handler);
        System.out.println("Active clients: " + clients.size());
    }
    
    /**
     * Removes a client from the active clients map (thread-safe)
     * @param username The username of the client to remove
     */
    public void removeClient(String username) {
        if (username != null) {
            clients.remove(username);
            System.out.println("Active clients: " + clients.size());
        }
    }
    
    /**
     * Broadcasts a message to all connected clients
     * Thread-safe method using concurrent collection iteration
     * @param message The message to broadcast
     */
    public void broadcastMessage(Message message) {
        // ConcurrentHashMap allows safe iteration while other threads modify
        for (ClientHandler handler : clients.values()) {
            handler.sendMessage(message);
        }
    }
    
    /**
     * Gets the count of currently connected clients
     * @return Number of active clients
     */
    public int getClientCount() {
        return clients.size();
    }
    
    /**
     * Stops the server and closes all connections
     */
    public void stop() {
        running = false;
        
        System.out.println("\nShutting down server...");
        
        // Send disconnect message to all clients and close connections
        for (ClientHandler handler : clients.values()) {
            handler.stop();
        }
        
        clients.clear();
        
        // Shutdown thread pool
        threadPool.shutdown();
        
        // Close server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        System.out.println("Server stopped.");
    }
    
    /**
     * Main method to start the chat server
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown signal received...");
            server.stop();
        }));
        
        // Start the server
        server.start();
    }
}
