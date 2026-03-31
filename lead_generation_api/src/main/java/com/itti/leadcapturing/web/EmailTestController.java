package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    /**
     * Test 1: Check Email Configuration
     */
    @GetMapping("/email/config")
    public ResponseEntity<Map<String, Object>> checkEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("emailEnabled", emailEnabled);
        config.put("fromEmail", fromEmail);
        config.put("smtpUsername", smtpUsername);
        config.put("mailSenderConfigured", mailSender != null);
        config.put("emailServiceConfigured", emailService != null);
        return ResponseEntity.ok(config);
    }

    /**
     * Test 2: Send Simple Test Email
     */
    @PostMapping("/email/send-simple")
    public ResponseEntity<Map<String, String>> sendSimpleTestEmail(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        try {
            String toEmail = request.get("toEmail");

            if (toEmail == null || toEmail.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Please provide 'toEmail' in request body");
                return ResponseEntity.badRequest().body(response);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🧪 Test Email from RFQ System");
            message.setText(
                    "Hello!\n\nThis is a test email from your RFQ Management System.\n\n" +
                    "If you received this email, your email configuration is working correctly! ✅\n\n" +
                    "Sent from: " + fromEmail + "\n" +
                    "Time: " + new java.util.Date()
            );

            mailSender.send(message);

            response.put("status", "success");
            response.put("message", "Test email sent successfully to " + toEmail);
            response.put("from", fromEmail);
            response.put("to", toEmail);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test 3: Send HTML Test Email
     */
    @PostMapping("/email/send-html")
    public ResponseEntity<Map<String, String>> sendHtmlTestEmail(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        try {
            String toEmail = request.get("toEmail");

            if (toEmail == null || toEmail.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Please provide 'toEmail' in request body");
                return ResponseEntity.badRequest().body(response);
            }

            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            padding: 30px;
                            text-align: center;
                            border-radius: 10px 10px 0 0;
                        }
                        .header h1 { color: white; margin: 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .success-box {
                            background: #d4edda;
                            border-left: 4px solid #28a745;
                            padding: 15px;
                            margin: 20px 0;
                        }
                        .btn {
                            display: inline-block;
                            padding: 12px 30px;
                            background: #667eea;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🧪 HTML Email Test</h1>
                        </div>
                        <div class="content">
                            <h2>Email Configuration Test</h2>

                            <div class="success-box">
                                <strong>✅ Success!</strong><br>
                                Your email system is working correctly with HTML templates.
                            </div>

                            <ul>
                                <li>Sent From: %s</li>
                                <li>Test Time: %s</li>
                                <li>Email Service: Gmail SMTP</li>
                                <li>Template Engine: Thymeleaf</li>
                            </ul>

                            <p style="text-align:center;margin-top:30px;">
                                <a href="#" class="btn">Your RFQ System is Ready!</a>
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                fromEmail,
                new java.util.Date()
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("🧪 HTML Test Email from RFQ System");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            response.put("status", "success");
            response.put("message", "HTML test email sent successfully to " + toEmail);
            response.put("from", fromEmail);
            response.put("to", toEmail);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send HTML email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}