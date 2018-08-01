package com.jza.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jza.dao.QuestionDao;
import com.jza.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;

    @Autowired
    SensitiveService sensitiveService;

    private int pageSize = 8;

    public PageInfo<Question> getLatestQuestions(int userId,int currentPage){
            PageHelper.startPage(currentPage,pageSize);
            List<Question> questions = questionDao.selectLatestQuestions(userId);
            PageInfo<Question> questionPageInfo = new PageInfo<>(questions);
            return questionPageInfo;
    }
    public int addQuestion(Question question){
        question.setTitle(sensitiveService.filter(HtmlUtils.htmlEscape(question.getTitle())));
        question.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(question.getContent())));
        return questionDao.insertQuestion(question);
    }

    public Question getQuestionById(Integer questionId) {
        return questionDao.selectQuestionById(questionId);
    }

    public Integer updateCommentCount(Integer commentCount, Integer id) {
        return questionDao.updateCommentCount(commentCount, id);
    }
}
