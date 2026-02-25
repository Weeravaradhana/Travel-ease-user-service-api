package com.travel_ease.hotel_system.service.impl;

import com.travel_ease.hotel_system.config.KeycloakConfig;
import com.travel_ease.hotel_system.dto.request.LoginRequestDto;
import com.travel_ease.hotel_system.dto.request.PasswordRequestDto;
import com.travel_ease.hotel_system.dto.request.UserRequestDto;
import com.travel_ease.hotel_system.dto.request.UserUpdateRequestDto;
import com.travel_ease.hotel_system.dto.response.LoginResponseDto;
import com.travel_ease.hotel_system.dto.response.UserResponseDto;
import com.travel_ease.hotel_system.entity.Otp;
import com.travel_ease.hotel_system.entity.User;
import com.travel_ease.hotel_system.exception.BadRequestException;
import com.travel_ease.hotel_system.exception.DuplicateEntryException;
import com.travel_ease.hotel_system.repository.OtpRepository;
import com.travel_ease.hotel_system.repository.UserRepository;
import com.travel_ease.hotel_system.service.UserService;
import com.travel_ease.hotel_system.util.ObjectMapper;
import com.travel_ease.hotel_system.util.OtpGenerator;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.resourceserver.jwt.token-uri}")
    private String keycloakApiUrl;

    private final UserRepository userRepository;
    private final OtpRepository  otpRepository;
    private final KeycloakConfig keycloakConfig;
    private final OtpGenerator otpGenerator;
    private final ObjectMapper objectMapper;

    @Override
    public void createUser(UserRequestDto dto) {
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First name is required");
        }

        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new BadRequestException("Last name is required");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        String userId;
        Keycloak keycloak;
        UserRepresentation existingUser;

        keycloak = keycloakConfig.getKeycloakInstance();

        existingUser = keycloak.realm(realm).users().search(dto.getEmail()).stream()
                .findFirst().orElse(null);

        if (existingUser != null) {
            Optional<User> selectedUserFromUserService = userRepository.findByEmail(dto.getEmail());

            if(selectedUserFromUserService.isEmpty()){
                keycloak.realm(realm).users().delete(dto.getEmail());
            }else {
                throw new DuplicateEntryException("Email already exists");
            }
        }else {
            Optional<User> selectedUserFromUserService = userRepository.findByEmail(dto.getEmail());

            if(selectedUserFromUserService.isPresent()){
                Optional<Otp> selectedOtp = otpRepository.findByUserId(selectedUserFromUserService.get().getId());

                selectedOtp
                        .ifPresent(otp -> otpRepository.deleteById(otp.getId()));

                userRepository.deleteById(selectedUserFromUserService.get().getId());

            }
        }

        UserRepresentation userRepresentation = objectMapper.mapUserRepo(dto, false, false);
        Response response = keycloak.realm(realm).users().create(userRepresentation);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            RoleRepresentation userRole = keycloak.realm(realm).roles().get("CUSTOMER").toRepresentation();
            userId= response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Arrays.asList(userRole));
            UserRepresentation createUser = keycloak.realm(realm).users().get(userId).toRepresentation();

            User user = User.builder()
                    .id(UUID.randomUUID())
                    .keycloakId(createUser.getId())
                    .email(dto.getEmail())
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .contact(dto.getContact())
                    .isActive(false)
                    .isAccountNonExpired(true)
                    .isAccountNonLocked(true)
                    .isCredentialsNonExpired(true)
                    .isEnabled(false)
                    .isEmailVerified(false)
                    .build();

            User savedUser = userRepository.save(user);

            Otp createOtp = Otp.builder()
                    .id(UUID.randomUUID())
                    .code(otpGenerator.generateOtp(5))
                    .attempts(0)
                    .isVerified(false)
                    .user(savedUser)
                    .build();
            otpRepository.save(createOtp);
            //send email user email service
        }


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
