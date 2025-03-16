package com.onesion.reggie.dto;

import com.onesion.reggie.entity.Setmeal;
import com.onesion.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

/**
 * 继承套餐表
 * 扩展套餐菜品属性，内部封装套餐包含的菜品
 */
@Data
public class SetmealDto extends Setmeal {

    // 接收页面传递的套餐关联的菜品列表
    private List<SetmealDish> setmealDishes;

    // 应该是用来显示套餐分类名称 没错
    private String categoryName;
}
