package com.example.productadmin;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import io.minio.RemoveObjectArgs;

import lombok.extern.log4j.Log4j2;
import org.dromara.web.utils.MinioUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.UUID;

//@SpringBootTest(classes = LdcmsAdminApplication.class)
@Log4j2
class PictureTests {

    @Test
    public void insertProductPicture() {
        String pictureAddress = "D:/Work/test.docx";
        // 使用 "\\/" 来分割字符串，因为 "/" 在正则表达式中是特殊字符，需要用 "\\" 来转义
        String[] split = pictureAddress.split("\\/");
        // 使用 pictureName.length - 1 来获取数组的最后一个元素的索引
        String pictureName = split[split.length - 1];
        String[] split2 = pictureName.split("\\.");
        String type = split2[1];
        String minioName = "test";
        FileInputStream fileInputStream = null;
        LocalDate today = LocalDate.now();
        // 生成UUID作为文件名的一部分
        String uniqueId = UUID.randomUUID().toString();
        String minioFileName = uniqueId + "_" + pictureName; // originalFileName 是原始文件名
        try {

            fileInputStream = new FileInputStream(pictureAddress);

            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000/").build();


            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .object(today.getYear() + "/" + today.getMonthValue() + "/" + today.getDayOfMonth() + "/" + minioFileName)//文件名
                .contentType("image/" + type)//文件类型
                .bucket("test")//桶名词  与minio创建的名词一致
                .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                .build();
            minioClient.putObject(putObjectArgs);


            String str = "http://192.168.200.130:9000/" + minioName + "/" + today.getYear() + "/" + today.getMonthValue() + "/" + today.getDayOfMonth() + "/" + minioFileName;
            System.out.println(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void test() {
        String pictureAddress = "D:/College/PS/游戏人物场景合成 素材/R-C.jpg";
        // 使用 "\\/" 来分割字符串，因为 "/" 在正则表达式中是特殊字符，需要用 "\\" 来转义
        String[] split = pictureAddress.split("\\/");
        // 使用 pictureName.length - 1 来获取数组的最后一个元素的索引
        String pictureName = split[split.length - 1];
        System.out.println(pictureName);
        String[] split2 = pictureName.split("\\.");
        String type = split2[1];
        System.out.println(type);

    }

    @Test
    public void test2() {
        String productListVo = "D:/College/PS/游戏人物场景合成 素材/R-C.jpg,D:/College/PS/游戏人物场景合成 素材/OIP-C.jpg,D:/College/PS/游戏人物场景合成 素材/OIP-C (2).jpg";
        String[] split = productListVo.split(",");
        for (int i = 0; i < split.length; i++) {
            String pictureAddress = MinioUtils.upLoadToMinio(split[i]);
            System.out.println(pictureAddress);
        }

    }

    //删除文件：
    @Test
    public void delete() {
        /**
         * String bucketName = "test";
         * String fileName = "2024/8/9/be78ea85-67d4-46ab-9dba-b09605d2ce76_OIP-C.jpg";
         * address+bucketName+fileName 就是访问路径，删除需要后两个参数。
         */
        String bucketName = "test";
        String fileName = "2024/8/9/be78ea85-67d4-46ab-9dba-b09605d2ce76_OIP-C.jpg";
        try {
            getMinioClient().removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            System.out.println("删除失败");
        }
        System.out.println("删除成功");
    }


    //获取操作对象：
    public MinioClient getMinioClient() {
        MinioClient minioClient = null;
        String address = "http://192.168.200.130:9000/";
        String username = "minio";
        String password = "minio123";
        if (minioClient == null) {
            minioClient = MinioClient.builder()
                .endpoint(address)
                .credentials(username, password)
                .build();
        }
        return minioClient;
    }

}
