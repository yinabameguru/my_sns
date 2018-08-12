package com.jza.service;

import com.jza.async.EventModel;
import com.jza.async.EventProducer;
import com.jza.async.EventType;
import com.jza.model.Mail;
import com.jza.model.User;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    FreeMarkerConfig freeMarkerConfig;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    UserService userService;

    public boolean sendWithHTMLTemplate(Mail mail) {
        try {
            String nick = MimeUtility.encodeText("meguru");
            InternetAddress from = new InternetAddress(nick + "<yinaba_meguru@163.com>");
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
            e.printStackTrace();
            return false;
        }
    }

    public void fireMailEvent(String to, String subject, String template, Map<String, Object> mailModel){
        EventModel eventModel = new EventModel(EventType.MAIL);
        Mail mail = new Mail();
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setTemplate(template);
        mail.setModel(mailModel);
        eventModel.set("mail", mail);
        eventProducer.fireEvent(eventModel);
    }

//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        mailSender = new JavaMailSenderImpl();
//        mailSender.setUsername("764904418@qq.com");
//        mailSender.setPassword("ymooxwpjyknkbbdj");
//        mailSender.setHost("smtp.qq.com");
//        mailSender.setPort(465);
//        mailSender.setProtocol("smtps");
//        mailSender.setDefaultEncoding("utf8");
//        Properties mailProperties = new Properties();
//        mailProperties.put("mail.smtp.starttls.enable", "true");
////        mailProperties.put("mail.smtp.auth", "true");
////        mailProperties.put("mail.smtp.ssl.enable","true");
//        mailSender.setJavaMailProperties(mailProperties);
//
//
////        mailSender = new JavaMailSenderImpl();
////        mailSender.setUsername("yinaba_meguru@foxmail.com");
////        mailSender.setPassword("bgmcmfmvtolobehg");
////        mailSender.setHost("smtp.qq.com");
////        mailSender.setPort(465);
////        mailSender.setProtocol("smtps");
////        mailSender.setDefaultEncoding("utf8");
////        Properties javaMailProperties = new Properties();
////        javaMailProperties.put("mail.smtp.ssl.enable", true);
////        mailSender.setJavaMailProperties(javaMailProperties);
//
//
//    }
}
