package config;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class Koneksi {
    private static Connection conn;
    private static String lastError;
    private static boolean driverLoaded = false;
    private static Driver loadedDriver = null;

    public static synchronized Connection getKoneksi() {
        try {
            if (conn != null && !conn.isClosed()) {
                return conn;
            }
        } catch (SQLException ignored) {
            conn = null;
        }

        ensureDriver();

        String[] hosts = new String[] { "localhost", "127.0.0.1" };
        String[] passes = new String[] { "", "root" };
        String[] dbNames = new String[] { "db_pt_canopy", "canopy" };
        String params = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        for (String dbName : dbNames) {
            for (String host : hosts) {
                for (String pass : passes) {
                    String url = "jdbc:mysql://" + host + ":3306/" + dbName + params;
                    try {
                        if (loadedDriver != null) {
                            Properties props = new Properties();
                            props.put("user", "root");
                            props.put("password", pass);
                            conn = loadedDriver.connect(url, props);
                        }
                        if (conn == null) {
                            conn = DriverManager.getConnection(url, "root", pass);
                        }
                        if (conn != null && !conn.isClosed()) {
                            lastError = null;
                            return conn;
                        }
                    } catch (Exception e) {
                        lastError = e.getMessage() != null ? e.getMessage() : e.toString();
                    }
                }
            }
        }

        System.err.println("Gagal koneksi database: " + lastError);
        return null;
    }

    public static Connection getConnection() {
        return getKoneksi();
    }

    public static Connection configDB() throws SQLException {
        Connection c = getKoneksi();
        if (c == null) {
            throw new SQLException("Koneksi database gagal: " + lastError);
        }
        return c;
    }

    public static String getLastError() {
        return lastError;
    }

    private static synchronized void ensureDriver() {
        if (driverLoaded) {
            return;
        }
        try {
            Class<?> c = Class.forName("com.mysql.cj.jdbc.Driver");
            loadedDriver = (Driver) c.getDeclaredConstructor().newInstance();
            driverLoaded = true;
            return;
        } catch (Throwable ignored) {}

        try {
            Class<?> c = Class.forName("com.mysql.jdbc.Driver");
            loadedDriver = (Driver) c.getDeclaredConstructor().newInstance();
            driverLoaded = true;
            return;
        } catch (Throwable ignored) {}

        List<File> searchDirs = new ArrayList<>();
        try {
            File cur = new File(Koneksi.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            for (int i = 0; i < 6 && cur != null; i++) {
                searchDirs.add(cur);
                searchDirs.add(new File(cur, "lib"));
                cur = cur.getParentFile();
            }
        } catch (Throwable ignored) {}

        String userDir = System.getProperty("user.dir");
        if (userDir != null) {
            searchDirs.add(new File(userDir));
            searchDirs.add(new File(userDir, "lib"));
            searchDirs.add(new File(userDir, "CanopyONE/lib"));
            searchDirs.add(new File(userDir, "Documents/AG-project/CanopyONE/lib"));
        }
        searchDirs.add(new File("lib"));
        searchDirs.add(new File("../lib"));
        searchDirs.add(new File("../../lib"));
        searchDirs.add(new File("C:/Users/Fareru/Documents/AG-project/CanopyONE/lib"));

        File jarFile = null;
        for (File dir : searchDirs) {
            if (dir != null && dir.exists()) {
                if (dir.isFile() && dir.getName().toLowerCase().endsWith(".jar") && dir.getName().toLowerCase().contains("mysql")) {
                    jarFile = dir;
                    break;
                }
                if (dir.isDirectory()) {
                    File[] jars = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar") && name.toLowerCase().contains("mysql"));
                    if (jars != null && jars.length > 0) {
                        jarFile = jars[0];
                        break;
                    }
                }
            }
        }

        if (jarFile != null && jarFile.exists()) {
            try {
                URLClassLoader ucl = new URLClassLoader(
                    new URL[] { jarFile.toURI().toURL() },
                    Koneksi.class.getClassLoader()
                );
                try {
                    Class<?> c = Class.forName("com.mysql.cj.jdbc.Driver", true, ucl);
                    loadedDriver = (Driver) c.getDeclaredConstructor().newInstance();
                } catch (Throwable t) {
                    Class<?> c = Class.forName("com.mysql.jdbc.Driver", true, ucl);
                    loadedDriver = (Driver) c.getDeclaredConstructor().newInstance();
                }
                if (loadedDriver != null) {
                    DriverManager.registerDriver(new DriverShim(loadedDriver));
                    driverLoaded = true;
                    return;
                }
            } catch (Throwable t) {
                lastError = "Gagal memuat driver dari " + jarFile.getName() + ": " + t.getMessage();
            }
        }

        if (!driverLoaded) {
            lastError = "MySQL Driver tidak ditemukan. Pastikan file mysql-connector-j ada di folder lib/";
        }
    }

    private static class DriverShim implements Driver {
        private final Driver driver;

        DriverShim(Driver d) {
            this.driver = d;
        }

        public boolean acceptsURL(String u) throws SQLException {
            return this.driver.acceptsURL(u);
        }

        public Connection connect(String u, Properties p) throws SQLException {
            return this.driver.connect(u, p);
        }

        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return this.driver.getPropertyInfo(u, p);
        }

        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return this.driver.getParentLogger();
        }
    }
}
