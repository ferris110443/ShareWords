package org.yplin.project.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.form.SignupForm;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.impl.UserServiceImpl;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final String scheme = "http";
    private final String domain = "example.com";
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    private SignupForm signupForm;

    @BeforeEach
    void setUp() {
        signupForm = new SignupForm("username", "email@example.com", "password");

    }

    @Test
    void test_Signup_Success() throws UserService.UserExistException {
        when(userRepository.findUserByEmail(signupForm.getEmail())).thenReturn(null);
        when(jwtTokenUtil.generateToken(signupForm.getEmail())).thenReturn("mockToken");
        when(jwtTokenUtil.getExpirationDateFromToken("mockToken")).thenReturn(new Timestamp(System.currentTimeMillis() + 3600000)); // 1 hour later

        SignInDto signInDto = userServiceImpl.signup(signupForm);

        verify(userRepository).save(any(UserModel.class));
        assertEquals(signupForm.getEmail(), signInDto.getUser().getEmail());
        assertEquals(signupForm.getName(), signInDto.getUser().getName());
        assertNotNull(signInDto.getAccessToken());
    }

    @Test
    void test_Signup_Fail_When_User_Exists() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(new UserModel());

        assertThrows(UserService.UserExistException.class, () -> {
            userServiceImpl.signup(signupForm);
        });
    }

}
