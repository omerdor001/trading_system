package com.example.trading_system.Service;

import com.example.trading_system.domain.externalservices.ServiceFacade;
import com.example.trading_system.domain.users.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

public class UserServiceImp implements UserService{

    private UserFacade facade;
    private static final Logger logger= LoggerFactory.getLogger(UserServiceImp.class);

    public UserServiceImp(UserFacade facade){
        this.facade=facade;
    }

    @Override
    public boolean registration(int id,String username, String password, LocalDate birthdate) {
        boolean result;
        logger.info("Trying registering a new user: {}", username);
        try {
            String encryption = Security.encrypt(password);
            facade.registration(id,username,encryption,birthdate);
        } catch (Exception e) {
            logger.error("Error occurred : {} , Failed trying registering user: {}", e.getMessage(), username);
            return false;
        }
        logger.info("Finished registering user: {}", username);
        return true;
    }
}
