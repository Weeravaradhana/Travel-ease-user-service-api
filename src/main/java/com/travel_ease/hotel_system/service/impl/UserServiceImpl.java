package com.travel_ease.hotel_system.service.impl;

import com.travel_ease.hotel_system.dto.request.LoginRequestDto;
import com.travel_ease.hotel_system.dto.request.PasswordRequestDto;
import com.travel_ease.hotel_system.dto.request.UserRequestDto;
import com.travel_ease.hotel_system.dto.request.UserUpdateRequestDto;
import com.travel_ease.hotel_system.dto.response.LoginResponseDto;
import com.travel_ease.hotel_system.dto.response.UserResponseDto;
import com.travel_ease.hotel_system.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void createUser(UserRequestDto dto) {

    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        return null;
    }

    @Override
    public void resend(String email, String type) {

    }

    @Override
    public void forgotPasswordSendVerificationCode(String email) {

    }

    @Override
    public boolean verifyReset(String otp, String email) {
        return false;
    }

    @Override
    public boolean passwordReset(PasswordRequestDto dto) {
        return false;
    }

    @Override
    public boolean verifyEmail(String otp, String email) {
        return false;
    }

    @Override
    public void updateUserDetails(String email, UserUpdateRequestDto data) {

    }

    @Override
    public UserResponseDto getUserDetails(String email) {
        return null;
    }
}
