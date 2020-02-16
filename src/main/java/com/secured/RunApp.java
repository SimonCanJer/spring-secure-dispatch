package com.secured;

import com.secured.api.data.IUserDetailService;
import com.secured.api.data.UserAccount;
import com.secured.concrete.decor.IJustInRequestAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import java.net.ServerSocket;
import java.util.Date;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@EnableWebMvc
@RestController
public class RunApp {

    static final int[] PORT_RANGE = new int[]{8080,8150};
    static public void main(String[] args)
    {
        for(int port=PORT_RANGE[0];port<=PORT_RANGE[1];port++) {
            try {
                ServerSocket server = new ServerSocket(port);
                server.close();
                System.getProperties().setProperty("server.port",String.valueOf(port));
                break;

            } catch (Exception e) {

            }
        }
        SpringApplication.run(RunApp.class);

    }

    ApplicationContext context;
    @Autowired
    IJustInRequestAdvisor advisor;


    @Bean
    ApplicationContextAware contextAware()
    {
        return new ApplicationContextAware() {
            @Override
            public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                context= applicationContext;
                UserAccount ua= new UserAccount();
                Date date = new Date();
                date.setTime(System.currentTimeMillis()+3600*1000*5);

                ua.setPasswordExperationDate(date);
                ua.setPassword("password1");
                ua.setUserId("main_guest");
                ua.setEnabled(true);
                ua.addRole("ROLE_ADMIN");
                applicationContext.getBean(IUserDetailService.class).saveUser(ua);

            }
        };
    }
    @PostMapping("/login")
    String connect(@RequestParam("user") String user, @RequestParam("pass") String password)
    {
        advisor.loginUsernamePassword(user, password);
        return "OK";


    }
    @PostMapping("/visit")
    String inspect(@RequestParam("command") String command)
    {
       return "executed "+command;

    }

}
