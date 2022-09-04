package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * @author 12141
 */
@Controller
public class HelloController {

    @GetMapping("/basic")
    public String testHello(Model model){

        //1、纯文本形式的参数
        model.addAttribute("name","freemarker");
        //2、实体类相关的参数

        Student student = new Student();
        student.setName("刘亦菲");
        student.setAge(33);
        student.setBirthday(LocalDate.of(1990,6,1));
        student.setMoney(12000f);

        model.addAttribute("stu",student);

        return "hello";
    }

}
