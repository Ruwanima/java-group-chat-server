import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;

/**
 * ChatClientGUI provides the graphical user interface for the chat client.
 * Built with Java Swing, it provides a user-friendly interface for chatting.
 * Implements ChatClient.MessageListener to receive real-time message updates.
 */
public class ChatClientGUI extends JFrame implements ChatClient.MessageListener {
    // GUI Components
    private JTextArea chatArea;
    private JTextArea notificationArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton connectButton;
    private JTextField usernameField;
    private JLabel statusLabel;
    
    // Client logic
    private ChatClient client;
    private boolean isConnected = false;
    
    /**
     * Constructor initializes the GUI
     */
    public ChatClientGUI() {
        initializeUI();
    }
    
    /**
     * Initializes all UI components and layouts
     */
    private void initializeUI() {
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel - Connection controls
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - Chat display and notifications
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Message input
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });
        
        setVisible(true);
    }
    
    /**
     * Creates the top panel with username input and connect button
     * @return JPanel containing connection controls
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        
        // Username input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        connectButton = new JButton("Connect");
        statusLabel = new JLabel("Not connected");
        statusLabel.setForeground(Color.RED);
        
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(connectButton);
        inputPanel.add(statusLabel);
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Connect button action
        connectButton.addActionListener(e -> handleConnect());
        
        // Enter key in username field triggers connect
        usernameField.addActionListener(e -> handleConnect());
        
        return topPanel;
    }
    
    /**
     * Creates the center panel with chat area and notification area
     * @return JPanel containing the chat display
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Chat display area
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat Messages"));
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Auto-scroll to bottom
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        
        // Notification area
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createTitledBorder("Notifications"));
        
        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
        notificationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        notificationArea.setForeground(new Color(0, 100, 0));
        
        // Auto-scroll to bottom
        DefaultCaret notifCaret = (DefaultCaret) notificationArea.getCaret();
        notifCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationPanel.add(notificationScrollPane, BorderLayout.CENTER);
        
        centerPanel.add(chatPanel);
        centerPanel.add(notificationPanel);
        
        return centerPanel;
    }
    
    /**
     * Creates the bottom panel with message input and send button
     * @return JPanel containing message input controls
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Send Message"));
        
        messageField = new JTextField();
        messageField.setEnabled(false);
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.setPreferredSize(new Dimension(80, 30));
        
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        
        // Send button action
        sendButton.addActionListener(e -> handleSendMessage());
        
        // Enter key in message field sends message
        messageField.addActionListener(e -> handleSendMessage());
        
        return bottomPanel;
    }
    
    /**
     * Handles the connect button action
     */
    private void handleConnect() {
        if (!isConnected) {
            String username = usernameField.getText().trim();
            
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a username", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create client and set listener
            client = new ChatClient(username);
            client.setMessageListener(this);
            
            // Attempt to connect
            if (client.connect()) {
                isConnected = true;
                updateConnectionUI(true);
                addNotification("Connected to server as '" + username + "'");
            }
        } else {
            disconnect();
        }
    }
    
    /**
     * Handles sending a message
     */
    private void handleSendMessage() {
        if (isConnected && client != null) {
            String message = messageField.getText().trim();
            
            if (!message.isEmpty()) {
                client.sendChatMessage(message);
                messageField.setText("");
                messageField.requestFocus();
            }
        }
    }
    
    /**
     * Disconnects from the server
     */
    private void disconnect() {
        if (isConnected && client != null) {
            client.disconnect();
            isConnected = false;
            updateConnectionUI(false);
            addNotification("Disconnected from server");
        }
    }
    
    /**
     * Updates UI elements based on connection status
     * @param connected true if connected, false otherwise
     */
    private void updateConnectionUI(boolean connected) {
        usernameField.setEnabled(!connected);
        connectButton.setText(connected ? "Disconnect" : "Connect");
        messageField.setEnabled(connected);
        sendButton.setEnabled(connected);
        statusLabel.setText(connected ? "Connected" : "Not connected");
        statusLabel.setForeground(connected ? new Color(0, 150, 0) : Color.RED);
        
        if (connected) {
            messageField.requestFocus();
        } else {
            usernameField.requestFocus();
        }
    }
    
    // ===== MessageListener Interface Implementation =====
    
    /**
     * Called when a message is received from the server
     * Updates the GUI on the Event Dispatch Thread for thread safety
     * @param message The received message
     */
    @Override
    public void onMessageReceived(Message message) {
        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case JOIN:
                case LEAVE:
                    // Display join/leave notifications in notification area
                    addNotification(message.getFormattedMessage());
                    break;
                    
                case MESSAGE:
                    // Display regular messages in chat area
                    addChatMessage(message.getFormattedMessage());
                    break;
                    
                case ERROR:
                    // Display error messages
                    addNotification("ERROR: " + message.getContent());
                    JOptionPane.showMessageDialog(this, 
                        message.getContent(), 
                        "Server Error", 
                        JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });
    }
    
    /**
     * Called when a connection error occurs
     * @param error The error message
     */
    @Override
    public void onConnectionError(String error) {
        SwingUtilities.invokeLater(() -> {
            addNotification("ERROR: " + error);
            JOptionPane.showMessageDialog(this, 
                error, 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            isConnected = false;
            updateConnectionUI(false);
        });
    }
    
    /**
     * Called when disconnected from the server
     */
    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            if (isConnected) {
                isConnected = false;
                updateConnectionUI(false);
                addNotification("Connection to server lost");
            }
        });
    }
    
    // ===== Helper Methods =====
    
    /**
     * Adds a message to the chat area
     * @param message The message to display
     */
    private void addChatMessage(String message) {
        chatArea.append(message + "\n");
    }
    
    /**
     * Adds a notification to the notification area
     * @param notification The notification to display
     */
    private void addNotification(String notification) {
        notificationArea.append(notification + "\n");
    }
    
    /**
     * Main method to launch the chat client GUI
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system LAF fails
        }
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new ChatClientGUI());
    }
}
