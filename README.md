📖 使用说明文档 (README.md)
markdown
# 加解密算法工具 v2.0 - 安全增强版

[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-2.0-orange.svg)]()

一个功能强大的图形化加解密工具，支持多种加密算法和哈希算法，专为安全性和易用性设计。

## ✨ 特性

### 安全增强
- ✅ 所有对称加密算法采用CBC模式（替代不安全的ECB模式）
- ✅ 添加IV（初始化向量）支持，提高加密安全性
- ✅ DES和MD5算法添加明确的安全警告
- ✅ 密钥长度验证和格式检查

### 支持算法
| 类型 | 算法 | 说明 |
|------|------|------|
| **对称加密** | AES | 支持128/192/256位，CBC模式 |
| **对称加密** | DES | 56位，CBC模式（已过时，仅用于兼容） |
| **对称加密** | Blowfish | 32-448位变长密钥，CBC模式 |
| **非对称加密** | RSA | 支持1024/2048/4096位密钥 |
| **数字签名** | DSA | 支持512/1024位密钥对 |
| **哈希算法** | MD5 | 128位输出（已不安全） |
| **哈希算法** | SHA-256 | 256位输出 |
| **哈希算法** | SHA-512 | 512位输出 |

### 用户体验
- 🎯 直观的图形界面，支持中文显示
- 🔑 密钥生成和复制功能
- 📊 密钥生成进度指示
- 🚨 详细的错误提示和解决方案
- 📋 结果复制和保存功能

## 🚀 快速开始

### 系统要求
- **Java 8** 或更高版本
- **Windows 7+** / **macOS 10.10+** / **Linux**（支持GUI）
- 至少 **512MB** 内存

### 下载安装

#### 方法1：使用安装程序（推荐）
1. 下载最新版本安装包：`EncryptionToolSetup.exe`
2. 双击运行安装程序
3. 按照向导完成安装
4. 从开始菜单或桌面快捷方式启动程序

#### 方法2：使用可执行JAR
1. 下载：`encryption-tool.jar`
2. 确保已安装Java运行环境
3. 双击运行或使用命令：
   ```bash
   java -jar encryption-tool.jar
方法3：从源码编译
bash
# 克隆项目
https://github.com/MIssLSK/EncryptionTool.git
cd encryption-tool

# 编译项目
ant compile

# 运行
ant run
🖥️ 使用指南
基本操作流程
选择算法：从下拉菜单选择所需算法

输入文本：在左侧文本框中输入要处理的内容

处理密钥：

点击"生成密钥"创建新密钥

或手动输入/粘贴密钥

使用"显示密钥"查看明文

执行操作：点击"加密"或"解密"按钮

查看结果：结果显示在右侧面板，可复制或保存

算法使用说明
对称加密（AES/DES/Blowfish）
text
加密：明文 + 密钥 → 密文
解密：密文 + 密钥 → 明文
注意：使用相同密钥进行加密和解密
非对称加密（RSA）
text
加密：明文 + 公钥 → 密文
解密：密文 + 私钥 → 明文
注意：公钥加密，私钥解密
数字签名（DSA）
text
签名：数据 + 私钥 → 签名
验证：数据 + 签名 + 公钥 → 验证结果
哈希算法（MD5/SHA）
text
计算：数据 → 哈希值
特点：不可逆，无需密钥
密钥格式要求
对称加密密钥
可以是任意字符串

程序自动处理长度（截取/填充）

建议使用Base64编码的随机密钥

RSA/DSA密钥
必须使用完整PEM格式

包含-----BEGIN和-----END标记

使用"生成密钥"按钮确保格式正确

重要：私钥必须保密，不要泄露

🔧 高级功能
密钥生成
支持所有算法的密钥生成

RSA/DSA密钥生成显示进度条

自动格式化为PEM格式

一键复制完整密钥

错误处理
详细的错误信息提示

提供解决方案建议

密钥格式自动检测和修复

安全性
使用SecureRandom生成随机数

CBC模式增强安全性

密钥长度验证

过时算法安全警告

📦 项目构建
编译环境
JDK 8 或更高版本

Apache Ant 1.10+（可选）

构建命令
bash
# 清理编译文件
ant clean

# 编译项目
ant compile

# 创建可执行JAR
ant jar

# 运行程序
ant run

# 创建完整发行版
ant dist
目录结构
text
encryption-tool/
├── build/           # 编译输出
├── dist/           # 发行版文件
├── lib/            # 依赖库
├── src/            # 源代码
└── resources/      # 资源文件

🔒 安全建议
算法选择
推荐使用：AES-256、RSA-2048、SHA-256

谨慎使用：DES、MD5（仅用于兼容）

避免使用：已知存在漏洞的算法

密钥管理
定期更换对称加密密钥

妥善保管RSA/DSA私钥

不要将密钥硬编码在代码中

使用后清除剪贴板中的敏感信息

操作环境
在安全的环境中生成密钥

避免在公共计算机上处理敏感数据

使用后关闭程序

🤝 贡献指南
报告问题
在GitHub Issues中创建问题

提供详细的重现步骤

包含操作系统和Java版本信息

提交代码
Fork项目

创建功能分支

提交更改

创建Pull Request

开发规范
遵循Java编码规范

添加适当的注释

编写单元测试（可选）

更新相关文档

📝 版本历史
v2.0 (当前版本)
✅ 所有对称加密改用CBC模式

✅ 添加IV支持增强安全性

✅ DES/MD5算法安全警告

✅ 密钥生成进度指示

✅ 代码重构和优化

v1.0
✅ 基础加密算法支持

✅ 图形用户界面

✅ 密钥生成功能

📄 许可证
本项目采用 MIT 许可证 - 详见 LICENSE 文件。

📞 联系方式
问题反馈：GitHub Issues

邮件支持：2820241167@qq.com

项目主页： https://github.com/MIssLSK/EncryptionTool

🙏 致谢
感谢所有贡献者和用户的支持！

🔄 更新日志
v2.0.1 (计划中)
添加文件加密功能

支持更多加密算法（ChaCha20, Ed25519）

添加密码管理功能

国际化支持（英文界面）

🆘 技术支持
获取帮助
查看文档：程序内的帮助信息

在线文档：GitHub Wiki页面

问题反馈：GitHub Issues

社区讨论：GitHub Discussions

调试信息收集
bash
# 启用详细日志
java -Dlogging.level=DEBUG -jar encryption-tool.jar > debug.log 2>&1
📋 系统要求详细说明
最低配置
CPU：双核 1.5GHz

内存：512MB RAM

磁盘：50MB可用空间

显示：1024x768分辨率

推荐配置
CPU：四核 2.0GHz+

内存：2GB RAM

磁盘：200MB可用空间（含JRE）

显示：1920x1080分辨率

🏆 致谢
感谢以下开源项目：

Java Platform

Apache Ant

Launch4j

Inno Setup

所有贡献者和用户

注意：本项目仅供学习和研究使用，请勿用于非法用途。使用加密工具时，请遵守当地法律法规。

安全提示：定期更新软件，使用强密码，妥善保管密钥，避免在不安全的设备上处理敏感信息。

技术支持：如有问题，请访问项目主页获取最新信息和帮助。

© 2025 EncryptionTool Team. All rights reserved.

text

✅ 所有优化后的Java源码

✅ 完整的文档
