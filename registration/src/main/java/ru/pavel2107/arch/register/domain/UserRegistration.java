package ru.pavel2107.arch.register.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistration implements Serializable {
    private String command;
    private User user;
}
