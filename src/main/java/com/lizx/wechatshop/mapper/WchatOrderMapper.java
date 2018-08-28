package com.lizx.wechatshop.mapper;

import com.lizx.wechatshop.entity.GoodsOrder;
import com.lizx.wechatshop.entity.WechatOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WchatOrderMapper {

    /**
     * 根据用户id、商品id和订单状态，查询出商品的订单信息
     * @param userId
     * @param goodsId
     * @param status
     * @return
     */
    @Select("SELECT * " +
            " FROM " +
            " ORDER " +
            " WHERE " +
            " user_id = '1'  and goods_id = '1' and status = 0")
    public GoodsOrder getByUserIdAndGoodsIdAndStaus(@Param("userId") String userId,
                       @Param("goodsId") String goodsId,  @Param("status")int status);

    /**
     * 根据商品订单表的Id查询出微信订单表中的数据信息
     * @param id
     * @return
     */
    @Select("select * from wechat_order where order_id = #{id}")
    WechatOrder getByOderId(String id);
}
