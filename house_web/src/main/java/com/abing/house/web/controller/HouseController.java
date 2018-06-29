package com.abing.house.web.controller;

import com.abing.house.biz.mapper.HouseMapper;
import com.abing.house.biz.service.AgencyService;
import com.abing.house.biz.service.CommentService;
import com.abing.house.biz.service.HouseService;
import com.abing.house.biz.service.RecommendService;
import com.abing.house.common.constants.CommonConstants;
import com.abing.house.common.model.*;
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

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private CommentService commentService;

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
        PageData<House> ps=houseService.queryHouse(house,PageParams.build(pageSize,pageNum));
        modelMap.put("ps",ps);
        modelMap.put("vo",house);
        return "house/listing";
    }

    /**
     * 查看房屋详情
     * 查看关联经纪人
     * @param id
     * @return
     */
    @RequestMapping("house/detail")
    public String houseDetail(Long id,ModelMap modelMap){
        House house=houseService.queryOneHouse(id);
        HouseUser houseUser=houseService.getHouseUser(id);
        recommendService.increase(id);
        List<Comment> comments=commentService.getHouseComments(id,8);
        if (houseUser.getUserId()!=null&&!houseUser.getUserId().equals(0)){
            modelMap.put("agent",agencyService.getAgentDeail(houseUser.getUserId()));
        }
        List<House> rcHouses=recommendService.getHotHouse(CommonConstants.RECOM_SIZE);
        modelMap.put("recomHouses", rcHouses);
        modelMap.put("house", house);
        modelMap.put("commentList", comments);
        return "/house/detail";
    }

    /**
     *留言
     * @param userMsg
     * @return
     */
    @RequestMapping("house/leaveMsg")
    public String houseMsg(UserMsg userMsg){
        houseService.addUserMsg(userMsg);
        return "redirect:/house/detail?id="+userMsg.getHouseId();
    }

}
