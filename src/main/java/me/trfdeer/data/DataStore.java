package me.trfdeer.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

import me.trfdeer.model.Source;

public class DataStore {
    private Connection conn;

    public DataStore() throws SQLException {
        String connectionString = System.getProperty("CONNECTION_STRING");
        String username = System.getProperty("DB_USER");
        String password = System.getProperty("DB_PASSWORD");

        this.conn = DriverManager.getConnection(connectionString, username, password);
        this.conn.setAutoCommit(false);
    }

    public Connection getConnection() {
        return this.conn;
    }

    public int addSource(String folder, String title, String url, String faviconUrl) {
        // TODO: Custom message if item already exists?
        try {

            PreparedStatement st = this.conn.prepareStatement(
                    "INSERT INTO sources (title, url, favicon_url, folder, date_added, date_updated) VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            long currentEpoch = Instant.now().getEpochSecond();
            st.setString(1, title);
            st.setString(2, url);
            st.setString(3, faviconUrl);
            st.setString(4, folder);
            st.setLong(5, currentEpoch);
            st.setLong(6, currentEpoch);

            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int insertedId = rs.getInt(1);

            this.conn.commit();
            return insertedId;

        } catch (SQLException e) {
            System.out.println("Failed to add source: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackErr) {
                System.err.println("Failed to rollback transaction: " + rollbackErr.getMessage());
            }
            return -1;
        }
    }

    public ArrayList<String> getAllDirectories() {
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT folder FROM sources ORDER BY folder ASC");

            ArrayList<String> folders = new ArrayList<>();

            while (rs.next()) {
                String name = rs.getString(1);
                folders.add(name);
            }

            return folders;

        } catch (Exception err) {
            System.err.println("Failed to retrieve folders: " + err.getMessage());
            return null;
        }
    }

    public ArrayList<Source> getAllSources() {
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st
                    .executeQuery("SELECT id, title, url, favicon_url, folder FROM sources ORDER BY title ASC");

            ArrayList<Source> sources = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String url = rs.getString(3);
                String faviconUrl = rs.getString(4);
                String folder = rs.getString(5);

                Source src = new Source(id, title, url, faviconUrl, folder);
                sources.add(src);
            }

            return sources;

        } catch (Exception err) {
            System.err.println("Failed to retrieve sources: " + err.getMessage());
            return null;
        }
    }

    public int updateSource(int id, String newTitle, String newUrl, String newFaviconUrl, String newFolder) {
        try {
            PreparedStatement st = this.conn.prepareStatement(
                    "UPDATE sources SET title = ?, url = ?, favicon_url = ?, date_updated = ?, folder = ? WHERE id = ?");

            st.setString(1, newTitle);
            st.setString(2, newUrl);
            st.setString(3, newFaviconUrl);
            st.setLong(4, Instant.now().getEpochSecond());
            st.setString(5, newFolder);
            st.setInt(6, id);

            st.execute();
            this.conn.commit();
            return id;

        } catch (SQLException e) {
            System.out.println("Failed to delete source: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackErr) {
                System.err.println("Failed to rollback transaction: " + rollbackErr.getMessage());
            }
            return -1;
        }
    }

    public boolean deleteSource(int id) {
        try {
            PreparedStatement st = this.conn.prepareStatement("DELETE FROM sources WHERE id = ?");
            st.setInt(1, id);

            st.execute();
            this.conn.commit();
        } catch (SQLException e) {
            System.out.println("Failed to delete source: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackErr) {
                System.err.println("Failed to rollback transaction: " + rollbackErr.getMessage());
            }
        }
        return false;
    }

    @Override
    protected void finalize() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            System.err.println("Failed to close connection: " + e.getMessage());
        }
    }
}
