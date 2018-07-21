package com.jza.controller;

import com.jza.model.Ticket;
import com.jza.model.User;
import com.jza.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(@Valid User user,
                           BindingResult bindingResult,
                           Model model
    ){
        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("errMsg",bindingResult.getFieldError().getDefaultMessage());
                return "login";
            }
            Map<String, Object> map = userService.register(user);
            if (map.containsKey("errMsg")){
                model.addAttribute("errMsg",(String)map.get("errMsg"));
                return "login";
            }
            return "redirect:/";
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("errMsg", "服务器错误");
            return "login";
        }
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(
            @Valid User user,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse response,
            @RequestParam(value = "rememberme",required = false) boolean rememberme
    ){
        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("errMsg",bindingResult.getFieldError().getDefaultMessage());
                return "login";
            }
            Map<String,Object> map = userService.login(user,rememberme);
            if (map.containsKey("errMsg")){
                model.addAttribute("errMsg",(String)map.get("errMsg"));
                return "login";
            }
            User userResult = (User) map.get("user");
            Ticket ticket = (Ticket) map.get("ticket");
            Cookie cookie = new Cookie("ticket", ticket.getTicket().toString());
            cookie.setPath("/");
            if (rememberme)
                cookie.setMaxAge(3600 * 24 * 10 + 3600 * 8);
            else
                cookie.setMaxAge(3600 + 3600 * 8);
            response.addCookie(cookie);
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("登陆异常" + e.getMessage());
            model.addAttribute("errMsg", "服务器错误");
            return "login";
        }
    }

    @RequestMapping(value = "/logout",method = {RequestMethod.GET})
    public String logout(
            HttpServletRequest request,
            Model model
    ){
        try {
            for (Cookie cookie : request.getCookies()){
                if (cookie.getName().equals("ticket")){
                    userService.logout(cookie.getValue());
                    break;
                }
            }
            return "redirect:/";
        }catch (Exception e){
            e.printStackTrace();
            logger.error("登出异常" + e.getMessage());
            model.addAttribute("errMsg", "服务器错误");
            return "login";
        }
    }
}
