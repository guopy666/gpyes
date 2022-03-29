package com.gpy.gpyes.jddemo.controller;

import com.gpy.gpyes.jddemo.service.JdEsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName JdEsController
 * @Description
 * @Author guopy
 * @Date 2022/3/28 15:54
 */
@RestController
@RequestMapping
public class JdEsController {

    @Autowired
    private JdEsService jdEsService;



    @GetMapping("parse/{keywords}")
    public Boolean parse(@PathVariable("keywords") String keywords) throws IOException {
        Boolean aBoolean = jdEsService.parseContent(keywords);
        return  aBoolean;
    }


    @GetMapping("pageSearch/{keywords}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> pageSearch(@PathVariable("keywords") String keywords,
                                                @PathVariable("pageNo") Integer pageNo,
                                                @PathVariable("pageSize") Integer pageSize) throws IOException {
        List<Map<String, Object>> list = jdEsService.searchPage(keywords, pageNo, pageSize);
        return list;
    }




}
