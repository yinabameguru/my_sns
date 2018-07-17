package com.jza.service;

import com.jza.dao.QuestionDao;
import com.jza.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;
    public List<Question> getLatestQuestions(int userId,int offset,int limit){
        try {
            return questionDao.selectLatestQuestions(userId,offset,limit);
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
