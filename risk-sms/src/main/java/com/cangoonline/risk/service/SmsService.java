package com.cangoonline.risk.service;

import java.util.List;

/**
 * Created by Administrator on 2017\12\12 0012.
 */
public interface SmsService {

    void sendSms(String content, String phones);

    void sendSms(String content, String... phones);

    void sendSms(String content, List<String> phones);

    boolean send(String content, String phones) throws Exception;

}
