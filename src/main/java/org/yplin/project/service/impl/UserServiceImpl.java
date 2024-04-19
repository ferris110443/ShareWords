package org.yplin.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserDto;
import org.yplin.project.data.form.SignInForm;
import org.yplin.project.data.form.SignupForm;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.UserService;

import java.sql.Timestamp;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private @Value("${jwt.signKey}") String jwtSignKey;

    @Override
    public UserDto getUserDtoByToken(String token) {
        return null;
    }

    @Override
    public UserModel getUserByToken(String token) {
        return null;
    }

    @Override
    public SignInDto signup(SignupForm signupForm) throws UserExistException {
        if (userRepository.findUserByEmail(signupForm.getEmail()) != null) {
            throw new UserExistException(signupForm.getEmail() + " is already exist");
        }

        String token = jwtTokenUtil.generateToken(signupForm.getEmail());
        UserModel user = new UserModel();
        user.setName(signupForm.getName());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setAccountCreatedDate(new Timestamp(System.currentTimeMillis()));
        user.setLastOnlineDate(new Timestamp(System.currentTimeMillis()));
        user.setAccessToken(token);
        user.setAccessExpired(jwtTokenUtil.getExpirationDateFromToken(token).getTime());

        userRepository.save(user);

        return SignInDto.from(user);
    }

    @Override
    public SignInDto signIn(SignInForm signInForm) throws UserPasswordMismatchException {

        UserModel userDB = userRepository.findUserByEmail(signInForm.getEmail());
        if (userDB == null) {
            throw new UsernameNotFoundException("User not found with email: " + signInForm.getEmail());
        }

        if (!passwordEncoder.matches(signInForm.getPassword(), userDB.getPassword())) {
            throw new UserPasswordMismatchException("Wrong Password");
        }

        String token = jwtTokenUtil.generateToken(signInForm.getEmail());

        userDB.setLastOnlineDate(new Timestamp(System.currentTimeMillis()));
        userDB.setAccessToken(token);
        userDB.setAccessExpired(jwtTokenUtil.getExpirationDateFromToken(token).getTime());

        userRepository.save(userDB);

        return SignInDto.from(userDB);
    }
}