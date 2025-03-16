package com.onesion.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onesion.reggie.entity.Orders;
import com.onesion.reggie.mapper.OrderMapper;
import com.onesion.reggie.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

}
