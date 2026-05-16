package com.guanshiyun.service.QQEmailVerificationCode.impl;

import com.guanshiyun.rpc.qqCode.QQCode;
import com.guanshiyun.service.QQEmailVerificationCode.QQEmailCodeService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class  QQEmailServiceImpl implements QQEmailCodeService {
    private final JavaMailSender javaMailSender;

    // 从配置文件中读取发件人邮箱，避免硬编码
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送邮箱验证码
     */
    @Override
    public Mono<Boolean> sendQQEmailCode(QQCode qqCode) {
        return Mono.fromCallable(() -> {

            // 2. 准备发送邮件
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(qqCode.getEmail());
            helper.setSubject("滇西集团验证码");
            // 发送 HTML 格式的邮件，让验证码更醒目
            String content = String.format(
                    "<div style='padding: 20px;'>" +
                            "<h3>您好，您的验证码是：</h3>" +
                            "<h1 style='color: #FF5722; font-size: 32px;'>%s</h1>" +
                            "<p style='color: #666;'>验证码 %d 分钟内有效，请勿泄露给他人。</p>" +
                            "</div>",
                    qqCode.getCode(),
                    qqCode.getExpire());
            helper.setText(content, true);

            // 3. 执行发送
            javaMailSender.send(mimeMessage);

            return true; // 发送成功返回 true
        }).onErrorResume(e -> {
            // 捕获异常（如邮箱不存在、网络问题等），返回 false
            log.error("Failed to send email verification code", e);
            return Mono.just(false);
        });
    }
}
