package com.encryptiontool.ui;

import com.encryptiontool.util.FontUtil;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private AlgorithmPanel algorithmPanel;
    private ResultPanel resultPanel;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearButton;
    private JLabel statusLabel;

    public MainFrame() {
        System.out.println("创建MainFrame");
        setTitle("加解密算法工具 v2.0 - 安全增强版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));

        initComponents();
        setupLayout();
        setupListeners();

        System.out.println("MainFrame初始化完成");
    }

    private void initComponents() {
        FontUtil.setUIFont();
        algorithmPanel = new AlgorithmPanel();
        resultPanel = new ResultPanel();

        encryptButton = createButton("加密", new Color(76, 175, 80));
        decryptButton = createButton("解密", new Color(33, 150, 243));
        clearButton = createButton("清空", new Color(244, 67, 54));

        statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font(FontUtil.getDefaultFont().getFamily(), Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font(FontUtil.getDefaultFont().getFamily(), Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // 顶部标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(42, 42, 42));

        JLabel titleLabel = new JLabel("加解密算法工具 v2.0");
        titleLabel.setFont(new Font(FontUtil.getDefaultFont().getFamily(), Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("支持AES/DES/RSA/DSA/MD5/SHA等算法 - 安全增强版");
        subtitleLabel.setFont(new Font(FontUtil.getDefaultFont().getFamily(), Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titleCenter = new JPanel(new GridLayout(2, 1, 0, 5));
        titleCenter.setBackground(new Color(42, 42, 42));
        titleCenter.add(titleLabel);
        titleCenter.add(subtitleLabel);

        titlePanel.add(titleCenter, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // 中央区域
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                algorithmPanel, resultPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(600);
        add(splitPane, BorderLayout.CENTER);

        // 底部区域
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // 状态栏
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(new JLabel("状态:"));
        statusPanel.add(statusLabel);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(clearButton);

        bottomPanel.add(statusPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void setupListeners() {
        encryptButton.addActionListener(e -> performEncryption());
        decryptButton.addActionListener(e -> performDecryption());
        clearButton.addActionListener(e -> clearAll());
    }

    private void performEncryption() {
        statusLabel.setText("正在加密...");
        statusLabel.setForeground(Color.BLUE);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    // 使用EncryptionProcessor处理加密
                    EncryptionProcessor processor = new EncryptionProcessor(algorithmPanel);
                    return processor.processEncryption();
                } catch (Exception ex) {
                    // 包装异常以便在done方法中处理
                    throw new Exception("加密处理失败: " + ex.getMessage(), ex);
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get(); // 获取doInBackground返回的结果
                    resultPanel.setResult(result, true);
                    statusLabel.setText("加密完成");
                    statusLabel.setForeground(new Color(0, 150, 0));
                } catch (Exception ex) {
                    // 获取原始异常原因
                    Throwable cause = ex.getCause();
                    if (cause == null) cause = ex;

                    showErrorDialog("加密失败", cause);
                    statusLabel.setText("加密失败");
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }

    private void performDecryption() {
        statusLabel.setText("正在解密...");
        statusLabel.setForeground(Color.BLUE);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    // 使用EncryptionProcessor处理解密
                    EncryptionProcessor processor = new EncryptionProcessor(algorithmPanel);
                    return processor.processDecryption();
                } catch (Exception ex) {
                    // 包装异常以便在done方法中处理
                    throw new Exception("解密处理失败: " + ex.getMessage(), ex);
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get(); // 获取doInBackground返回的结果
                    resultPanel.setResult(result, false);
                    statusLabel.setText("解密完成");
                    statusLabel.setForeground(new Color(0, 150, 0));
                } catch (Exception ex) {
                    // 获取原始异常原因
                    Throwable cause = ex.getCause();
                    if (cause == null) cause = ex;

                    showErrorDialog("解密失败", cause);
                    statusLabel.setText("解密失败");
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }

    private void showErrorDialog(String title, Throwable ex) {
        String errorMessage = ex.getMessage();
        StringBuilder message = new StringBuilder();

        if (errorMessage != null) {
            message.append(errorMessage);

            if (errorMessage.contains("Unable to decode key") || errorMessage.contains("无法解析")
                    || errorMessage.contains("密钥")) {
                message.append("\n\n解决方案：\n");
                message.append("1. 重新生成密钥（推荐）\n");
                message.append("2. 确保复制完整的密钥\n");
                message.append("3. 检查密钥格式是否正确\n");
                message.append("4. 对于RSA/DSA使用PEM格式\n");
                message.append("5. 加密用公钥，解密用私钥");
            }
        } else {
            message.append("发生未知错误: ").append(ex.getClass().getName());
        }

        JOptionPane.showMessageDialog(this,
                message.toString(),
                title,
                JOptionPane.ERROR_MESSAGE);
    }

    private void clearAll() {
        algorithmPanel.clear();
        resultPanel.clear();
        statusLabel.setText("已清空");
        statusLabel.setForeground(Color.GRAY);
    }
}