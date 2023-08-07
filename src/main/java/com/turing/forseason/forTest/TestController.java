package com.turing.forseason.forTest;

import com.turing.forseason.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @PostMapping("/user/login")
    public String login(@ModelAttribute UserDTO userDTO, HttpSession session) {
        UserDTO loginResult = userService.login(userDTO);

        if(loginResult!=null){
            // login 성공
            session.setAttribute("userId", loginResult.getUserId());
            return "home";
        }else{
            // login 실패
            return "login";
        }
    }

    @GetMapping("/user/join")
    public String getJoinForm(){
        return "join";
    }

    @PostMapping("user/join")
    public  String join(@ModelAttribute UserDTO userDTO){
        System.out.println("UserController.join");
        System.out.println("userDTO = " + userDTO);
        userService.join(userDTO);
        return "login";
    }
}
