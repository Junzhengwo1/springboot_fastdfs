package com.kou.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


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
            logger.error("Fasrdfs Client初始化失败",e);
        }
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
