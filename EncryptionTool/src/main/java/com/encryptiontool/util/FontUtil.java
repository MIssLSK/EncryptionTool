package com.encryptiontool.util;

import javax.swing.*;
import java.awt.*;

public class FontUtil {
    private static Font defaultFont = null;

    /**
     * 获取默认字体
     */
    public static Font getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = findChineseFont();
        }
        return defaultFont;
    }

    /**
     * 寻找可用的中文字体
     */
    private static Font findChineseFont() {
        // 中文字体优先级列表
        String[] chineseFonts = {
                "Microsoft YaHei UI",
                "Microsoft YaHei",
                "SimSun",
                "NSimSun",
                "SimHei",
                "PingFang SC",
                "Hiragino Sans GB",
                "STHeiti",
                "WenQuanYi Zen Hei",
                "文泉驿正黑",
                "DejaVu Sans",
                Font.DIALOG,
                Font.SANS_SERIF
        };

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();

        System.out.println("系统可用字体数量: " + availableFonts.length);

        // 寻找首选字体
        for (String preferredFont : chineseFonts) {
            for (String availableFont : availableFonts) {
                if (availableFont.equalsIgnoreCase(preferredFont)) {
                    System.out.println("选择字体: " + availableFont);
                    return new Font(availableFont, Font.PLAIN, 12);
                }
            }
        }

        // 如果找不到，使用默认字体
        System.out.println("未找到中文字体，使用默认字体");
        return new Font(Font.DIALOG, Font.PLAIN, 12);
    }

    /**
     * 设置组件字体
     */
    public static void setComponentFont(JComponent component) {
        Font currentFont = component.getFont();
        Font newFont = getDefaultFont().deriveFont(currentFont.getStyle(), currentFont.getSize());
        component.setFont(newFont);
    }

    /**
     * 设置所有UI组件字体
     */
    public static void setUIFont() {
        Font defaultFont = getDefaultFont();

        // 设置所有UI组件的默认字体
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                Font originalFont = (Font) value;
                Font newFont = defaultFont.deriveFont(
                        originalFont.getStyle(),
                        originalFont.getSize()
                );
                UIManager.put(key, newFont);
            }
        }

        System.out.println("UI字体设置完成: " + defaultFont.getFamily());
    }
}