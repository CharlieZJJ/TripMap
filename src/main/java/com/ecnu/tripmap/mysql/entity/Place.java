package com.ecnu.tripmap.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("place")
public class Place {
//    @TableId(type = IdType.AUTO)
//    private Long id;

    //    @TableField("place_id")
    @TableId(type = IdType.AUTO)
    private Integer placeId;

    @TableField("place_province")
    private String placeProvince;

    @TableField("place_area")
    private String placeArea;

    @TableField("place_address")
    private String placeAddress;
}
