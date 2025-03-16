package com.onesion.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onesion.reggie.dto.SetmealDto;
import com.onesion.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    //根据id查询套餐详情信息，包括套餐关联的菜品集合，一对多查询
    SetmealDto findById(Long id);
}
