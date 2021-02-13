package com.kou.utils;

import com.kou.pojo.FastDfsFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author JIAJUN KOU
 *
 * FastDfs工具类
 */
public class FastDfsClient {

    private static Logger logger= LoggerFactory.getLogger(FastDfsClient.class);

    //ClientGlobal.init方法会读取配置文件
    static {
        try {
            String filePath=new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
            ClientGlobal.init(filePath);
        } catch (Exception e) {
            logger.error("FastDfs Client初始化失败",e);
        }
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    public static String[] upload(FastDfsFile file) {
        logger.info("File Name"+file.getName()+",File length"+file.getContent().length);
        NameValuePair[] meta_list=new NameValuePair[1];
        meta_list[0]=new NameValuePair("author",file.getAuthor());
        long startTime=System.currentTimeMillis();
        String[] uploadResults=null;
        StorageClient storageClient=null;
        try {
            //获取Storage客户端
             storageClient= getStorageClient();
             uploadResults=storageClient.upload_file(file.getContent(),file.getExt(),meta_list);
        } catch (Exception e) {
             logger.error("上传失败，File Name"+file.getName(),e);
        }
        logger.info("上传时间"+(System.currentTimeMillis()-startTime)+"ms");
        //验证上传结果
        if(uploadResults==null&&storageClient!=null){
            logger.error("上传失败"+storageClient.getErrorCode());
        }
        //上传成功响应响应结果
        logger.info("上传成功，group_name"+uploadResults[0]+",remoteFileName:"+uploadResults[1]);
        return uploadResults;
    }

    /**
     * 下载文件
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static InputStream downFile(String groupName,String remoteFileName){
        try {
            StorageClient storageClient = getStorageClient();
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            InputStream ins=new ByteArrayInputStream(fileByte);
            return ins;
        } catch (Exception e) {
            logger.error("下载失败",e);
        }
        return null;
    }

    /**
     * 删除文件
     * @param groupName
     * @param remoteFileName
     */
    public static void deleteFile(String groupName,String remoteFileName){

        try {
            StorageClient storageClient = getStorageClient();
            int i = storageClient.delete_file(groupName, remoteFileName);
            logger.info("删除成功"+i);
        } catch (Exception e) {
            logger.error("删除失败",e);
        }
    }


    public static FileInfo getFile(String groupName,String remoteFileName){
        try {
            StorageClient storageClient = getStorageClient();
            return storageClient.get_file_info(groupName,remoteFileName);
        } catch (Exception e) {
            logger.error("查看文件失败",e);
        }
        return null;
    }

    /**
     * 获取文件路径
     * @return
     * @throws IOException
     */
    public static String getTrackerUrl() throws Exception {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
        return "http://"+storeStorage.getInetSocketAddress().getHostString()+":80/";
    }

    /**
     * 生成tracker服务器端
     * @return
     * @throws IOException
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient=new TrackerClient();
        return trackerClient.getTrackerServer();
    }

    /**
     * 生成Storage可客户端
     * @return
     * @throws IOException
     */
    private static StorageClient getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        return new StorageClient(trackerServer,null);
    }



}
