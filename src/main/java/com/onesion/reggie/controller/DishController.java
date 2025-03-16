package com.onesion.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onesion.reggie.common.R;
import com.onesion.reggie.dto.DishDto;
import com.onesion.reggie.entity.Category;
import com.onesion.reggie.entity.Dish;
import com.onesion.reggie.entity.DishFlavor;
import com.onesion.reggie.service.CategoryService;
import com.onesion.reggie.service.DishFlavorService;
import com.onesion.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 根据条件查询对应的菜品数据  就是根据菜品分类id，查询菜品
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//
//        //添加条件，查询状态为1（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus, 1);
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);  // 菜品分页查询条件
        Page<DishDto> dishDtoPage = new Page<>();  // 菜品 口味 分页查询条件

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        // 数据处理
        List<Dish> records = pageInfo.getRecords();  // 拿到菜品分页数据
        List<DishDto> list = records.stream().map((item) -> {  // 把每一个菜品信息拷贝到DishDto

            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); //分类id

            Category category = categoryService.getById(categoryId);  // 通过分类id查询分类信息

            if(category != null) {
                String categoryName = category.getName();  // 拿到菜品分类的名字 川菜、粤菜啥的
                dishDto.setCategoryName(categoryName);  // 设置到DishDto中
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * 菜品、口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 套餐管理中的方法  新增套餐里面根据菜品分类id（前端传过来的）查询菜品的方法 如上
     * @param dish Dish来包装前端传过来的菜品分类id
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        List<DishDto> dishDtoList = null;

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // 这里应该是查询菜品信息，但是没有口味信息 没错
        // 查询的是等于前端传过来菜品分类id的所有菜品信息，菜品表dish中查询的
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {

            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); // 菜品分类id
            Category category = categoryService.getById(categoryId);  // 通过菜品分类id拿到分类
            if(category != null) {
                String categoryName = category.getName();  // 通过分类拿到 分类名称 ，估计是要显示的分类名称，DishDto里面有categoryName字段
                dishDto.setCategoryName(categoryName);  // 就跟之前分页查询菜品时候要显示的分类名称
            }
            /**
             * 通过菜品分类id查询分类下的菜品信息，遍历每一个菜品信息，
             * 拿到菜品分类id，通过菜品分类id拿到分类信息，取出分类名称设置到DishDto
             */

            //当前菜品的id
            Long dishId = item.getId();  // 拿到当前菜品的id，因为要查当前菜品的口味信息

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            //SQL: select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            // 这里拿到了菜品口味信息！！！！移动端就能用上了
            dishDto.setFlavors(dishFlavorList);
            /**
             * 通过菜品分类id查询分类下的菜品信息，遍历每一个菜品信息，
             * 拿到菜品id，通过菜品id拿到菜品口味信息，设置DishDto中菜品口味信息
             */

            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

}
