package com.jza.utils;

import com.jza.model.Mail;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;
    @Autowired
    FreeMarkerConfig freeMarkerConfig;

    public boolean sendWithHTMLTemplate(Mail mail) {
        try {
            String nick = MimeUtility.encodeText("meguru");
            InternetAddress from = new InternetAddress(nick + "<yinaba_meguru.163.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            Template template1 = freeMarkerConfig.getConfiguration().getTemplate(mail.getTemplate(),"utf-8");
            String result = FreeMarkerTemplateUtils.processTemplateIntoString(template1, mail.getModel());
            mimeMessageHelper.setTo(mail.getTo());
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setText(result, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            LOGGER.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("yinaba_meguru@163.com");
        mailSender.setPassword("meguru642328789");
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("utf8");
        Properties mailProperties = new Properties();
//        mailProperties.put("mail.smtp.ssl.enable", true);
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(mailProperties);
    }
}
