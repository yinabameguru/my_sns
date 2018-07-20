package test;

import com.jza.dao.QuestionDao;
import com.jza.model.Question;
import com.jza.model.User;
import com.jza.service.QuestionService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class QuestionTest {
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionDao questionDao;
    @Test
    public void test1(){
//        User us = questionDao.us2();
//        System.out.println(us);
////        User us = questionDao.us();
////        System.out.println(us);
////        List<Question> latestQuestions = questionService.getLatestQuestions(0, 0, 8);
////        System.out.println(latestQuestions.toString());
        System.out.println(new Date().getTime());
    }
}
