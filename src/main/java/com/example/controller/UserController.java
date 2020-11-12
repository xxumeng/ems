package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.VerifyCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin //允许跨域
@RequestMapping("user")
@Slf4j
public class UserController {
    /**
     * 处理用户注册
     */
    @Autowired
    private UserService userService;
    //请求路径，传送的是json对象，需使用@RequestBody转换成对象
    @PostMapping("register")
    public Map<String,Object> register(@RequestBody User user, String code, HttpServletRequest request){

        log.info("用户信息：[{}]", user.toString());
        log.info("验证码信息：[{}]", code);
        Map<String,Object> map = new HashMap();
        //调用service
        try {
            //获取SevletContext作用域的验证码
            String key = (String) request.getServletContext().getAttribute("code");
            log.info("SevletContext作用域的验证码信息：[{}]", key);
            if (key.equalsIgnoreCase(code)){
                log.info("验证码正确");
                userService.register(user);
                map.put("state",true);
                map.put("msg","提示：注册成功");

            }else {
                map.put("state",false);
                map.put("msg","提示：验证码错误");
            }

        }catch (Exception e){
            e.printStackTrace();
            map.put("state",false);
            map.put("msg","提示：注册失败");
        }
        return map;
    }

    /*
     * 用户登陆
     * */
    @PostMapping("login")
    public Map<String,Object> login(@RequestBody User user)
    {
        log.info("当前登录用户的信息: [{}]",user.toString());
        Map<String,Object> map = new HashMap<>();
        try {
            User userDB =userService.login(user);
            map.put("state",true);
            map.put("msg","提示:登陆成功！");
            map.put("user",userDB);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("state",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }

    /*
    * 生成验证码图片
    * */
    @GetMapping("getImage")
    public String getImageCode(HttpServletRequest request) throws IOException {
        //1、生成验证码
        String code = VerifyCodeUtils.generateVerifyCode(4);
        //2、将验证码放入SevletContext作用域
        request.getServletContext().setAttribute("code",code);//得到最大作用域


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(120,30,byteArrayOutputStream,code);
        //3、图片转为base64
        String s = "data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray());
        return s;
    }
}
