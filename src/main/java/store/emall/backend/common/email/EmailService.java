package store.emall.backend.common.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    public void sendOtpEmail(String toEmail, String username, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(toEmail);
            helper.setSubject("E-Malls — Password Reset OTP");
            helper.setText(buildOtpEmailBody(username, otp), true);

            mailSender.send(message);
            log.info("OTP email sent to '{}' for user '{}'", toEmail, username);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send OTP email to '{}': {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }
    }

    private String buildOtpEmailBody(String username, String otp) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                    <h2 style="color: #333;">Password Reset Request</h2>
                    <p>Hello <strong>%s</strong>,</p>
                    <p>We received a request to reset your password. Use the OTP code below to proceed:</p>
                    <div style="text-align: center; margin: 32px 0;">
                        <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #4A90E2;">%s</span>
                    </div>
                    <p>This code is valid for <strong>5 minutes</strong>. Do not share it with anyone.</p>
                    <p>If you did not request a password reset, you can safely ignore this email.</p>
                    <hr style="margin-top: 32px; border: none; border-top: 1px solid #eee;" />
                    <p style="color: #999; font-size: 12px;">E-Malls Platform — This is an automated message, please do not reply.</p>
                </div>
                """.formatted(username, otp);
    }
}