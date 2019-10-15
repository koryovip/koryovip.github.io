package jp.co.syslinks.ssh.base;

public abstract class ExecPABase extends ExecBase {

    public ExecPABase(ShellAppender appender) {
        super(appender);
    }

    @Override
    public String user() {
        return "root";
    }

    @Override
    public String pass() {
        return "growtech-00";
    }

}
