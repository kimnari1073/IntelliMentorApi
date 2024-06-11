//package org.intelli.intellimentor.service;
//
//import lombok.RequiredArgsConstructor;
//import org.intelli.intellimentor.domain.User;
//import org.intelli.intellimentor.dto.UserDTO;
//import org.intelli.intellimentor.repository.UserRepository;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//
//public class UserServiceImpl implements UserService{
//
//    private final ModelMapper modelMapper;
//    private final UserRepository userRepository;
//
//    @Override
//    public Long register(UserDTO userDTO) {
//        User user = modelMapper.map(userDTO,User.class);
//        User savedUser = userRepository.save(user);
//        return savedUser.getId();
//    }
//}
