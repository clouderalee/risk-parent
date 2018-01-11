package com.cangoonline.risk.service.impl;

import com.cangoonline.risk.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class EmailServiceImpl implements EmailService {

    private MailSender mailSender;
    private SimpleMailMessage simpleMailMessage;

    /**
     * @方法名: sendMail
     * @参数名：@param subject  邮件主题
     * @参数名：@param content 邮件主题内容h
     * @参数名：@param to   收件人Email地址
     * @描述语: 发送邮件
     */
    @Override
    public void sendMail(String subject, String content, String senders) {
        simpleMailMessage.setSubject(subject); //设置邮件主题
        simpleMailMessage.setTo(getEmailAddress(senders));//设定收件人
        simpleMailMessage.setText(content);  //设置邮件主题内容
        try {
            mailSender.send(simpleMailMessage); //发送邮件
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    private String[] getEmailAddress(String senders) {
        if(senders == null) return null;
        String[] arrs = senders.split(",");
        for (int i = 0; i < arrs.length; i++) {
            arrs[i] = arrs[i].trim();
        }
        return arrs;
    }

    //Spring 依赖注入
    public void setSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
        this.simpleMailMessage = simpleMailMessage;
    }

    //Spring 依赖注入
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

}