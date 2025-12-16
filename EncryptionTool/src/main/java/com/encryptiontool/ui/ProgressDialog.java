package com.encryptiontool.ui;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog {
    private JProgressBar progressBar;

    public ProgressDialog(Frame parent, String title, String message) {
        super(parent, title, true);

        setLayout(new BorderLayout(10, 10));
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // 消息标签
        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel, BorderLayout.NORTH);

        // 进度条
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        add(progressBar, BorderLayout.CENTER);

        // 提示标签
        JLabel hintLabel = new JLabel("请稍候，这可能需要几秒钟...");
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hintLabel.setFont(new Font(hintLabel.getFont().getName(), Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        add(hintLabel, BorderLayout.SOUTH);
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
        });
    }
}