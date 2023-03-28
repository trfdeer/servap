package me.trfdeer;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class Startup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);

        Dotenv.configure().systemProperties().load();

        String dbDriver = System.getProperty("DB_DRIVER");

        try {
            Class.forName(dbDriver);
            System.out.println("Loaded JDBC Driver " + dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load jdbc driver " + dbDriver + ": " + e.getMessage());
        }
    }

}
