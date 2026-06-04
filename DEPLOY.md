# 服务器部署说明

所有配置集中在 **`src/main/resources/application.yml`** 一个文件中，修改后重启后端即可。

## 1. MySQL

本地默认（已写在 `application.yml`）：

| 项 | 值 |
|----|-----|
| 地址 | `127.0.0.1:3306` |
| 库名 | `novel_studio` |
| 用户名 | `root` |
| 密码 | `root` |

首次使用前在 MySQL 执行：

```sql
CREATE DATABASE novel_studio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

应用启动时 **Flyway** 会自动执行 `db/migration/V1__init_schema.sql` 建表。

**上服务器时**：直接改 `application.yml` 里 `spring.datasource` 的 `url`、`username`、`password`。

## 2. SMTP 邮件（网易 126）

配置在 **`backend/src/main/resources/application.yml`**，已预设：

| 项 | 值 |
|----|-----|
| 发信账号 | `wangwenzhushou@126.com` |
| SMTP | `smtp.126.com:465`（SSL） |

**授权码填写位置**（只改这一处）：

```yaml
spring:
  mail:
    password: "你的授权码粘贴到这里"
```

`password` 填的是网易邮箱后台生成的 **SMTP 授权码**，不是网页登录密码。

获取授权码：登录 [mail.126.com](https://mail.126.com) → 设置 → POP3/SMTP/IMAP → 开启 SMTP → 新增授权码。

改完后重启后端。若发信失败，检查授权码是否过期、是否开启 SMTP 服务。

## 3. 旧数据迁移

若此前使用 `backend/data/` 下的 JSON，在 **users 表为空** 且 `novel.storage.import-legacy-json: true` 时，启动会自动导入。导入完成后可改为 `false`。

## 4. IDEA 本地运行

1. 确认 MySQL 已启动，且存在库 `novel_studio`。
2. 确认 `application.yml` 中数据库账号密码（默认 `root` / `root`）。
3. 运行 `NovelAiStudioApplication`。

## 5. 构建与运行

```powershell
cd backend
.\scripts\build.ps1
java -jar target\backend-0.1.0.jar
```

前端由 Nginx 反代 `/api` 到 `http://127.0.0.1:4000`。

## 6. 数据表说明

| 表 | 用途 |
|----|------|
| `users` | 账号 |
| `auth_sessions` | 登录 Token |
| `email_verification_codes` | 注册邮箱验证码 |
| `captcha_challenges` | 人机验证 |
| `user_libraries` | 当前选中书籍 |
| `books` | 书籍摘要 + 完整 `project_json` |
