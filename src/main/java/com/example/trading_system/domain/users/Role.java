package com.example.trading_system.Domain.users;

import com.example.trading_system.Domain.stores.Store; ;

public class Role {
    private RoleState roleState;
    private Store store;
    private Registered user;
    private int appointedById;

    public Role(RoleState roleState,Store store,Registered user,int appointedById) {
        this.roleState=roleState;
        this.store = store;
        this.user=user;
        this.appointedById=appointedById;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Registered getUser() {
        return user;
    }

    public void setUser(Registered user) {
        this.user = user;
    }

    public int getAppointedById() {
        return appointedById;
    }

    public void setAppointedById(int appointedById) {
        this.appointedById = appointedById;
    }
}
