package com.kou.controller;

import com.kou.pojo.FastDfsFile;
import com.kou.utils.FastDfsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author JIAJUN KOU
 */
@Controller
public class FastDfsController {

    private static  Logger logger=LoggerFactory.getLogger(FastDfsController.class);

    @PostMapping("upload")
    public String FileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        if(file.isEmpty()){
            redirectAttributes.addFlashAttribute("message","请选择一个文件上传");
            return "redirect:uploadStatus";
        }
        try {
            //上传文件拿到返回文件为路径
            String path = saveFile(file);
            redirectAttributes.addFlashAttribute("message","上传成功"+file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("path","路径"+path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:uploadStatus";
    }


    @GetMapping("/uploadStatus")
    public String uploadStatus(){
        return "uploadStatus";
    }

    @GetMapping("/")
    public String upload(){
        return "upload";
    }

    public String saveFile(MultipartFile file) throws Exception {
        String[] fileAbsolutePath={};
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff=null;
        InputStream inputStream = file.getInputStream();
        if(inputStream!=null){
            int len1 = inputStream.available();
            file_buff=new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDfsFile fastDfsFile=new FastDfsFile(fileName,file_buff,ext);
        fileAbsolutePath= FastDfsClient.upload(fastDfsFile);
        if(fileAbsolutePath==null){
            logger.error("上传失败");
        }
        String path=FastDfsClient.getTrackerUrl()+fileAbsolutePath[0]+"/"+fileAbsolutePath[1];
        return path;
    }

}
