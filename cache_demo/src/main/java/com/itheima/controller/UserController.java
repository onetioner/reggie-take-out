package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private CacheManager cacheManager;  // 使用Spring Cache框架中的

    @Autowired
    private UserService userService;


    /**
     * 新增
     * CachePut：将方法返回值放入缓存
     * value：缓存的名称，每个缓存名称下面可以有多个key
     * key：缓存的key  注意主要是Key的设计，因为缓存多份数据，这些数据不能混了，就需要根据Key去区分，key往往是动态的
     * 例如：动态使用当前用户的id作为key，那怎么获得呢？提供了一种表达式语言，通过这种方式可以动态的获得用户的id
     */
    @CachePut(value = "userCache", key = "#result.id")
    @PostMapping
    public User save(User user){  // 这个地方没有加@RestBody，所以并不需要json格式的数据，普通的表单数据就可以
        userService.save(user);
        return user;
    }


    /**
     * 删除
     * CacheEvict：清理指定缓存
     * value：缓存的名称，每个缓存名称下面可以有多个key
     * key：缓存的key
     */
    @CacheEvict(value = "userCache", key = "#id")  // 这个名字一定跟参数的名称id保持一致
//    @CacheEvict(value = "userCache", key = "#root.args[0]")
//    @CacheEvict(value = "userCache", key = "#p0")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        userService.removeById(id);
    }

    /**
     * 修改时清除缓存数据
     * @param user
     * @return
     */
//    @CacheEvict(value = "userCache", key = "#p0.id") 0代表第一个参数
    @CacheEvict(value = "userCache", key = "#user.id")  // 这属于比较直观的
//    @CacheEvict(value = "userCache", key = "#root.args[0]") 0代表第一个参数  前面这三种写法都是从参数里边来获得值
//    @CacheEvict(value = "userCache", key = "#result.id")  从返回结果中获得id属性值
    @PutMapping
    public User update(User user){
        userService.updateById(user);
        return user;
    }


    /**
     * 通过id查询
     * Cacheable：在方法执行前spring先查看缓存中是否有数据，如果有数据，则直接返回缓存数据；
     *            若没有数据，调用方法并将方法返回值放到缓存中
     * value：缓存的名称，每个缓存名称下面可以有多个key
     * key：缓存的key
     * condition：条件，满足条件时才缓存数据
     * unless：满足条件则不缓存
     */
    @Cacheable(value = "userCache", key = "#id", unless = "#result == null")
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id){
        User user = userService.getById(id);
        return user;
    }


    /**
     * 根据条件查询的方法
     * @param user
     * @return
     */
    @Cacheable(value = "userCache", key = "#user.id + '_' + #user.name")
    @GetMapping("/list")
    public List<User> list(User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(user.getId() != null,User::getId,user.getId());
        queryWrapper.eq(user.getName() != null,User::getName,user.getName());
        List<User> list = userService.list(queryWrapper);
        return list;
    }
}
