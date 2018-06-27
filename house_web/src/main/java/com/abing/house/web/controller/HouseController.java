package com.abing.house.web.controller;

import com.abing.house.biz.mapper.HouseMapper;
import com.abing.house.biz.service.HouseService;
import com.abing.house.biz.service.RecommendService;
import com.abing.house.common.model.Community;
import com.abing.house.common.model.House;
import com.abing.house.common.page.PageData;
import com.abing.house.common.page.PageParams;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private RecommendService recommendService;
    /**
     * 获取房屋列表
     * 1、实现分页
     * 2、支持小区收索、类型收索
     * 3、支持排序
     * 4、至此展示图片、价格、标题、地址等信息
     * @param pageSize
     * @param pageNum
     * @param house
     * @param modelMap
     * @return
     */
    @RequestMapping("/house/list")
    public String houseList(Integer pageSize, Integer pageNum, House house, ModelMap modelMap){
        PageData<House> housePageData=houseService.queryHouse(house,PageParams.build(pageSize,pageNum));
//        List<House> hotHouses=
        return "";
    }
}
