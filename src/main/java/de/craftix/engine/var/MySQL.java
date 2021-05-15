package de.craftix.engine.var;

import de.craftix.engine.GameEngine;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class MySQL implements Serializable {

    protected String server;
    protected int port;
    protected String database;
    protected String username;
    protected String password;
    protected Connection con;

    public MySQL(String server, int port, String database, String username, String password) {
        this.server = server;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public MySQL(String server, String database, String username, String password) { this(server, 3306, database, username, password); }

    public void connect() {
        if (isConnected()) return;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database, username, password);
        }catch (Exception e) {
            GameEngine.throwError(e);
        }
    }

    public void disconnect() {
        if (!isConnected()) return;
        try {
            con.close();
            con = null;
        }catch (Exception e) { GameEngine.throwError(e); }
    }

    public boolean isConnected() { return con != null; }

    public void insert(String qry) {
        if (!isConnected()) throw new NullPointerException("MySQL not connected");
        try {
            con.prepareStatement(qry).executeUpdate();
        }catch (Exception e) { GameEngine.throwError(e); }
    }

    public ResultSet getData(String qry) {
        if (!isConnected()) throw new NullPointerException("MySQL not connected");
        try {
            return con.prepareStatement(qry).executeQuery();
        }catch (Exception e) { GameEngine.throwError(e); }
        return null;
    }

}
