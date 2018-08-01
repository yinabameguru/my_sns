package com.jza.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jza.dao.CommentDao;
import com.jza.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;

    public Integer addComment(Comment comment) {
        return commentDao.insertComment(comment);
    }

    public Integer deleteComment(Integer id) {
        return commentDao.updateStatus(1, id);
    }

    public PageInfo<Comment> getComment(Integer entityId,Integer entityType,Integer currentPage) {
        PageHelper.startPage(currentPage, 8);
        List<Comment> comments = commentDao.selectComments(entityId, entityType);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        return pageInfo;
    }

    public Integer getCount(Integer entityId, Integer entityType) {
        return commentDao.selectCount(entityId, entityType);
    }
}
