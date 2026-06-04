package com.inkfield.backend.service.mail;

/**
 * 注册验证码邮件模板（白色磨砂玻璃风格 HTML + 纯文本备用）。
 */
public final class RegistrationVerificationEmail {
    public static final String APP_NAME = "网文助手";
    public static final String SUBJECT = APP_NAME + " 注册验证码";

    private RegistrationVerificationEmail() {
    }

    public static String plainText(String code) {
        return """
            您好，

            您的注册验证码为：%s

            验证码 3 分钟内有效，请勿泄露给他人。

            如非本人操作，请忽略此邮件。

            — %s
            """.formatted(code, APP_NAME).trim();
    }

    public static String html(String code, String registerUrl) {
        String safeCode = escapeHtml(code);
        String safeRegisterUrl = escapeHtml(registerUrl == null || registerUrl.isBlank() ? "#" : registerUrl);
        String spacedCode = safeCode.chars()
            .mapToObj(character -> String.valueOf((char) character))
            .reduce((left, right) -> left + " " + right)
            .orElse(safeCode);

        return """
            <!DOCTYPE html>
            <html lang="zh-CN">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>%s</title>
            </head>
            <body style="margin:0;padding:0;background:#f0f0f2;font-family:'Segoe UI','PingFang SC','Microsoft YaHei',sans-serif;color:#1c1c1e;">
              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:linear-gradient(165deg,#fafafa 0%%,#f0f0f2 50%%,#e8e8ed 100%%);padding:40px 16px;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:520px;border-radius:20px;border:1px solid rgba(255,255,255,0.95);background:rgba(255,255,255,0.78);box-shadow:0 20px 50px rgba(0,0,0,0.06),0 1px 0 rgba(255,255,255,1) inset;overflow:hidden;">
                      <tr>
                        <td style="padding:28px 32px 20px;border-bottom:1px solid rgba(0,0,0,0.05);">
                          <p style="margin:0 0 6px;font-size:11px;letter-spacing:0.12em;color:#86868b;font-weight:600;">%s</p>
                          <h1 style="margin:0;font-size:22px;font-weight:600;color:#1c1c1e;line-height:1.3;">注册验证码</h1>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:28px 32px 12px;">
                          <p style="margin:0 0 20px;font-size:15px;line-height:1.65;color:#48484a;">您好，您正在注册 <strong>%s</strong>。请使用下方验证码：</p>
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin:0 0 20px;border-radius:16px;border:1px solid rgba(255,255,255,0.9);background:rgba(255,255,255,0.65);box-shadow:0 4px 24px rgba(0,0,0,0.04),inset 0 1px 0 rgba(255,255,255,1);">
                            <tr>
                              <td align="center" style="padding:22px 24px;">
                                <span style="font-size:30px;font-weight:700;letter-spacing:0.32em;color:#1c1c1e;font-family:'Consolas','Monaco',monospace;">%s</span>
                              </td>
                            </tr>
                          </table>
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="margin:0 0 20px;">
                            <tr>
                              <td align="center">
                                <a href="%s" style="display:inline-block;padding:11px 22px;border-radius:12px;border:1px solid rgba(0,0,0,0.1);background:rgba(28,28,30,0.92);color:#ffffff;font-size:14px;font-weight:600;text-decoration:none;">前往注册页</a>
                              </td>
                            </tr>
                          </table>
                          <p style="margin:0 0 8px;font-size:13px;line-height:1.55;color:#86868b;">验证码 <strong style="color:#1c1c1e;">3 分钟内</strong>有效，请勿泄露给他人。</p>
                          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="border-radius:12px;border:1px solid rgba(0,0,0,0.04);background:rgba(255,255,255,0.5);">
                            <tr>
                              <td style="padding:12px 14px;font-size:13px;line-height:1.55;color:#86868b;">
                                如非本人操作，请忽略此邮件。
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:16px 32px 24px;border-top:1px solid rgba(0,0,0,0.04);">
                          <p style="margin:0;font-size:12px;color:#aeaeb2;text-align:center;">— %s</p>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(SUBJECT, APP_NAME, APP_NAME, spacedCode, safeRegisterUrl, APP_NAME);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
