package com.abing.house.biz.service;

import com.abing.house.biz.mapper.HouseMapper;
import com.abing.house.common.model.Community;
import com.abing.house.common.model.House;
import com.abing.house.common.page.PageData;
import com.abing.house.common.page.PageParams;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Value("${file.prefix}")
    private String imgPrefix;

    /**
     *1、 查询小区
     * 2、添加图片服务器地址前缀
     * 3、构建分页结果
     * @param house
     * @param pageParams
     */
    public PageData<House> queryHouse(House house, PageParams pageParams){
        List<House> houseList=Lists.newArrayList();
        if (!Strings.isNullOrEmpty(house.getName())){
            Community community=new Community();
            community.setName(house.getName());
            List<Community> communities=houseMapper.selectCommunity(community);
            if (!communities.isEmpty()){
                house.setCommunityId(communities.get(0).getId());
            }
        }
        houseList=queryAndSetImg(house,pageParams);
        Long count=houseMapper.selectPageCount(house);
        return PageData.buildPage(houseList,count,pageParams.getPageSize(),pageParams.getPageNum());
    }

    /**
     * 添加图片服务器前缀
     * @return
     */
    public List<House> queryAndSetImg(House house,PageParams pageParams){
        List<House> houses=houseMapper.selectPageHouses(house,pageParams);
        houses.forEach(house1 -> {
            house1.setFirstImg(imgPrefix+house1.getFirstImg());
            house1.setImageList(house1.getImageList().stream().map(img->imgPrefix+img).collect(Collectors.toList()));
            house1.setFloorPlanList(house1.getFloorPlanList().stream().map(img->imgPrefix+img).collect(Collectors.toList()));
        });
        return houses;
    }
}
