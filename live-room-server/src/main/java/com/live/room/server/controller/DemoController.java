package com.live.room.server.controller;

import com.live.room.server.dao.StatDao;
import com.live.room.server.dao.UserDao;
import com.live.room.server.entity.MsgEntity;
import com.live.room.server.entity.UserEntity;
import com.live.room.server.service.IpUtil;
import com.live.room.server.service.NameGenerator;
import com.live.room.server.service.UserAgentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/14.
 */
@Controller
public class DemoController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private StatDao statDao;
    @RequestMapping(value = "/live_room",method = RequestMethod.GET)
    public String hello(HttpServletRequest request, Model model){
        //根据用户ip判断用户是否访问过本站
        String ip = IpUtil.getIp(request);
        Random random = new Random(20);
        HttpSession session = request.getSession();
        UserEntity user;
        if (userDao.findOne(ip) != null){
            //用户曾经访问过
            System.out.println("用户曾经访问过");
            user = userDao.findOne(ip);
        }else {
            //用户未访问过，存储用户信息
            System.out.println("用户未访问过");
            user = new UserEntity();
            user.setIp(ip);
            user.setRandomName(NameGenerator.generate());
            //System.out.println("ip="+ip+"name="+user.getRandomName());
            userDao.save(user);
        }
        //System.out.println("ip="+ip+"name="+user.getRandomName());
        session.setAttribute("user",user);
        //判断用户是手机还是电脑端
        if (UserAgentUtil.JudgeIsMoblie(request)){
            //移动端访问
            return "live_m";
        }else {
            model.addAttribute("online_guests",getOnlineUser());
            model.addAttribute("history_guests",getHistoryGuests());
            return "live";
        }

    }
    @RequestMapping(value = "/online_guests",method = RequestMethod.GET)
    @ResponseBody
    public Set getOnlineUser(){
        return  statDao.getAllUserOnline();
    }
    @RequestMapping(value = "/history_guests",method = RequestMethod.GET)
    @ResponseBody
    public List getHistoryGuests(){
        return statDao.getGuestHistory();
    }
    @MessageMapping(value = "/chat")
    @SendTo("/topic/group")
    public MsgEntity testWst(String message , @Header(value = "simpSessionAttributes") Map<String,Object> session){
        UserEntity user = (UserEntity) session.get("user");
        String username = user.getRandomName();
        MsgEntity msg = new MsgEntity();
        msg.setCreator(username);
        msg.setsTime(Calendar.getInstance());
        msg.setMsgBody(message);
        return msg;
    }
    @RequestMapping(value = "/resume",method = RequestMethod.GET)
    public String viewMyresume(){
        return "myresume";
    }

}
