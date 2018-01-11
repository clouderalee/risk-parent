package com.cangoonline.risk.service.impl;

import com.cangoonline.risk.service.SftpService;
import com.jcraft.jsch.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Administrator on 2017\12\11 0011.
 */
public class SftpServiceImpl implements SftpService {
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    /*sftp类型 密码连接*/
    public static final String SFTP_TYPE_PASSWORD = "1";
    /*sftp类型 密钥连接*/
    public static final String SFTP_TYPE_PRIVATEKEY = "2";
    /*默认端口*/
    public static final int SFTP_DEFAULT_PORT = 22;

    private String localPath;
    private String remotePath;
    private String host;
    private String port;

    private String userName;
    private String password;
    private String privateKey;

    private String sftpType = "1";
    private int timeout = 5 * 60 * 1000;

    /* 本地下载文件存放地址 */
    private String baseLocalDownLoadPath = "";

    /* 本地需要上传的文件存放地址 */
    private String baseLocalUploadPath = "";

    /* 从远程的此地址下载指定文件到本地的baseLocalDownLoadPath路径 */
    private String baseRemoteDownLoadPath = "";

    /* 从本地上传到远程，远程存放地址 */
    private String baseRemoteUploadPath = "";

//    private boolean isAddDateLocalPath = false;
//    private boolean isAddDateRemotePath = false;



    @Override
    public boolean getFile() {
        return downloadFile(this.remotePath , this.localPath);
    }

    @Override
    public boolean putFile() {
        return uploadFile(this.localPath , this.remotePath);
    }

    @Override
    public boolean downloadFile(String remotePath, String localPath) {
        remotePath = baseRemoteDownLoadPath + remotePath;
        localPath = getFinalLocalPath(localPath);
        sftpCheck(remotePath,localPath);
        SftpChannel sftpUtil = null;
        InputStream instream = null;
        OutputStream out = null;
        try {
            sftpUtil = new SftpChannel();
            ChannelSftp sftpChannel = getChanelSftp(sftpUtil,sftpType);
            // 以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
            System.out.println("[下载文件]");
            System.out.println("local  path:"+localPath);
            System.out.println("remote path:"+remotePath);
            out = new FileOutputStream(localPath);
            instream = sftpChannel.get(remotePath);
            exchangeStream(instream, out);
            System.out.println("[文件下载成功]");
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResource(sftpUtil, instream, out);
        }
    }


    @Override
    public boolean uploadFile(String localPath, String remotePath) {
        localPath = baseLocalUploadPath + localPath;
        SftpChannel sftpUtil = null;
        InputStream instream = null;
        OutputStream out = null;
        try {
            sftpUtil = new SftpChannel();
            ChannelSftp sftpChannel = getChanelSftp(sftpUtil,sftpType);
            remotePath = getFinalRemotePath(sftpChannel,remotePath);
            System.out.println("[上传文件]");
            System.out.println("local  path:"+localPath);
            System.out.println("remote path:"+remotePath);
            out = sftpChannel.put(remotePath);
            instream = new FileInputStream(localPath);
            exchangeStream(instream, out);
            System.out.println("[文件上传成功]");
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResource(sftpUtil,instream,out);
        }
    }

    /**
     * 最大化程度使本地路径存在
     * @param localPath
     * @return
     */
    private String getFinalLocalPath(String localPath) {
        String finalLocalPath = this.baseLocalDownLoadPath;

        //0.没有配置基础路径时，说明用户使用完整路径
        if(isEmpty(this.baseLocalDownLoadPath)){
            createLocalFile(localPath);
            return localPath;
        }else{
            String tempDirStr;
            String tempFileNameStr;
            if(localPath.contains("/")){
                tempDirStr = localPath.substring(0,localPath.lastIndexOf("/")+1);
                tempFileNameStr = localPath.substring(localPath.lastIndexOf("/")+1);
                finalLocalPath += tempDirStr;
            }else{
                //localPath 是一个文件
                tempFileNameStr = localPath;
            }
            finalLocalPath += tempFileNameStr;
        }
        System.out.println(finalLocalPath);
        //确保文件存在
        createLocalFile(finalLocalPath);
        return finalLocalPath;
    }


    /**
     * 最大程度在远程服务器创建目录
     * @param sftpChannel
     * @param remotePath
     * @return
     */
    private String getFinalRemotePath(ChannelSftp sftpChannel, String remotePath) {

        String finalRemotePath = this.baseRemoteUploadPath;

        //0.没有配置基础路径时，说明用户使用完整路径
        if(isEmpty(this.baseRemoteUploadPath)){
            //创建文件路径
            createRemoteFile(sftpChannel,remotePath);
            return remotePath;
        }else{
            String tempDirStr;
            String tempFileNameStr;
            if(remotePath.contains("/")){
                tempDirStr = remotePath.substring(0,remotePath.lastIndexOf("/")+1);
                //localPath 是一个文件
                tempFileNameStr = remotePath.substring(remotePath.lastIndexOf("/")+1);
                finalRemotePath += tempDirStr;
            }else{
                tempFileNameStr = remotePath;
            }
            finalRemotePath += tempFileNameStr;
        }
        //确保远程目录存在
        createRemoteFile(sftpChannel,finalRemotePath);
        return finalRemotePath;
    }

    /**
     * 确保文件存在
     * @param filePath
     */
    private void createLocalFile(String filePath){

        //1.确保本地路径存在
        String tempDirPath = filePath.substring(0,filePath.lastIndexOf("/"));
        File dir = new File(tempDirPath);
        if(!(dir.exists()&&dir.isDirectory())){
            dir.mkdirs();
            logger("sftp本地路径["+tempDirPath+"]创建成功...");
        }

        //2.确保文件存在，保证流交换时正常
        File file = new File(filePath);
        if(!(file.exists()&&file.isFile())){
            try {
                file.createNewFile();
                logger("sftp本地文件["+filePath+"]创建成功...");
            } catch (IOException e) {
                e.printStackTrace();
                logger("创建sftp本地文件["+filePath+"]失败...");
            }
        }
    }

    /**
     * 创建远程目录
     * @param sftpChannel
     * @param filePath
     */
    private void createRemoteFile(ChannelSftp sftpChannel, String filePath){
        //1.创建文件路径
        String tempDirPath = filePath.substring(0,filePath.lastIndexOf("/"));
        try {
            sftpChannel.cd(tempDirPath);
        } catch (SftpException e) {
            logger("sftp服务器["+host+"]不存在文件夹:"+tempDirPath+" , 程序自动创建中...");
            //文件夹不存在
            try {
                sftpChannel.cd( "/" );
                String[] folders = tempDirPath.split("/");
                for ( String folder : folders ) {
                    if (folder.length() > 0 ) {
                        try {
                            sftpChannel.cd( folder );
                        }catch ( SftpException ex ) {
                            sftpChannel.mkdir( folder );
                            sftpChannel.cd( folder );
                        }
                    }
                }
            } catch (SftpException x) {
                x.printStackTrace();
                logger("文件夹:"+tempDirPath+" , 不是一个有效的目录路径或者没有权限，目录创建失败...");
            }
        }

        //2.确保文件存在
        //sftpChannel 自动创建覆盖，无须处理
    }



    /**
     * 检查参数
     * @param remotePath
     * @param localPath
     * @return
     */
    private boolean sftpCheck(String remotePath, String localPath) {
        if(existEmpty(host, remotePath, localPath)){
            return false;
        }
        if(SFTP_TYPE_PASSWORD.equals(sftpType)){
            if(existEmpty(userName,password)){
                return false;
            }
        }else{
            if(existEmpty(privateKey)){
                return false;
            }
        }
        //检查本地文件是否存在

        return true;
    }

    /**
     * 获取日期路径字符串 yyyy/MM/dd -> "2017/12/13"
     * @param date
     * @param isAddDatePath
     * @return
     */
    private String getDatePath(Date date, boolean isAddDatePath) {
        String datePath = null;
        if(isAddDatePath){
            if(date == null) {
                return "";
            }
            datePath = sf.format(date);
        }else{
            datePath = "";
        }
        return datePath;
    }

    private boolean existEmpty(Object ... array){
        if(array==null||array.length<=0){
            return true;
        }
        for (int i = 0; i < array.length; i++) {
            if(isEmpty(array[i])){
                return true;
            }
        }
        return false;

    }
    private boolean isEmpty(Object object){
        return object==null||"".equals(object);
    }


    /**
     * 初始化ChanelSftp
     * @param sftpUtil
     * @return
     * @throws JSchException
     */
    private ChannelSftp getChanelSftp(SftpChannel sftpUtil,String sftpType) throws JSchException {
        return sftpUtil.getChannel(
                localPath,remotePath,host,port,userName,password,privateKey,sftpType,timeout);
    }

    /**
     * 交换流内容
     * @param instream
     * @param out
     * @throws IOException
     */
    private void exchangeStream(InputStream instream, OutputStream out) throws IOException {
        byte[] buff = new byte[1024 * 2];
        int read;
        if (instream != null) {
            logger("Start to read input stream");
            do {
                read = instream.read(buff, 0, buff.length);
                if (read > 0) {
                    out.write(buff, 0, read);
                }
                out.flush();
            } while (read >= 0);
            logger("input stream read done.");
        }
    }

    private void closeResource(SftpChannel sftpUtil, InputStream instream, OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
            if (instream != null) {
                instream.close();
            }
            if (sftpUtil != null) {
                sftpUtil.closeChannel();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String pathReplace(String path) {
        if(isEmpty(path))
            return path;
        path = path.replace("\\","/");
        if(!path.endsWith("/")){
            path += "/";
        }
        return path;
    }

    private void logger(String message) {
        System.out.println(message);
    }


    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
        if(!isEmpty(localPath)){
            this.localPath = localPath.replace("\\","/");
        }
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        if(!isEmpty(remotePath)){
            this.remotePath = remotePath.replace("\\","/");
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getSftpType() {
        return sftpType;
    }

    public void setSftpType(String sftpType) {
        this.sftpType = sftpType;
    }

    public String getBaseRemoteUploadPath() {
        return baseRemoteUploadPath;
    }

    public void setBaseRemoteUploadPath(String baseRemoteUploadPath) {
        this.baseRemoteUploadPath = pathReplace(baseRemoteUploadPath);;
    }
//
//    public boolean isAddDateLocalPath() {
//        return isAddDateLocalPath;
//    }
//
//    public void setAddDateLocalPath(boolean addDateLocalPath) {
//        isAddDateLocalPath = addDateLocalPath;
//    }
//
//    public boolean isAddDateRemotePath() {
//        return isAddDateRemotePath;
//    }
//
//    public void setAddDateRemotePath(boolean addDateRemotePath) {
//        isAddDateRemotePath = addDateRemotePath;
//    }

    public String getBaseLocalDownLoadPath() {
        return baseLocalDownLoadPath;
    }


    public void setBaseLocalDownLoadPath(String baseLocalDownLoadPath) {
        this.baseLocalDownLoadPath = pathReplace(baseLocalDownLoadPath);
    }

    public String getBaseLocalUploadPath() {
        return baseLocalUploadPath;
    }

    public void setBaseLocalUploadPath(String baseLocalUploadPath) {
        this.baseLocalUploadPath = pathReplace(baseLocalUploadPath);
    }

    public String getBaseRemoteDownLoadPath() {
        return baseRemoteDownLoadPath;
    }

    public void setBaseRemoteDownLoadPath(String baseRemoteDownLoadPath) {
        this.baseRemoteDownLoadPath = pathReplace(baseRemoteDownLoadPath);
    }
}

class SftpChannel {
    private Channel channel;
    private Session session;

    public ChannelSftp getChannel(String sourcePath, String remotePath, String ftpHost, String port,
                                  String ftpUserName, String ftpPassword, String ftpPrivateKey, String ftpType,
                                  int timeout)
            throws JSchException {

        int ftpPort = SftpServiceImpl.SFTP_DEFAULT_PORT;
        if (port != null && !port.equals("")) {
            ftpPort = Integer.valueOf(port);
        }

        JSch jsch = new JSch(); // 创建JSch对象
        if(SftpServiceImpl.SFTP_TYPE_PRIVATEKEY.equals(ftpType)) {
            jsch.addIdentity(ftpPrivateKey);
        }

        session = jsch.getSession(ftpUserName, ftpHost, ftpPort); // 根据用户名，主机ip，端口获取一个Session对象
        System.out.println("Session created.");
        if(SftpServiceImpl.SFTP_TYPE_PASSWORD.equals(ftpType) && ftpPassword != null) {
            session.setPassword(ftpPassword); // 设置密码
        }

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config); // 为Session对象设置properties
        session.setTimeout(timeout); // 设置timeout时间
        session.connect(); // 通过Session建立链接
        System.out.println("Session connected.");

        System.out.println("Opening Channel.");
        channel = session.openChannel("sftp"); // 打开SFTP通道
        channel.connect(); // 建立SFTP通道的连接
        System.out.println("Connected successfully to ftpHost = " + ftpHost
                + ",as ftpUserName = " + ftpUserName + ", returning: "
                + channel);
        return (ChannelSftp) channel;
    }

    public void closeChannel() throws Exception {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }
}


