package com.example.uber_eat_demo.uber_eat_demo;

import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test1() {
        String fileName = "abc.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
