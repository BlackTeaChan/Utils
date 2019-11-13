package com.zjars.jsdc.dto;

import lombok.Data;

/**
 * @author blackteachan
 * @date 2019-11-13 10:49
 */
@Data
public class StudentVO {

    @ExcelVOAttribute(name = "序号", column = "A")
    private Integer id;

    @ExcelVOAttribute(name = "姓名", column = "B", isExport = true)
    private String name;

    @ExcelVOAttribute(name = "年龄", column = "C", prompt = "年龄保密哦!", isExport = false)
    private Integer age;

    @ExcelVOAttribute(name = "班级", column = "D", combo = { "五期提高班", "六期提高班",
            "七期提高班" })
    private String clazz;

    @ExcelVOAttribute(name = "公司", column = "F")
    private String company;

}
