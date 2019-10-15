package jp.co.syslinks.ssh.base;

import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public abstract class ExecBase extends SSHBase {

    protected Session session = null;
    protected ChannelExec channelExec = null;

    protected InputStream in = null;
    protected final int buffSize = 1024 * 4;
    protected final byte[] tmp = new byte[buffSize];

    public ExecBase(ShellAppender appender) {
        super(appender);
    }

    @Override
    public boolean connect() throws Exception {
        return this.connect(5_000);
    }

    abstract public String getCommand();

    @Override
    public boolean connect(int timeout) throws Exception {
        session = super.getSession(timeout);

        channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setPty(true);
        channelExec.setPtyType("vt101", 160, 160, 160, 160); // 色のコードを無くす
        channelExec.setEnv("LANG", "ja_JP.UTF-8");
        // channelShell.setEnv("JAVA_HOME", "/java");

        channelExec.setCommand(getCommand());
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);

        in = channelExec.getInputStream();
        channelExec.connect(timeout);

        // pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(channelShell.getOutputStream(), UTF_8)));

        return true;
    }

    @Override
    public void exec(final Queue<CMD> cmdList) throws Exception {
        while (true) {
            while (in.available() > 0) {
                int count = in.read(tmp, 0, buffSize);
                if (count < 0) {
                    break;
                }
                appender.append(new String(tmp, 0, count));
            }
            if (channelExec.isClosed()) {
                if (in.available() > 0)
                    continue;
                appender.appendln("exit-status: " + channelExec.getExitStatus());
                break;
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
    }

    @Override
    public void exit() {
        super.close(in);
        //close(pw);
        super.close(channelExec);
        super.close(session);
        super.exit();
    }

}
