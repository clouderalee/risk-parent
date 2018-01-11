package com.cangoonline.risk.service;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public interface SftpService {

    boolean getFile();
    boolean putFile();

    boolean downloadFile(String remotePath , String localPath);
    boolean uploadFile(String localPath , String remotePath);


}
