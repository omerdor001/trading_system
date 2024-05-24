package com.example.trading_system.domain.users;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ManagerPermission {
    private boolean watch;
    private boolean editSupply;
    private boolean editBuyPolicy;
    private boolean editDiscountPolicy;
}
