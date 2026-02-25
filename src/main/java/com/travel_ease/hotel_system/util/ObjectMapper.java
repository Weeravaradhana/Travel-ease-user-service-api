package com.travel_ease.hotel_system.util;

import com.travel_ease.hotel_system.dto.request.UserRequestDto;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ObjectMapper {
    public UserRepresentation mapUserRepo(UserRequestDto dto, boolean isEmailVerified , boolean isEnable) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getEmail());
        user.setEnabled(isEnable);
        user.setEmailVerified(isEmailVerified);
        List<CredentialRepresentation> credList = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setValue(dto.getPassword());
        credList.add(cred);
        user.setCredentials(credList);
        return user;
    }
}
