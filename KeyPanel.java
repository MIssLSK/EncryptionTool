package com.encryptiontool.ui.components;

import com.encryptiontool.algorithms.util.KeyGeneratorUtil;
import com.encryptiontool.ui.ProgressDialog;
import com.encryptiontool.util.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class KeyPanel extends JPanel {
    private JTextArea keyTextArea;
    private JPasswordField passwordField;
    private JButton generateKeyButton;
    private JButton copyKeyButton;
    private JLabel keyLabel;
    private JCheckBox showKeyCheckBox;
    private JScrollPane keyScrollPane;
    private JPanel keyDisplayPanel;
    private CardLayout cardLayout;

    private JComboBox<String> algorithmComboBox;
    private JComboBox<String> keySizeComboBox;

    public KeyPanel(JComboBox<String> algorithmComboBox, JComboBox<String> keySizeComboBox) {
        this.algorithmComboBox = algorithmComboBox;
        this.keySizeComboBox = keySizeComboBox;

        initComponents();
        setupLayout();
        updateVisibility((String) algorithmComboBox.getSelectedItem());
    }

    private void initComponents() {
        keyLabel = new JLabel("密钥/密码:");
        FontUtil.setComponentFont(keyLabel);

        // 使用CardLayout来切换显示方式
        cardLayout = new CardLayout();
        keyDisplayPanel = new JPanel(cardLayout);

        // 明文显示区域
        keyTextArea = new JTextArea(5, 30);
        FontUtil.setComponentFont(keyTextArea);
        keyTextArea.setLineWrap(true);
        keyTextArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(keyTextArea);
        textScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // 密码显示区域
        passwordField = new JPasswordField(30);
        FontUtil.setComponentFont(passwordField);
        passwordField.setEchoChar('•');

        keyDisplayPanel.add(textScrollPane, "TEXT");
        keyDisplayPanel.add(passwordField, "PASSWORD");

        showKeyCheckBox = new JCheckBox("显示密钥");
        FontUtil.setComponentFont(showKeyCheckBox);
        showKeyCheckBox.addActionListener(e -> updateKeyVisibility());

        generateKeyButton = new JButton("生成密钥");
        FontUtil.setComponentFont(generateKeyButton);
        styleButton(generateKeyButton, new Color(255, 152, 0));
        generateKeyButton.addActionListener(e -> generateKey());

        copyKeyButton = new JButton("复制密钥");
        FontUtil.setComponentFont(copyKeyButton);
        styleButton(copyKeyButton, new Color(66, 133, 244));
        copyKeyButton.addActionListener(e -> copyKeyToClipboard());
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel keyInputPanel = new JPanel(new BorderLayout(5, 5));
        keyInputPanel.setBackground(Color.WHITE);

        JPanel keyLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        keyLabelPanel.setBackground(Color.WHITE);
        keyLabelPanel.add(keyLabel);
        keyLabelPanel.add(showKeyCheckBox);

        keyInputPanel.add(keyLabelPanel, BorderLayout.NORTH);
        keyInputPanel.add(keyDisplayPanel, BorderLayout.CENTER);

        JPanel keyButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        keyButtonPanel.setBackground(Color.WHITE);
        keyButtonPanel.add(generateKeyButton);
        keyButtonPanel.add(copyKeyButton);

        keyInputPanel.add(keyButtonPanel, BorderLayout.SOUTH);

        JLabel hintLabel = new JLabel("<html><b>重要提示：</b>对于RSA/DSA，请确保使用完整的PEM格式密钥</html>");
        FontUtil.setComponentFont(hintLabel);
        hintLabel.setForeground(new Color(200, 0, 0));

        add(keyInputPanel, BorderLayout.CENTER);
        add(hintLabel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font(FontUtil.getDefaultFont().getFamily(), Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updateKeyVisibility() {
        if (showKeyCheckBox.isSelected()) {
            // 显示明文
            passwordField.setText(new String(passwordField.getPassword()));
            cardLayout.show(keyDisplayPanel, "TEXT");
        } else {
            // 显示密码
            keyTextArea.setText(keyTextArea.getText());
            cardLayout.show(keyDisplayPanel, "PASSWORD");
        }
    }

    public void updateVisibility(String selectedAlgorithm) {
        if (selectedAlgorithm == null) {
            setVisible(false);
            return;
        }

        boolean isHashAlgorithm = selectedAlgorithm.contains("MD5") || selectedAlgorithm.contains("SHA");

        if (isHashAlgorithm) {
            keyTextArea.setText("哈希算法不需要密钥");
            keyTextArea.setEnabled(false);
            passwordField.setEnabled(false);
            showKeyCheckBox.setEnabled(false);
            generateKeyButton.setEnabled(false);
            copyKeyButton.setEnabled(false);
            setVisible(true);
            cardLayout.show(keyDisplayPanel, "TEXT");
        } else {
            keyTextArea.setEnabled(true);
            passwordField.setEnabled(true);
            showKeyCheckBox.setEnabled(true);
            generateKeyButton.setEnabled(true);
            copyKeyButton.setEnabled(true);

            if (keyTextArea.getText().equals("哈希算法不需要密钥")) {
                keyTextArea.setText("");
                passwordField.setText("");
            }

            setVisible(true);
            updateKeyVisibility(); // 根据复选框状态显示正确的内容
        }

        revalidate();
        repaint();
    }

    private void generateKey() {
        String selected = (String) algorithmComboBox.getSelectedItem();
        if (selected == null) return;

        int keySize = 128;
        if (keySizeComboBox.isVisible() && keySizeComboBox.getSelectedItem() != null) {
            try {
                keySize = Integer.parseInt((String) keySizeComboBox.getSelectedItem());
            } catch (NumberFormatException e) {
                // 使用默认值
            }
        }

        try {
            if ("RSA".equals(selected) || "DSA".equals(selected)) {
                generateAsymmetricKey(selected, keySize);
            } else {
                generateSymmetricKey(selected, keySize);
            }
        } catch (Exception ex) {
            showErrorDialog("生成密钥失败", ex);
        }
    }

    private void generateSymmetricKey(String algorithm, int keySize) throws Exception {
        String key = KeyGeneratorUtil.generateSymmetricKey(algorithm, keySize);
        keyTextArea.setText(key);
        passwordField.setText(key);
        keyTextArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(this,
                algorithm + "密钥生成成功！\n\n" +
                        "密钥长度: " + keySize + "位\n" +
                        "格式: Base64编码",
                "密钥生成成功",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateAsymmetricKey(String algorithm, int keySize) throws Exception {
        // 显示确认对话框
        int confirm = JOptionPane.showConfirmDialog(this,
                "即将生成" + algorithm + " " + keySize + "位密钥对，这可能需要几秒钟时间。\n" +
                        "是否继续？",
                "生成" + algorithm + "密钥对",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // 显示进度对话框
        ProgressDialog progressDialog = new ProgressDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "生成" + algorithm + "密钥对",
                "正在生成" + algorithm + " " + keySize + "位密钥对，请稍候..."
        );

        // 在后台线程中生成密钥
        SwingWorker<String[], Void> worker = new SwingWorker<String[], Void>() {
            @Override
            protected String[] doInBackground() throws Exception {
                if ("RSA".equals(algorithm)) {
                    return KeyGeneratorUtil.generateRSAKeyPair(keySize);
                } else if ("DSA".equals(algorithm)) {
                    return KeyGeneratorUtil.generateDSAKeyPair(keySize);
                }
                return new String[]{"", ""};
            }

            @Override
            protected void done() {
                progressDialog.close();

                try {
                    String[] keys = get();
                    String key = formatKeyPair(algorithm + " " + keySize + "位密钥对", keys[0], keys[1]);
                    keyTextArea.setText(key);
                    passwordField.setText(key);
                    keyTextArea.setCaretPosition(0);

                    JOptionPane.showMessageDialog(KeyPanel.this,
                            algorithm + "密钥对生成成功！\n\n" +
                                    "请确保：\n" +
                                    "1. 复制完整的密钥（使用'复制密钥'按钮）\n" +
                                    ("RSA".equals(algorithm) ?
                                            "2. 加密时使用公钥，解密时使用私钥\n" :
                                            "2. 签名时使用私钥，验证时使用公钥\n") +
                                    "3. 妥善保管私钥，不要泄露",
                            "密钥生成成功",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    showErrorDialog("生成密钥失败", ex);
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private String formatKeyPair(String title, String publicKey, String privateKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(title).append(" ===\n\n");

        sb.append("★ 公钥 (Public Key):\n");
        sb.append(publicKey).append("\n");

        sb.append("\n★ 私钥 (Private Key):\n");
        sb.append(privateKey).append("\n");

        sb.append("\n══════════════════════════════════════\n");
        sb.append("重要提醒：\n");
        sb.append("1. 请复制完整的密钥（包含BEGIN/END行）\n");
        sb.append("2. 私钥必须保密，不要泄露给他人\n");
        sb.append("3. 建议将密钥保存在安全的地方\n");
        sb.append("══════════════════════════════════════\n");

        return sb.toString();
    }

    private void copyKeyToClipboard() {
        String key;
        if (showKeyCheckBox.isSelected()) {
            key = keyTextArea.getText().trim();
        } else {
            key = new String(passwordField.getPassword()).trim();
        }

        if (!key.isEmpty() && !key.equals("哈希算法不需要密钥")) {
            StringSelection selection = new StringSelection(key);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);

            JOptionPane.showMessageDialog(this,
                    "密钥已复制到剪贴板\n\n" +
                            "提示：请确保复制的密钥是完整的",
                    "复制成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showErrorDialog(String title, Exception ex) {
        String errorMsg = ex.getMessage();
        String detailedMsg = "生成密钥失败: " + errorMsg;

        if (errorMsg != null) {
            if (errorMsg.contains("密钥长度")) {
                detailedMsg += "\n\n建议：\n" +
                        "1. RSA使用1024、2048或4096位\n" +
                        "2. DSA使用512或1024位\n" +
                        "3. AES使用128、192或256位";
            } else if (errorMsg.contains("算法")) {
                detailedMsg += "\n\n可能的原因：\n" +
                        "1. 系统不支持该算法\n" +
                        "2. JCE策略文件未更新";
            }
        }

        JOptionPane.showMessageDialog(this,
                detailedMsg,
                "错误",
                JOptionPane.ERROR_MESSAGE);
    }

    public String getKey() {
        if (showKeyCheckBox.isSelected()) {
            return keyTextArea.getText().trim();
        } else {
            return new String(passwordField.getPassword()).trim();
        }
    }

    public void clear() {
        keyTextArea.setText("");
        passwordField.setText("");
    }
}