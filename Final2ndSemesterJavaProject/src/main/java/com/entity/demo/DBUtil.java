package com.entity.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DBUtil {
    private static String dbUrl;
    private static String username;
    private static String password;
    private static Connection conn;
    private static Statement stmt;
    public static void dbConnect() {
        dbUrl = "jdbc:mysql://localhost:3308/ChatApplicationDB";
        username = "root";
        password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connected to database");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void dbDisconnect() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean userExist(String userId, String userPassword) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Accounts WHERE userId = \'" + userId + "\' AND userPassword = \'" + userPassword + "\';");
            System.out.println("Checking if login is valid...");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static boolean addUser(String userId, String userPassword) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Accounts WHERE userId = \'" + userId + "\';");
            System.out.println("Checking if user already exist...");
            if (rs.next()) return false;
            else {
                stmt.executeUpdate("INSERT INTO Accounts VALUES (\'" + userId + "\', \'" + userPassword + "\');");
                return true;
            }
        } catch (SQLException e ) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static boolean changePassword(String userId, String oldUserPassword, String newUserPassword) {
        try {
            stmt = null;
            System.out.println("Checking if user old password is correct...");
            if (userExist(userId, oldUserPassword)) {
                dbConnect();
                stmt = conn.createStatement();
                stmt.execute("UPDATE Accounts SET userPassword = '" + newUserPassword + "' WHERE userId = '" + userId + "';");
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static boolean deleteAccount(String userId, String userPassword) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Accounts WHERE userId = \'" + userId + "\';");
            System.out.println("Checking if user old password is correct...");
            if (rs.next()) {
                stmt.execute("DELETE Accounts WHERE userId = '" + userId + "'");
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void loadMessage(ClientController clientController) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM PublicMessage ORDER BY sendTime ASC;");
            while (rs.next()) {
                String inMessage = rs.getString(1) + ": " + rs.getString(2);
                clientController.displayMessage(inMessage);
                clientController.autoSrollDown();
            }
            clientController.autoSrollDown();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void savePublicMessage(String inMessage) {
        try {
            String[] messageComponent = inMessage.split(": ", 2);
            String inUsername = messageComponent[0];
            String messageContent = messageComponent[1];
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();
            stmt.execute("INSERT INTO PublicMessage VALUES ('" + inUsername + "', '" + messageContent + "', '" + dtf.format(now) + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void loadDirectMessage(ClientController clientController, String receiverId) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement(); // (clientController.getUsername().hashCode() + receiverId.hashCode())
            System.out.println(clientController.getUsername() + " - " + receiverId);
            rs = stmt.executeQuery("SELECT * FROM DirectMessage WHERE contactId = '" + ((int) Math.abs(clientController.getUsername().hashCode() + receiverId.hashCode())) + "' ORDER BY sendTime ASC;");
            while (rs.next()) {
                String inMessage = rs.getString(2) + ": " + rs.getString(4);
                clientController.displayMessage(inMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void savePrivateMessage(String sender, String reiceiver, String inMessage) {
        try {
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();
            stmt.execute("INSERT INTO DirectMessage VALUES ('" + ((int) Math.abs(sender.hashCode() + reiceiver.hashCode())) + "', '" + sender + "', '" + dtf.format(now) + "', '" + inMessage + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void sendFriendRequest(String userId1, String userId2) {
        try {
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            stmt.execute("INSERT INTO FriendRequests VALUES ('" + userId1 + "', '" + userId2 + "', '" + ((int)Math.abs(userId1.hashCode() + userId2.hashCode())) + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void acceptFriendRequest(ClientController clientController, String userId1, String userId2, boolean accept) {
        try {
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            if (accept) {
                stmt.execute("INSERT INTO Contacts (SELECT * FROM FriendRequests WHERE relationId = '" + ((int)Math.abs(userId1.hashCode() + userId2.hashCode())) + "');");
                stmt.execute("DELETE FROM FriendRequests WHERE relationId = '" + ((int)Math.abs(userId1.hashCode() + userId2.hashCode())) + "';");
                clientController.addContactToContainer(userId1); 
            } else {
                stmt.execute("DELETE FROM FriendRequests WHERE relationId = '" + ((int)Math.abs(userId1.hashCode() + userId2.hashCode())) + "';");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void loadFriendRequests(ClientController clientController) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM FriendRequests WHERE userId2 = '" + clientController.getUsername() + "';");
            while (rs.next()) {
                clientController.addFriendRequestToContainer(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
    public static void loadContacts(ClientController clientController) {
        try {
            ResultSet rs = null;
            stmt = null;
            dbConnect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Contacts WHERE userId1 = '" + clientController.getUsername() + "' OR userId2 = '" + clientController.getUsername() + "';");
            while (rs.next()) {
                if (rs.getString(1).equals(clientController.getUsername())) {
                    clientController.addContactToContainer(rs.getString(2));
                } else if (rs.getString(2).equals(clientController.getUsername())) {
                    clientController.addContactToContainer(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbDisconnect();
        }
    }
}
