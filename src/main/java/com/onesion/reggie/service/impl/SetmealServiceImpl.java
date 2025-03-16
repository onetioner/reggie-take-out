package com.onesion.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onesion.reggie.dto.SetmealDto;
import com.onesion.reggie.entity.Setmeal;
import com.onesion.reggie.entity.SetmealDish;
import com.onesion.reggie.mapper.SetmealMapper;
import com.onesion.reggie.service.SetmealDishService;
import com.onesion.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     *
     * A. 保存套餐基本信息
     * B. 获取套餐关联的菜品集合，并为集合中的每一个元素赋值套餐ID(setmealId)
     * C. 批量保存套餐关联的菜品集合
     *
     * SetmealDto是用来接收前端页面数据的，
     * Setmeal是用来保存套餐基本信息的，
     * SetmealDish是用来保存一个套餐中有哪些菜品的，描述套餐和菜品之间的关系
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        //获取 套餐关联的菜品集合，并为集合中的每一个元素赋值套餐ID(setmealId)
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }
}
