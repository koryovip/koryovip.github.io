package jp.co.syslinks.ssh.base;

public class CMD {

    final public String cmd;
    final public String result;

    public CMD(String cmd) {
        this(cmd, null);
    }

    public CMD(String cmd, String result) {
        this.cmd = cmd;
        this.result = result;
    }

    public static final CMD $(String cmd) {
        return $(false, cmd);
    }

    public static final CMD $(boolean ヒストリに残るフラグ, String cmd) {
        if (ヒストリに残るフラグ) {
            return new CMD(cmd);
        }
        return new CMD(" " + cmd);
    }

    public static final CMD $(String cmd, String result) {
        return $(false, cmd, result);
    }

    public static final CMD $(boolean ヒストリに残るフラグ, String cmd, String result) {
        if (ヒストリに残るフラグ) {
            return new CMD(cmd, result);
        }
        return new CMD(" " + cmd, result);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CMD [cmd=").append(cmd).append(", result=").append(result).append("]");
        return builder.toString();
    }

}
