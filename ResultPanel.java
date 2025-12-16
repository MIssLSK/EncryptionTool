package com.encryptiontool.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResultPanel extends JPanel {
    private JTextArea resultTextArea;
    private JLabel operationLabel;
    private JButton copyButton;
    private JButton saveButton;

    public ResultPanel() {
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "处理结果",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font(getChineseFontName(), Font.BOLD, 14),
                new Color(70, 70, 70)
        ));

        // 操作类型标签
        operationLabel = new JLabel("未执行操作");
        operationLabel.setFont(new Font(getChineseFontName(), Font.BOLD, 16));
        operationLabel.setForeground(new Color(100, 100, 100));

        // 结果文本区域 - 强制使用支持中文的字体
        resultTextArea = new JTextArea(15, 30);

        // 关键修复：强制设置为支持中文的字体
        String chineseFont = getChineseFontName();
        Font textAreaFont = new Font(chineseFont, Font.PLAIN, 13);
        resultTextArea.setFont(textAreaFont);
        System.out.println("设置JTextArea字体为: " + textAreaFont.getFamily());

        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        resultTextArea.setEditable(false);
        resultTextArea.setBackground(new Color(250, 250, 250));

        // 测试字体是否能显示中文
        testFontDisplay(textAreaFont);

        // 创建滚动面板
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        resultScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // 按钮
        copyButton = createStyledButton("复制到剪贴板", new Color(66, 133, 244));
        saveButton = createStyledButton("保存结果", new Color(52, 168, 83));

        // 初始化隐藏按钮
        copyButton.setVisible(false);
        saveButton.setVisible(false);
    }

    /**
     * 获取可用的中文字体名称
     */
    private String getChineseFontName() {
        // 不同操作系统推荐的中文字体
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("windows")) {
            // Windows系统
            String[] windowsFonts = {
                    "Microsoft YaHei UI",  // Win8+ 雅黑UI
                    "Microsoft YaHei",      // Win7+ 雅黑
                    "SimSun",               // 宋体
                    "NSimSun",              // 新宋体
                    "SimHei",               // 黑体
                    "KaiTi",                // 楷体
                    "FangSong"              // 仿宋
            };

            for (String fontName : windowsFonts) {
                if (isFontAvailable(fontName)) {
                    System.out.println("选择Windows字体: " + fontName);
                    return fontName;
                }
            }
        } else if (osName.contains("mac")) {
            // macOS系统
            String[] macFonts = {
                    "PingFang SC",          // 苹方
                    "Hiragino Sans GB",     // 冬青黑体
                    "STHeiti",              // 华文黑体
                    "STSong",               // 华文宋体
                    "Apple LiGothic",       // 苹果丽黑
                    "Apple LiSung"          // 苹果丽宋
            };

            for (String fontName : macFonts) {
                if (isFontAvailable(fontName)) {
                    System.out.println("选择macOS字体: " + fontName);
                    return fontName;
                }
            }
        } else {
            // Linux或其他系统
            String[] linuxFonts = {
                    "WenQuanYi Zen Hei",    // 文泉驿正黑
                    "WenQuanYi Micro Hei",  // 文泉驿微米黑
                    "Noto Sans CJK SC",     // 思源黑体
                    "DejaVu Sans",          // DejaVu字体
                    "Liberation Sans"       // Liberation字体
            };

            for (String fontName : linuxFonts) {
                if (isFontAvailable(fontName)) {
                    System.out.println("选择Linux字体: " + fontName);
                    return fontName;
                }
            }
        }

        // 如果都没找到，使用Java默认字体
        System.out.println("未找到中文字体，使用默认字体");
        return Font.DIALOG;
    }

    /**
     * 检查字体是否可用
     */
    private boolean isFontAvailable(String fontName) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        for (String name : fontNames) {
            if (name.equals(fontName)) {
                // 进一步检查是否能显示中文
                Font font = new Font(fontName, Font.PLAIN, 12);
                return font.canDisplay('中') && font.canDisplay('文');
            }
        }
        return false;
    }

    /**
     * 测试字体显示（仅控制台输出，不显示弹窗）
     */
    private void testFontDisplay(Font font) {
        System.out.println("字体测试:");
        System.out.println("  字体名称: " + font.getFamily());
        System.out.println("  字体样式: " + font.getStyle());
        System.out.println("  字体大小: " + font.getSize());
        System.out.println("  能否显示'中': " + font.canDisplay('中'));
        System.out.println("  能否显示'文': " + font.canDisplay('文'));
        System.out.println("  能否显示'A': " + font.canDisplay('A'));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        // 按钮也使用中文字体
        button.setFont(new Font(getChineseFontName(), Font.PLAIN, 12));

        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // 顶部：操作标签
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(operationLabel);
        add(topPanel, BorderLayout.NORTH);

        // 中央：结果区域
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel resultLabel = new JLabel("结果输出:");
        resultLabel.setFont(new Font(getChineseFontName(), Font.PLAIN, 14));
        centerPanel.add(resultLabel, BorderLayout.NORTH);

        centerPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 底部：按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(copyButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        copyButton.addActionListener(e -> copyToClipboard());
        saveButton.addActionListener(e -> saveResult());
    }

    /**
     * 设置结果文本 - 直接设置，确保字体正确
     */
    public void setResult(String result, boolean isEncryption) {
        if (isEncryption) {
            operationLabel.setText("加密结果");
            operationLabel.setForeground(new Color(0, 150, 136));
        } else {
            operationLabel.setText("解密结果");
            operationLabel.setForeground(new Color(63, 81, 181));
        }

        // 打印调试信息（仅控制台）
        System.out.println("\n=== 设置结果到JTextArea ===");
        System.out.println("操作: " + (isEncryption ? "加密" : "解密"));
        System.out.println("结果长度: " + result.length());
        System.out.println("JTextArea当前字体: " + resultTextArea.getFont().getFamily());

        // 直接设置文本
        resultTextArea.setText(result);

        // 显示按钮
        copyButton.setVisible(true);
        saveButton.setVisible(true);

        // 自动滚动到顶部
        resultTextArea.setCaretPosition(0);

        // 强制重绘
        resultTextArea.revalidate();
        resultTextArea.repaint();
    }

    private void copyToClipboard() {
        String result = resultTextArea.getText();
        if (!result.isEmpty()) {
            StringSelection stringSelection = new StringSelection(result);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            // 显示提示
            JOptionPane.showMessageDialog(this, "结果已复制到剪贴板",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveResult() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存结果");
        fileChooser.setSelectedFile(new File("encryption_result.txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave, StandardCharsets.UTF_8))) {
                writer.write(resultTextArea.getText());
                JOptionPane.showMessageDialog(this,
                        "结果已保存到: " + fileToSave.getPath(),
                        "保存成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "保存失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void clear() {
        operationLabel.setText("未执行操作");
        operationLabel.setForeground(new Color(100, 100, 100));
        resultTextArea.setText("");
        copyButton.setVisible(false);
        saveButton.setVisible(false);
    }
}