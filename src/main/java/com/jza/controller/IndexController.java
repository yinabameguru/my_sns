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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionDao questionDao;

    @RequestMapping(path={"/index","/"})
    public String index(Model model){
        model.addAttribute("vos",getQuestions());
        return "index";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String fu(Model model){
        List<User> fu = questionDao.fu();
        return fu.toString();
    }

    private List<ViewObject> getQuestions(){
        LinkedList<ViewObject> viewObjects = new LinkedList<>();
        List<Question> latestQuestions = questionService.getLatestQuestions(0, 0, 8);
        ViewObject viewObject;
        for (Question question : latestQuestions){
            viewObject = new ViewObject();
            viewObject.set("question",question);
            viewObject.set("user",userService.findUser(question.getUserId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

}
