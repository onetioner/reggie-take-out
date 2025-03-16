package com.onesion.reggie.controller;


import com.onesion.reggie.common.R;
import com.onesion.reggie.entity.User;
import com.onesion.reggie.service.UserService;
import com.onesion.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 移动端用户登录
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {

        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)) {

            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);  // 假设这里验证码，输出在控制台上

            //调用阿里云提供的短信服务API完成发送短信
            // SMSUtils.sendMessage("瑞吉外卖", "", phone, code);

            // 需要将生成的验证码保存到Session
            session.setAttribute(phone, code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信改善失败");
    }


}
