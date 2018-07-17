package com.jza.controller;

import com.jza.dao.QuestionDao;
import com.jza.model.Question;
import com.jza.model.User;
import com.jza.model.ViewObject;
import com.jza.service.QuestionService;
import com.jza.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class IndexController {
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionDao questionDao;

    @RequestMapping(path={"/index","/"},method = {RequestMethod.GET})
    public String index(Model model){
        model.addAttribute("vos",getQuestions(0));
        return "index";
    }


    @RequestMapping(path = "/user/{userId}",method = {RequestMethod.GET})
    public String userIndex(@PathVariable("userId") Integer userId,Model model){
        model.addAttribute("vos",getQuestions(userId));
        return "/index";
    }

    @RequestMapping(path = "/toRegister",method = {RequestMethod.GET})
    public String toRegister(){
        return "login";
    }

    private List<ViewObject> getQuestions(Integer userId){
        LinkedList<ViewObject> viewObjects = new LinkedList<>();
        List<Question> latestQuestions = questionService.getLatestQuestions(userId, 0, 8);
        ViewObject viewObject;
        for (Question question : latestQuestions){
            viewObject = new ViewObject();
            viewObject.set("question",question);
            viewObject.set("user",userService.findUser(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }


    @RequestMapping("/insert")
    @ResponseBody
    public String addQuestion(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Question question = new Question();
            question.setTitle("title" + i);
            question.setContent("1111111111111111111111111");
            question.setUserId(2);
            Date date = new Date();
            date.setTime(date.getTime() + 1000*3600*i);
            question.setCreatedDate(date);
            question.setCommentCount(i);
            list.add(questionService.addQuestion(question));
        }
        return list.toString();
    }

}
