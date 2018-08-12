package com.jza.controller;

import com.jza.async.EventModel;
import com.jza.async.EventProducer;
import com.jza.async.EventType;
import com.jza.model.Mail;
import com.jza.model.Ticket;
import com.jza.model.User;
import com.jza.service.MailService;
import com.jza.service.SensitiveService;
import com.jza.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Transactional
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(@Valid User user,
                           BindingResult bindingResult,
                           HttpServletResponse response,
                           Model model
    ){
        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("errMsg",bindingResult.getFieldError().getDefaultMessage());
                return "login";
            }
            Map<String, Object> map = userService.register(new User(user));
            if (map.containsKey("errMsg")){
                model.addAttribute("errMsg",(String)map.get("errMsg"));
                return "login";
            }
            user = userService.getUserByName(user.getName());
            Map<String, Object> mailModel = new HashMap<>();
            mailModel.put("mail", user.getName());
            mailModel.put("activationCode", user.getActivationCode());
            mailService.fireMailEvent(user.getName(), "用户激活", "mails/registerMail.html", mailModel);
            return "registerSuccess";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("注册异常" + e.getMessage());
            model.addAttribute("errMsg", "服务器错误");
            return "login";
        }
    }

    @Transactional
    @RequestMapping(value = "/activation", method = {RequestMethod.GET})
    public String activation(
            @RequestParam("mail") String name,
            @RequestParam("activationCode") String activationCode,
            HttpServletResponse response,
            Model model
    ) {
        try {
            User user = userService.getUserByName(name);
            userService.activation(name, activationCode);
            Ticket ticket = userService.activationLogin(user, false);
            Cookie cookie = new Cookie("ticket",ticket.getTicket());
            cookie.setPath("/");
            cookie.setMaxAge(3600 + 3600 * 8);
            response.addCookie(cookie);
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", user.getName());
            mailService.fireMailEvent(user.getName(), "激活成功", "/mails/registerSuccessMail.html", map);
            model.addAttribute("name", name);
            return "mails/registerSuccessMail";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("激活失败" + e.getMessage());
            model.addAttribute("errMsg", "激活失败");
            return "err";
        }
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(
            @Valid User user,
            BindingResult bindingResult,
            Model model,
            HttpServletResponse response,
            @RequestParam(value = "next",required = false) String next,
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
            if (StringUtils.isEmpty(next))
                return  "redirect:/";
            String s ="redirect:" + next;
            return s;
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
