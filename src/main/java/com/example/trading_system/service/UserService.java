package com.example.trading_system.service;

import java.time.LocalDate;

public interface UserService {

    boolean registration(int id, String username, String password, LocalDate birthdate);
}
