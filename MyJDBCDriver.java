package demo.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

public class MyJDBCDriver implements java.sql.Driver {

    private Driver lastUnderlyingDriverRequested;

    static final public void registerDriver() {
        try {
            DriverManager.registerDriver(new MyJDBCDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        System.out.println("connect");
        if (!url.startsWith("my:")) {
            return null;
        }
        url = url.substring("my:".length());
        Driver d = getRealDriver(url);
        if (d == null) {
            return null;
        }
        lastUnderlyingDriverRequested = d;
        Connection c = d.connect(url, info);
        if (c == null) {
            throw new SQLException("invalid or unknown driver url: " + url);
        }
        return new MyConnection(c);
    }

    private Driver getRealDriver(String url) throws SQLException {
        Enumeration<Driver> e = DriverManager.getDrivers();
        Driver d;
        while (e.hasMoreElements()) {
            d = e.nextElement();
            if (d.acceptsURL(url)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url.startsWith("my:")) {
            return true;
        }
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        System.out.println("getPropertyInfo");
        return null;
    }

    @Override
    public int getMajorVersion() {
        System.out.println("getMajorVersion");
        if (lastUnderlyingDriverRequested == null) {
            return 0;
        }
        return lastUnderlyingDriverRequested.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        System.out.println("getMajorVersion");
        if (lastUnderlyingDriverRequested == null) {
            return 0;
        }
        return lastUnderlyingDriverRequested.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        System.out.println("jdbcCompliant");
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        System.out.println("getParentLogger");
        return null;
    }

}
