package com.zjars.jsdc.utils;

import com.zjars.jsdc.dto.StudentVO;
import org.apache.poi.util.IOUtils;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class SuperExcelUtilTest {

    @Test
    public void print(){
        System.out.println(new Double("1.0").intValue());
    }

    @Test
    public void test(){
        FileInputStream fis = null;
        try {
            File file = new File("/Users/blackteachan/Desktop/SuperExcelUtil.xlsx");
            System.out.println(file.getName());
            fis = new FileInputStream(file);
            SuperExcelUtil<StudentVO> util = new SuperExcelUtil<StudentVO>(StudentVO.class);// 创建excel工具类

            List<StudentVO> list = util.importExcel("Sheet1", file2MultipartFile(file));// 导入
            System.out.println(list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * File 转 MultipartFile
     * @param file
     * @throws Exception
     */
    public static MultipartFile file2MultipartFile( File file )  {

        FileInputStream fileInput = null;
        MultipartFile toMultipartFile = null;
        try {
            fileInput = new FileInputStream(file);
            toMultipartFile = new MockMultipartFile("file",file.getName(),
                    "text/plain",
                    IOUtils.toByteArray(fileInput));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return toMultipartFile;

    }

}