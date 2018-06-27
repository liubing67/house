package com.abing.house.biz.mapper;

import com.abing.house.common.model.Community;
import com.abing.house.common.model.House;
import com.abing.house.common.page.PageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseMapper {

    public List<House> selectPageHouses(@Param("house") House house, @Param("pageParams") PageParams pageParams);

    public Long selectPageCount(@Param("house")House query);

    public int insert(House house);

    List<Community> selectCommunity(Community community);
}
