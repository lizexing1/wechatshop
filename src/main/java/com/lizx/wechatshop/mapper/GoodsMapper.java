package com.lizx.wechatshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsMapper {

    @Select("select * from menu m where m.pid = #{pid}")
    public List<Map<String,Object>> getByParent(@Param("pid")String pid);

}
