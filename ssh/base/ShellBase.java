package jp.co.syslinks.ssh.base;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public abstract class ShellBase extends SSHBase {

    abstract public String greeting(); // デフォルトのログインPS1

    protected final Charset UTF_8 = Charset.forName("UTF-8");
    protected final Pattern pattern1 = Pattern.compile("\\[[ 0-9]+\\]$", Pattern.MULTILINE); // [0] or [0 1] or [1 2 3]
    protected final Pattern pattern2 = Pattern.compile("\\[[ 0]+\\]$", Pattern.MULTILINE); // [0] or [0 0] or [0 0 0]

    protected JSch jsch = null;
    protected Session session = null;
    protected ChannelShell channelShell = null;

    protected InputStream in = null;
    protected PrintWriter pw = null;

    protected final int buffSize = 4096;
    protected final byte[] tmp = new byte[buffSize];
    // protected final Queue<String> cmdList = new ArrayDeque<String>();

    public ShellBase(ShellAppender appender) {
        super(appender);
    }

    @Override
    public boolean connect() throws Exception {
        return this.connect(5_000);
    }

    @Override
    public boolean connect(int timeout) throws Exception {
        session = super.getSession(timeout);
        channelShell = (ChannelShell) session.openChannel("shell");
        channelShell.setPty(true);
        channelShell.setPtyType("vt101", 160, 160, 160, 160); // 色のコードを無くす
        channelShell.setEnv("LANG", "ja_JP.UTF-8");
        // channelShell.setEnv("JAVA_HOME", "/java");
        channelShell.connect(timeout);

        in = channelShell.getInputStream();
        pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(channelShell.getOutputStream(), UTF_8)));

        return true;
    }

    @Override
    public void exec(final Queue<CMD> cmdList) throws Exception {
        String result = null;
        StringBuilder output = new StringBuilder();
        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);
            while (in.available() > 0) {
                int count = in.read(tmp, 0, buffSize);
                if (count < 0) {
                    break;
                }
                output.append(new String(tmp, 0, count, UTF_8));
            }
            this.appender.append(output.toString());
            // System.out.print(output);
            if (channelShell.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                // System.out.println("exit-status: " + channelShell.getExitStatus());
                this.appender.appendln("exit-status: " + channelShell.getExitStatus());
                break;
            }
            if (output.length() > 0) {
                String lastLine = this.getLast(output.toString().split("\r\n"));
                if (lastLine.endsWith(greeting())) {
                    pw.println("export HISTCONTROL=ignoreboth && export PS1='[${PIPESTATUS[@]}]'");
                    pw.flush();
                    output.setLength(0);
                    continue;
                }
                Matcher matcher1 = pattern1.matcher(lastLine);
                if (!matcher1.find()) {
                    // コマンド実行中
                    output.setLength(0);
                    continue;
                }
                Matcher matcher2 = pattern2.matcher(lastLine);
                if (!matcher2.find()) { // [0] or [0...0] じゃない場合
                    if (result != null && lastLine.endsWith(result)) {
                        // 予想内エラーコード
                        appender.appendln("予想内エラーコード：" + result + "。処理続行");
                    } else {
                        System.err.println("result error:" + lastLine);
                        pw.println("exit");
                        pw.flush();
                        output.setLength(0);
                        continue;
                    }
                }
                {
                    if (cmdList.size() > 0) {
                        CMD cmd = cmdList.poll();
                        result = cmd.result;
                        pw.println(cmd.cmd);
                        pw.flush();
                        output.setLength(0);
                        continue;
                    } else {
                        result = null;
                        pw.println("exit");
                        pw.flush();
                        output.setLength(0);
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public void exit() {
        super.close(in);
        super.close(pw);
        super.close(channelShell);
        super.close(session);
        super.exit();
    }

    private String getLast(String[] arr) {
        // TODO java.lang.ArrayIndexOutOfBoundsException: -1
        if (arr.length <= 0) {
            return "";
        }
        return arr[arr.length - 1];
    }

}
