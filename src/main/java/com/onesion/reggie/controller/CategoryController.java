package com.onesion.reggie.controller;

import com.onesion.reggie.common.R;
import com.onesion.reggie.entity.Category;
import com.onesion.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 分类管理
 * （菜品分类、套餐分类）
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        log.info("category:{}", category);

        categoryService.save(category);

        return R.success("新增分类成功");
    }
    
}
