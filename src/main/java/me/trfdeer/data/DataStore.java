package me.trfdeer.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import me.trfdeer.model.Link;
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

            Timestamp timeNow = Timestamp.from(Instant.now());
            st.setString(1, title);
            st.setString(2, url);
            st.setString(3, faviconUrl);
            st.setString(4, folder);
            st.setTimestamp(5, timeNow);
            st.setTimestamp(6, timeNow);

            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int insertedId = rs.getInt(1);

            this.conn.commit();
            st.close();
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

    public List<String> getAllDirectories() {
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DISTINCT folder FROM sources ORDER BY folder ASC");

            List<String> folders = new ArrayList<>();

            while (rs.next()) {
                String name = rs.getString(1);
                folders.add(name);
            }
            st.close();

            return folders;

        } catch (Exception err) {
            System.err.println("Failed to retrieve folders: " + err.getMessage());
            return null;
        }
    }

    public Source getSource(int sourceId) {
        try {
            PreparedStatement st = this.conn.prepareStatement(
                    "SELECT id, title, url, favicon_url, folder FROM sources WHERE id = ? LIMIT 1");

            st.setInt(1, sourceId);
            ResultSet rs = st.executeQuery();
            rs.next();

            int id = rs.getInt(1);
            String title = rs.getString(2);
            String url = rs.getString(3);
            String faviconUrl = rs.getString(4);
            String folder = rs.getString(5);

            Source src = new Source(id, title, url, faviconUrl, folder);

            st.close();

            return src;

        } catch (Exception err) {
            System.err.println("Failed to retrieve sources: " + err.getMessage());
            return null;
        }
    }

    public List<Source> getAllSources() {
        try {
            Statement st = this.conn.createStatement();
            ResultSet rs = st
                    .executeQuery("SELECT id, title, url, favicon_url, folder FROM sources ORDER BY title ASC");

            List<Source> sources = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String url = rs.getString(3);
                String faviconUrl = rs.getString(4);
                String folder = rs.getString(5);

                Source src = new Source(id, title, url, faviconUrl, folder);
                sources.add(src);
            }
            st.close();

            return sources;

        } catch (Exception err) {
            System.err.println("Failed to retrieve sources: " + err.getMessage());
            return null;
        }
    }

    public int updateSource(int id, String newTitle, String newUrl, String newFolder) {
        try {
            PreparedStatement st = this.conn.prepareStatement(
                    "UPDATE sources SET title = ?, url = ?, date_updated = ?, folder = ? WHERE id = ?");

            st.setString(1, newTitle);
            st.setString(2, newUrl);
            st.setTimestamp(3, Timestamp.from(Instant.now()));
            st.setString(4, newFolder);
            st.setInt(5, id);

            st.execute();
            this.conn.commit();
            st.close();
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

    public int deleteSource(int id) {
        try {
            PreparedStatement st = this.conn.prepareStatement("DELETE FROM sources WHERE id = ?");
            st.setInt(1, id);

            int count = st.executeUpdate();
            this.conn.commit();
            st.close();
            return count;
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

    public int addLinks(List<Link> links) {
        try {
            PreparedStatement st = this.conn.prepareStatement(
                    "INSERT IGNORE INTO links (`source_id`, `title`, `link`, `description`, `publish_date`, `author`) VALUES (?, ?, ?, ?, ?, ?)");

            for (Link link : links) {
                st.setInt(1, link.sourceId);
                st.setString(2, link.title);
                st.setString(3, link.link);
                st.setString(4, link.description);
                st.setTimestamp(5, Timestamp.from(link.publishDate));
                st.setString(6, link.author);
                st.addBatch();
            }

            int[] count = st.executeBatch();

            int linksAdded = 0;
            for (int c : count)
                linksAdded += c;

            this.conn.commit();
            st.close();

            return linksAdded;

        } catch (Exception err) {
            System.err.println("Failed to add links: " + err.getMessage());
            return -1;
        }
    }

    public List<Link> getLinks(int sourceId) {
        try {
            PreparedStatement st = this.conn.prepareStatement(
                    "SELECT id, title, link, description, publish_date, author FROM links WHERE source_id=?");

            st.setInt(1, sourceId);
            ResultSet rs = st.executeQuery();

            List<Link> links = new ArrayList<>();

            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String link = rs.getString(3);
                String description = rs.getString(4);
                Instant publishDate = rs.getTimestamp(5).toInstant();
                String author = rs.getString(6);

                links.add(new Link(id, sourceId, title, link, description, author, publishDate));
            }

            return links;

        } catch (Exception err) {
            System.err.println("Failed to add links: " + err.getMessage());
            return null;
        }
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
