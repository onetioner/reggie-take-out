package com.onesion.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.onesion.reggie.common.BaseContext;
import com.onesion.reggie.common.R;
import com.onesion.reggie.entity.AddressBook;
import com.onesion.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;


    /**
     * 新增地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {

        addressBook.setUserId(BaseContext.getCurrentId());

        log.info("addressBook:{}", addressBook);

        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {

        log.info("addressBook:{}", addressBook);

        // 先将该用户所有地址的is_default更新为0
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);

        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        // 然后将当前的设置的默认地址的is_default设置为1
        addressBook.setIsDefault(1);

        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {

        AddressBook addressBook = addressBookService.getById(id);

        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }
}
