package com.jza.interceptor;

import com.jza.dao.TicketDao;
import com.jza.dao.UserDao;
import com.jza.model.HostHolder;
import com.jza.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    TicketDao ticketDao;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserDao userDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equals("ticket")){
                ticket = cookie.getValue();
                break;
            }
        }
        if (ticket != null){
            Ticket ticketResult = ticketDao.selectTicketByTicket(ticket);
            if (ticketResult.getExpired().after(new Date()) && ticketResult.getStatus() == 0)
                hostHolder.setUser(userDao.selectUserById(ticketResult.getUser_id()));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null)
            modelAndView.addObject("user",hostHolder.getUser());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
