package jp.co.syslinks.ssh.appender;

import javax.swing.JTextArea;
import javax.swing.text.Document;

import jp.co.syslinks.ssh.base.ShellAppender;

public class JTextAreaAppender implements ShellAppender {

    private final JTextArea textArea;

    public JTextAreaAppender(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void append(String str) throws Exception {
        textArea.append(str);
        Document doc = textArea.getDocument();
        textArea.setCaretPosition(doc.getLength());
    }

    @Override
    public void appendln(String str) throws Exception {
        textArea.append(str);
        textArea.append("\n");
        Document doc = textArea.getDocument();
        textArea.setCaretPosition(doc.getLength());
    }

}
