package com.kidd.uber_eat_demo.controller;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kidd.uber_eat_demo.common.R;
import com.kidd.uber_eat_demo.entity.User;
import com.kidd.uber_eat_demo.service.UserService;
import com.kidd.uber_eat_demo.utils.SMSUtils;
import com.kidd.uber_eat_demo.utils.ValidateCodeUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * 
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}", code);
            // 调用阿里云提供的短信服务API完成发送短信
            // SMSUtils.sendMessage("uber_eat", "", phone, code);

            // 需要将生成的验证码保存到Session
            // session.setAttribute(phone, code);

            // 需要将生成的验证码保存在Redis中，设置过期时间--5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * 
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString()); // map 封装phone code

        // 获取手机号
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = (String) map.get("code").toString();

        // 从Session中获取保存的验证码
        // Object codeInSession = session.getAttribute(phone);

        // 从Redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)) {
            // 如果能够比对成功，说明登录成功

            // 查询用户信息，返回给界面
            LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class);
            queryWrapper.eq(StringUtils.isNotEmpty(phone), User::getPhone, phone);

            User user = userService.getOne(queryWrapper);

            // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            // ? 疑问，没有进行再次查询如何返回id的，是save方法返回的吗
            session.setAttribute("user", user.getId());

            // 从Redis中删除缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登录失败");
    }

}
