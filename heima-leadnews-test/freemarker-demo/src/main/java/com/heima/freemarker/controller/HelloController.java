package com.heima.freemarker.controller;

import com.heima.freemarker.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author 12141
 */
@Controller
public class HelloController {

    @GetMapping("/basic")
    public String testHello(Model model){

        //1、纯文本形式的参数
        model.addAttribute("name",null);
        //2、实体类相关的参数

        Student student = new Student();
        student.setName("刘亦菲");
        student.setAge(33);
        model.addAttribute("stu",student);

        return "hello";
    }


    @GetMapping("/list")
    public String list(Model model){
        //------------------------------------
        Student stu1 = new Student();
        stu1.setName("小强");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        //小红对象模型数据
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);

        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        //向model中存放List集合数据
        model.addAttribute("stus",stus);

        //------------------------------------

        //创建Map数据
        HashMap<String,Student> stuMap = new HashMap<>();
        stuMap.put("stu1",stu1);
        stuMap.put("stu2",stu2);
        // 3.1 向model中存放Map数据
        model.addAttribute("stuMap", stuMap);

        //添加日期数据
        model.addAttribute("now",new Date());
        //内建函数
        model.addAttribute("point", 17890123568212L);
        return "02-list";
    }

    @GetMapping("/operation")
    public String testOperation(Model model) {
        //构建 Date 数据
        Date now = new Date();
        model.addAttribute("date1", now);
        model.addAttribute("date2", now);
        return "03-operation";
    }

}
