package com.onesion.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.onesion.reggie.dto.SetmealDto;
import com.onesion.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 根据id查询套餐详情信息，包括套餐关联的菜品集合
     * 修改套餐中套餐详情数据回显
     * @param id
     * @return
     */
    SetmealDto findById(Long id);
    
}
