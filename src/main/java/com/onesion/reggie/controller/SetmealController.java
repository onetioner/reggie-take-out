package com.onesion.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onesion.reggie.common.R;
import com.onesion.reggie.dto.SetmealDto;
import com.onesion.reggie.entity.Category;
import com.onesion.reggie.entity.Setmeal;
import com.onesion.reggie.service.CategoryService;
import com.onesion.reggie.service.SetmealDishService;
import com.onesion.reggie.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto 用来接收前端传递过来的参数Setmeal SetmealDish SetmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    @ApiOperation(value = "新增套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto) {

        log.info("套餐信息：{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
    })
    public R<Page> page(int page, int pageSize, String name) {

        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);  // 套餐Setmeal分页
        Page<SetmealDto> dtoPage = new Page<>();  // SetmealDto分页 多了 菜品信息、套餐分类名称

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);  // 分页查询套餐

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();  // 拿到套餐信息（描述套餐的信息）集合

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);

            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            // 这里就是拿到每一个套餐信息，获取套餐分类id，然后查询分类信息获取套餐分类名称，设置到SetmealDto中
            // 并没有设置套餐关联的菜品信息，因为不需要显示

            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐 单个删除和批量删除都可以
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "套餐删除接口")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids) {

        log.info("ids:{}", ids);

        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    /**
     * 根据id查询套餐详情信息
     * 修改套餐中，套餐详情数据回显
     */
    @GetMapping("{id}")
    public R<SetmealDto> findById(@PathVariable Long id) {

        SetmealDto setmealDto = setmealService.findById(id);

        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {

        setmealService.updateWithDish(setmealDto);

        return R.success("修改成功");
    }


    /**
     * 根据分类id查询套餐列表
     * 套餐分类id，因为要在移动端显示套餐分类，和其中套餐列表
     * 根据条件查询套餐数据  day_06菜品展示  Redis缓存 SpringCache注解方式
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")  // 加入这个Cacheable注解。这个key跟当前查询条件有关系，可以是id和status
    @GetMapping("list")
    @ApiOperation(value = "套餐条件查询接口")
    public R<List<Setmeal>> list(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
