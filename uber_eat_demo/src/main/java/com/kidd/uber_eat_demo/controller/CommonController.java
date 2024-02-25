package com.kidd.uber_eat_demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kidd.uber_eat_demo.common.R;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${uber_eat.Path}")
    private String basePath;

    /**
     * 文件上传
     * 注意三要素：post、multipart/form-data、file
     * 
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        // 原始文件名
        String originalFilename = file.getOriginalFilename();// abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String filename = UUID.randomUUID().toString() + suffix;// dfsdfdfd.jpg

        // 创建一个目录对象
        File dir = new File(basePath);
        // 判断当前目录是否存在
        if (!dir.exists()) {
            // 目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            // 将临时文件转存到指定位置
            // 将基础路径和文件名拼接起来，创建表示文件路径的File对象
            File targetFile = new File(dir + File.separator + filename);
            file.transferTo(targetFile);
        } catch (IOException e) {
            // 打印异常的堆栈跟踪信息
            e.printStackTrace();
        }

        return R.success(filename);
    }

    /**
     * 文件下载
     * 
     * @param param
     */
    @GetMapping("/download")
    // 输出流通过response获得
    public void download(String name, HttpServletResponse response) {

        try {
            // 输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + File.separator + name));
            // 输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");

            // 使用工具类
            // Apache Commons IO 库中提供的一个便捷方法，用于将输入流中的数据复制到输出流中。
            // 这个方法的目的是为了简化数据复制的操作，避免开发者自己编写复制数据的代码
            IOUtils.copy(fileInputStream, outputStream);

            // 输入流向输出流进行拷贝
            // int len = 0;
            // byte[] bytes = new byte[1024];
            // while ((len = fileInputStream.read(bytes)) != -1) {
            // outputStream.write(bytes, 0, len);
            // outputStream.flush();
            // }

            // 关闭资源
            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
