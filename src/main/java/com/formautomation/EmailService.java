package com.formautomation;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.*;
import java.util.Properties;

/**
 * Simple email service for sending automation results
 */
public class EmailService {

    private Properties emailConfig;

    public EmailService() {
        loadEmailConfig();
    }

    /**
     * Load email configuration from properties file
     */
    private void loadEmailConfig() {
        emailConfig = new Properties();
        try (InputStream input = new FileInputStream("email-config.properties")) {
            emailConfig.load(input);
            System.out.println("✅ Email configuration loaded successfully");
        } catch (IOException e) {
            System.err.println("❌ Error loading email configuration: " + e.getMessage());
            emailConfig = null;
        }
    }

    /**
     * Send email with Excel attachment
     * @param runCount Number of successful runs completed
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendResults(int runCount) {
        if (emailConfig == null) {
            System.err.println("❌ Email configuration not loaded. Cannot send email.");
            return false;
        }

        try {
            System.out.println("📧 Preparing to send email with results...");

            // Setup mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.host", emailConfig.getProperty("email.smtp.host"));
            props.put("mail.smtp.port", emailConfig.getProperty("email.smtp.port"));
            props.put("mail.smtp.auth", emailConfig.getProperty("email.smtp.auth"));
            props.put("mail.smtp.starttls.enable", emailConfig.getProperty("email.smtp.starttls.enable"));

            // Create session with authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            emailConfig.getProperty("email.username"),
                            emailConfig.getProperty("email.password")
                    );
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getProperty("email.from")));

            // Add recipients
            String[] recipients = emailConfig.getProperty("email.to").split(",");
            InternetAddress[] recipientAddresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipients[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddresses);

            // Set subject
            message.setSubject(emailConfig.getProperty("email.subject"));

            // Create multipart message for attachment
            Multipart multipart = new MimeMultipart();

            // Add text body
            MimeBodyPart textPart = new MimeBodyPart();
            String emailBody = emailConfig.getProperty("email.body").replace("{RUN_COUNT}", String.valueOf(runCount));
            textPart.setText(emailBody);
            multipart.addBodyPart(textPart);

            // Add Excel file attachment
            String excelFile = emailConfig.getProperty("excel.filename", "form_data.xlsx");
            File attachment = new File(excelFile);
            if (attachment.exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(attachment);
                multipart.addBodyPart(attachmentPart);
                System.out.println("📎 Excel file attached: " + excelFile);
            } else {
                System.out.println("⚠️ Excel file not found: " + excelFile);
            }

            // Set content
            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("✅ Email sent successfully to " + recipients.length + " recipient(s)");
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the configured loop count
     * @return number of times to run automation
     */
    public int getLoopCount() {
        if (emailConfig == null) {
            return 1; // Default to 1 run if config not loaded
        }
        return Integer.parseInt(emailConfig.getProperty("automation.loop.count", "1"));
    }
}