package jp.co.syslinks.ssh.appender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jp.co.syslinks.ssh.base.ShellAppender;

public class ShellAppenderFile implements ShellAppender {

    private final File file;

    public ShellAppenderFile(File file) {
        this.file = file;
        if (this.file.exists()) {
            this.file.delete();
        }
    }

    @Override
    public void append(String str) throws Exception {
        System.out.print(str);
        this.append(str, this.file);
    }

    @Override
    public void appendln(String str) throws Exception {
        System.out.print(str);
        System.out.println();
        this.append(str, this.file);
    }

    public void append(String sb, final File file) {
        PrintWriter pw = null;
        try {
            //String fileName = "r:/test_out.txt"; // ファイル名
            String charSet = "utf-8"; // 文字コードセット
            int buffer = 2048;
            boolean append = true; // 追加モード
            boolean auto_flush = true; // 自動フラッシュ
            pw = new PrintWriter( //
                    new BufferedWriter( //
                            new OutputStreamWriter( //
                                    new FileOutputStream( //
                                            file, append), //
                                    charSet),
                            buffer), // 省略するとシステム標準
                    auto_flush);
            pw.write(sb);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            // .. 例外処理
        } finally {
            this.close(pw);
        }
    }

    private void close(BufferedReader br) {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close(PrintWriter pw) {
        pw.close();
    }

}
