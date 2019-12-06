package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private UploadProperties prop;
   // private static final List<String> ALLOWTYPE= Arrays.asList();
    public String uploadImage(MultipartFile file){
        try {
        if (!prop.getAllowType().contains(file.getContentType())){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
        BufferedImage read = ImageIO.read(file.getInputStream());
        if (read==null){
            throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
        }
            String  originalFilename= file.getOriginalFilename();
        String extension= StringUtils.substringAfterLast(originalFilename,".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            return  prop.getBaseUrl()+storePath.getFullPath();
        } catch (IOException e) {
            log.error("上传文件失败",e);
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

    }
}
