package jp.co.syslinks.ssh.base;

public interface ShellAppender {

    public void append(String str) throws Exception;

    public void appendln(String str) throws Exception;

}
