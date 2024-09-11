package org.dromara.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.web.utils.MinioUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传下载
 * @author zhang
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/upLoadAndDeleteFile")
public class UpLoadAndDeleteFileController {

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String resultFileUrl = MinioUtils.uploadFile(file);
        return resultFileUrl;
    }

    @PostMapping("/deleteFile")
    public String deleteFile(@RequestParam("fileName") String fileName) {
        String fileNameBySplit = fileName.split("/test/")[1];
        String resultMessage = MinioUtils.deleteFile(fileNameBySplit);
        return resultMessage;
    }

}
