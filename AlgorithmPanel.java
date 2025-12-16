package com.encryptiontool.ui;

import com.encryptiontool.algorithms.EncryptionAlgorithm;
import com.encryptiontool.algorithms.asymmetric.DSAAlgorithm;
import com.encryptiontool.algorithms.asymmetric.RSAAlgorithm;
import com.encryptiontool.algorithms.hash.MD5Algorithm;
import com.encryptiontool.algorithms.hash.SHA256Algorithm;
import com.encryptiontool.algorithms.hash.SHA512Algorithm;
import com.encryptiontool.algorithms.symmetric.AESAlgorithm;
import com.encryptiontool.algorithms.symmetric.BlowfishAlgorithm;
import com.encryptiontool.algorithms.symmetric.DESAlgorithm;
import com.encryptiontool.ui.components.KeyPanel;
import com.encryptiontool.util.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmPanel extends JPanel {
    private JComboBox<String> algorithmComboBox;
    private JTextArea inputTextArea;
    private KeyPanel keyPanel;
    private Map<String, EncryptionAlgorithm> algorithms;
    private JComboBox<String> keySizeComboBox;
    private JLabel algorithmInfoLabel;
    private JLabel algorithmWarningLabel;

    public AlgorithmPanel() {
        initAlgorithms();
        initComponents();
        setupLayout();
        updateAlgorithmInfo();
    }

    private void initAlgorithms() {
        algorithms = new HashMap<>();
        algorithms.put("AES", new AESAlgorithm());
        algorithms.put("DES", new DESAlgorithm());
        algorithms.put("Blowfish", new BlowfishAlgorithm());
        algorithms.put("RSA", new RSAAlgorithm());
        algorithms.put("DSA", new DSAAlgorithm());
        algorithms.put("MD5", new MD5Algorithm());
        algorithms.put("SHA-256", new SHA256Algorithm());
        algorithms.put("SHA-512", new SHA512Algorithm());
    }

    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder("输入参数"));

        // 算法选择
        String[] algorithmNames = {"AES", "DES", "Blowfish", "RSA", "DSA", "MD5", "SHA-256", "SHA-512"};
        algorithmComboBox = new JComboBox<>(algorithmNames);
        FontUtil.setComponentFont(algorithmComboBox);

        // 算法信息标签
        algorithmInfoLabel = new JLabel();
        FontUtil.setComponentFont(algorithmInfoLabel);
        algorithmInfoLabel.setForeground(new Color(0, 100, 200));

        // 算法警告标签
        algorithmWarningLabel = new JLabel();
        FontUtil.setComponentFont(algorithmWarningLabel);
        algorithmWarningLabel.setForeground(Color.RED);
        algorithmWarningLabel.setFont(new Font(getFont().getName(), Font.BOLD, 12));

        // 密钥长度选择
        keySizeComboBox = new JComboBox<>();
        FontUtil.setComponentFont(keySizeComboBox);

        // 输入文本区域
        inputTextArea = new JTextArea(10, 30);
        FontUtil.setComponentFont(inputTextArea);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);

        // 密钥面板
        keyPanel = new KeyPanel(algorithmComboBox, keySizeComboBox);

        // 添加事件监听
        algorithmComboBox.addActionListener(e -> {
            updateAlgorithmInfo();
            updateKeySizeOptions();
            keyPanel.updateVisibility((String) algorithmComboBox.getSelectedItem());
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 顶部：算法选择和密钥长度
        JPanel topPanel = new JPanel(new BorderLayout(10, 5));
        topPanel.setBackground(Color.WHITE);

        JPanel algorithmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        algorithmPanel.setBackground(Color.WHITE);
        algorithmPanel.add(new JLabel("选择算法:"));
        algorithmPanel.add(algorithmComboBox);
        algorithmPanel.add(algorithmInfoLabel);

        JPanel keySizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        keySizePanel.setBackground(Color.WHITE);
        keySizePanel.add(new JLabel("密钥长度:"));
        keySizePanel.add(keySizeComboBox);

        topPanel.add(algorithmPanel, BorderLayout.NORTH);
        topPanel.add(keySizePanel, BorderLayout.CENTER);
        topPanel.add(algorithmWarningLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 中央：输入区域
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 输入文本区
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(Color.WHITE);

        JLabel inputLabel = new JLabel("输入文本:");
        FontUtil.setComponentFont(inputLabel);
        inputPanel.add(inputLabel, BorderLayout.NORTH);

        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(keyPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * 更新算法信息
     */
    private void updateAlgorithmInfo() {
        String selected = (String) algorithmComboBox.getSelectedItem();
        if (selected == null) return;

        String info = "";
        String warning = "";

        switch (selected) {
            case "AES":
                info = "对称加密，支持128/192/256位密钥，CBC模式";
                break;
            case "DES":
                info = "对称加密，固定56位密钥，CBC模式";
                warning = "⚠️ 警告：DES算法已过时，安全性低，仅用于兼容目的";
                break;
            case "Blowfish":
                info = "对称加密，支持变长密钥（32-448位），CBC模式";
                break;
            case "RSA":
                info = "非对称加密，建议2048位密钥（安全）";
                break;
            case "DSA":
                info = "数字签名算法，建议1024位密钥";
                break;
            case "MD5":
                info = "哈希算法，128位输出";
                warning = "⚠️ 警告：MD5已不安全，易发生碰撞，不建议用于安全应用";
                break;
            case "SHA-256":
                info = "哈希算法，256位输出";
                break;
            case "SHA-512":
                info = "哈希算法，512位输出";
                break;
        }
        algorithmInfoLabel.setText(info);
        algorithmWarningLabel.setText(warning);
        algorithmWarningLabel.setVisible(!warning.isEmpty());
    }

    /**
     * 更新密钥长度选项
     */
    private void updateKeySizeOptions() {
        String selected = (String) algorithmComboBox.getSelectedItem();
        keySizeComboBox.removeAllItems();

        if ("AES".equals(selected)) {
            keySizeComboBox.addItem("128");
            keySizeComboBox.addItem("192");
            keySizeComboBox.addItem("256");
            keySizeComboBox.setSelectedItem("128");
            keySizeComboBox.setVisible(true);
        } else if ("DES".equals(selected)) {
            keySizeComboBox.addItem("56");
            keySizeComboBox.setSelectedItem("56");
            keySizeComboBox.setVisible(true);
        } else if ("Blowfish".equals(selected)) {
            keySizeComboBox.addItem("128");
            keySizeComboBox.addItem("192");
            keySizeComboBox.addItem("256");
            keySizeComboBox.addItem("448");
            keySizeComboBox.setSelectedItem("128");
            keySizeComboBox.setVisible(true);
        } else if ("RSA".equals(selected)) {
            keySizeComboBox.addItem("1024");
            keySizeComboBox.addItem("2048");
            keySizeComboBox.addItem("4096");
            keySizeComboBox.setSelectedItem("2048");
            keySizeComboBox.setVisible(true);
        } else if ("DSA".equals(selected)) {
            keySizeComboBox.addItem("512");
            keySizeComboBox.addItem("1024");
            keySizeComboBox.setSelectedItem("1024");
            keySizeComboBox.setVisible(true);
        } else {
            keySizeComboBox.setVisible(false);
        }
    }

    public String getInputText() {
        return inputTextArea.getText().trim();
    }

    public String getSelectedAlgorithm() {
        return (String) algorithmComboBox.getSelectedItem();
    }

    public EncryptionAlgorithm getAlgorithm() {
        return algorithms.get(getSelectedAlgorithm());
    }

    public String getKey() {
        return keyPanel.getKey();
    }

    public int getKeySize() {
        if (keySizeComboBox.isVisible() && keySizeComboBox.getSelectedItem() != null) {
            try {
                return Integer.parseInt((String) keySizeComboBox.getSelectedItem());
            } catch (NumberFormatException e) {
                return 128; // 默认值
            }
        }
        return 128;
    }

    public KeyPanel getKeyPanel() {
        return keyPanel;
    }

    public void clear() {
        inputTextArea.setText("");
        keyPanel.clear();
        updateAlgorithmInfo();
    }
}