package com.jza.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jza.dao.QuestionDao;
import com.jza.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;

    private int pageSize = 8;

    public PageInfo<Question> getLatestQuestions(int userId,int currentPage){
        try {
            PageHelper.startPage(currentPage,pageSize);
            List<Question> questions = questionDao.selectLatestQuestions(userId);
            PageInfo<Question> questionPageInfo = new PageInfo<>(questions);
            return questionPageInfo;
        }catch (RuntimeException e){
            e.printStackTrace();
            return null;
        }

    }
    public int addQuestion(Question question){
        try {
            return questionDao.insertQuestion(question);
        }catch (RuntimeException e){
            e.printStackTrace();
            return -1;
        }
    }
}
