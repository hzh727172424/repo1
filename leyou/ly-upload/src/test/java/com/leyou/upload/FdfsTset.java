package com.leyou.upload;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FdfsTset {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;
    @Test
    public void testUpload()throws FileNotFoundException{
        File file =new File("D:\\无极产品目录(电子档)\\1.jpg");
        //上传并生成缩略图
        StorePath storePath =this.storageClient.uploadFile(
                new FileInputStream(file),file.length() ,"jpg" ,null );
        //带分组的路径
        System.out.println(storePath.getFullPath());
        //不带分组的路径
        System.out.println(storePath.getPath());
    }
    @Test
    public void testUploadAndCreateThumb()throws  FileNotFoundException{
        File file =new File("D:\\test\\baby.png");
        StorePath storePath=this.storageClient.uploadImageAndCrtThumbImage(
                new FileInputStream(file),file.length() ,"png" ,null );
        System.out.println(storePath.getFullPath());
        System.out.println(storePath.getPath());
        String thumbImagePath = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println(thumbImagePath);
    }
}
