package jp.co.syslinks.ssh.base;

import java.io.InputStream;
import java.io.PrintWriter;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public abstract class SSHBase implements ShellIF {

    protected JSch jsch = null;

    protected int port() {
        return 22;
    }

    abstract public String user();

    abstract public String pass();

    protected final ShellAppender appender;

    public SSHBase(ShellAppender appender) {
        this.appender = appender;
    }

    protected Session getSession(int timeout) throws Exception {
        jsch = new JSch();
        Session session = jsch.getSession(user(), host(), port());
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(pass());
        session.connect(timeout);
        return session;
    }

    public void exit() {
        jsch = null;
    }

    protected void close(PrintWriter pw) {
        if (pw != null) {
            try {
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void close(Channel channel) {
        if (channel != null) {
            try {
                channel.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void close(Session session) {
        if (session != null) {
            try {
                session.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
