package org.intelli.intellimentor.controller;


import lombok.RequiredArgsConstructor;
import org.intelli.intellimentor.dto.UserDTO;
import org.intelli.intellimentor.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")

public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public Map<String,Long> register(@RequestBody UserDTO userDTO){
        Long tno = userService.register(userDTO);
        return Map.of("TNO",tno);
    }
}
