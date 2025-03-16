package com.onesion.reggie.dto;

import com.onesion.reggie.entity.Dish;
import com.onesion.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜品信息和口味信息
 * 继承Dish，扩展flavors属性（内部封装DishFlvor）
 */
@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    // 分类名称，菜品分页查询时，通过分类id获取到分类名称
    private String categoryName;

    private Integer copies;
}
