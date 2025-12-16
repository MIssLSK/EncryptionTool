package com.encryptiontool;

import com.encryptiontool.ui.MainFrame;
import com.encryptiontool.util.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;

public class Main {

    static {
        // 强制设置系统编码为UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("user.language", "zh");
        System.setProperty("user.country", "CN");

        // 验证编码设置
        System.out.println("系统编码: " + System.getProperty("file.encoding"));
        System.out.println("默认字符集: " + Charset.defaultCharset().name());
        System.out.println("JVM默认区域: " + java.util.Locale.getDefault());

        // 显示版本信息
        System.out.println("========================================");
        System.out.println("加解密算法工具 v2.0 - 安全增强版");
        System.out.println("更新内容:");
        System.out.println("1. 所有对称加密算法改用CBC模式");
        System.out.println("2. 添加DES/MD5算法安全警告");
        System.out.println("3. 改进密钥生成进度指示");
        System.out.println("4. 优化代码结构，移除冗余");
        System.out.println("========================================");
    }

    public static void main(String[] args) {
        // 设置默认Unicode字体，确保中文显示
        setDefaultFont();

        SwingUtilities.invokeLater(() -> {
            try {
                // 设置系统外观
                System.out.println("设置系统外观...");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("系统外观设置完成");
            } catch (Exception e) {
                System.err.println("设置外观失败，将使用默认外观: " + e.getMessage());
            }

            try {
                System.out.println("创建主窗口...");
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                System.out.println("程序启动成功");
                System.out.println("提示: DES和MD5算法已添加安全警告，建议使用AES或SHA-256");
            } catch (Exception e) {
                System.err.println("程序启动失败: " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                        "程序启动失败: " + e.getMessage() + "\n" +
                                "请确保已安装Java 8或更高版本",
                        "启动错误",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    /**
     * 设置默认字体，确保中文正常显示
     */
    private static void setDefaultFont() {
        try {
            // 设置UI字体
            FontUtil.setUIFont();
            System.out.println("字体设置完成");

        } catch (Exception e) {
            System.err.println("设置字体失败: " + e.getMessage());
        }
    }
}