package com.onesion.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
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

    @Autowired
    private SetmealMapper setmealMapper;

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


    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据  单个和批量
     * @param ids
     *
     * A. 查询该批次套餐中是否存在售卖中的套餐, 如果存在, 不允许删除
     * B. 删除套餐数据
     * C. 删除套餐关联的菜品数据
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //select count(*) from setmeal where id in (1, 2, 3) and status = 1

        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if(count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);


        //然后再删除关系表中的数据---setmeal_dish
        //delete from setmeal_dish where setmeal_id in (1, 2, 3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 根据id查询套餐详情信息，包括套餐关联的菜品集合
     * 修改套餐中套餐详情数据回显
     * @param id
     * @return
     */
    @Override
    public SetmealDto findById(Long id) {
        return setmealMapper.findById(id);
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {

        //1. 更新套餐表数据 setmeal
        this.updateById(setmealDto);

        //2. 更新套餐菜品关联数据 setmeal_dish，先删除再添加
        //delete from setmeal_dish where setmeal_id=?
        setmealDishService.update()
                .eq("setmeal_id", setmealDto.getId())
                .remove();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //TODO 给每一个SetmealDish设置setmealId
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDto.getId()));

        setmealDishService.saveBatch(setmealDishes);
    }
}
