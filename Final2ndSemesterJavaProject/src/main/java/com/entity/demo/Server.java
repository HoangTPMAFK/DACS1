package com.entity.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ArrayList<ConnectionHandler> connections;
    private String onlineUsers;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public Server () {
        connections = new ArrayList<>();
        onlineUsers = ""; 
    }

    public void execute() {
        try {
            serverSocket = new ServerSocket(1917);
            pool = Executors.newCachedThreadPool();
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(socket);
                pool.execute(handler);
                connections.add(handler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }
    public void shutdown() {
        try {
            if (!serverSocket.isClosed())
                serverSocket.close();
            pool.shutdown();
            for (ConnectionHandler handler : connections) {
                handler.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void broadcast(String message) {
        for (ConnectionHandler handler : connections) {
            if (handler != null) {
                if (handler.isInGroupChat())
                    handler.sendMessage(message);
            }
        }
    }
    public void directMessage(ConnectionHandler conn1, ConnectionHandler conn2, String msg) {
        if (conn1 != null) {
            if (!conn1.isInGroupChat())
                conn1.sendMessage(msg);
        }
        if (conn2 != null) {
            if (!conn2.isInGroupChat())
                conn2.sendMessage(msg);
        }
    }

    public class ConnectionHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private boolean inGroupChat;
        public ConnectionHandler (Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                username = in.readLine();
                System.out.println(username + " connected");
                broadcast("Server: " + username + " joined the chat!");
                DBUtil.savePublicMessage("Server: " + username + " joined the chat!");
                inGroupChat = true;
                onlineUsers = username + ", " + onlineUsers;
                System.out.println(onlineUsers);
                broadcast(onlineUsers);
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("::exit")) {
                        broadcast("Server: "+ username + " left the chat!");
                        DBUtil.savePublicMessage("Server: "+ username + " left the chat!");
                        System.out.println(username + " disconnected");
                        while (onlineUsers.indexOf(username + ", ") != -1) {
                            if (onlineUsers.split(username + ", ").length == 0) onlineUsers = "";
                            else if (onlineUsers.split(username + ", ").length == 1) {
                                onlineUsers = onlineUsers.split(username + ", ")[0];
                            } else if (onlineUsers.split(username + ", ").length > 1) {
                                onlineUsers = onlineUsers.split(username + ", ")[0] + onlineUsers.split(username + ", ")[1];
                            }
                        }
                        connections.remove(this);
                        shutdown();
                    } else if (msg.equalsIgnoreCase("::notOnGroupChat")) {
                        inGroupChat = false;
                    } else if (msg.equals("::onGroupChat")) {
                        inGroupChat = true;
                    } else {
                        if (msg.indexOf("#>--->") != -1) {
                            String receiverUsername = msg.substring(msg.indexOf("#>--->") + 6, msg.indexOf(": "));
                            
                            System.out.println(username + " - " + receiverUsername);
                            ConnectionHandler conn1 = this;
                            ConnectionHandler conn2 = null;
                            for (ConnectionHandler ch : connections) {
                                if (ch.getUsername().equals(receiverUsername)) {
                                    conn2 = ch;
                                    break;
                                }
                            }
                            System.out.println(msg.split(": ")[1]);
                            DBUtil.savePrivateMessage(username, receiverUsername, msg.split(": ")[1]);
                            directMessage(conn1, conn2, username + ": " +  msg.split(": ")[1]);
                            System.out.println(username + ": " + msg);
                        }
                        else {
                            broadcast(username + ": " + msg);
                            DBUtil.savePublicMessage(username + ": " + msg);
                        }
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
        public String getUsername() {
            return this.username;
        }
        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendMessage(String message) {
            out.println(message);
        }
        public boolean isInGroupChat() {
            return inGroupChat;
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.execute();
    }
}
