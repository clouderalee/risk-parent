package com.cangoonline.risk.service;

public interface EmailService {
	void sendMail(String subject, String content, String senders);
}
