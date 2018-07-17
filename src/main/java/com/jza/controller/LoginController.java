package com.jza.controller;

import com.jza.model.User;
import com.jza.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(@Valid User user,
                           BindingResult bindingResult,
                           Model model
    ){
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
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ){
        if (bindingResult.hasErrors()){
            model.addAttribute("errMsg",bindingResult.getFieldError().getDefaultMessage());
            return "login";
        }
        Map<String,Object> map = userService.login(user);
        if (map.containsKey("errMsg")){
            model.addAttribute("errMag",(String)map.get("errMsg"));
            return "login";
        }
        return "redirect:/";
    }
}
