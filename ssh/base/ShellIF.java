package jp.co.syslinks.ssh.base;

import java.util.Queue;

public interface ShellIF {

    public boolean connect() throws Exception;

    public boolean connect(int timeout) throws Exception;

    public void exec(final Queue<CMD> cmdList) throws Exception;

    public void exit();

    public String host();

    public String hostname();
}
