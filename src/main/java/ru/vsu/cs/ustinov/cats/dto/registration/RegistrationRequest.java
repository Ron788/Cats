package ru.vsu.cs.ustinov.cats.dto.registration;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
}