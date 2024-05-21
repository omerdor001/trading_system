package com.example.trading_system.users;

import com.example.trading_system.Domain.stores.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class UserTests {
    private static final Logger logger = LoggerFactory.getLogger(UserTests.class);

    @Mock
    private Store store;


    @BeforeEach
    void setUp() {

    }

    @Test
    void locationTest() {
        assertThat(true).isTrue();
    }
}
