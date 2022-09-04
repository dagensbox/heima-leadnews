package com.heima.minio.test;


import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.FileInputStream;

public class MinIoTest {

    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("D:\\list.html");

            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder().endpoint("http://192.168.80.1:9000")
                    .credentials("minio", "minio123").build();

            PutObjectArgs putObjectArgs = PutObjectArgs.builder().object("list.html")
                    .contentType("text/html")
                    .bucket("leadnews")
                    .stream(fileInputStream, fileInputStream.available(), -1).build();

            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.80.1:9000/leadnews/list.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
