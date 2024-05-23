package com.example.trading_system.domain.users;

public class Role {
    private RoleState roleState;
    private String name_id;
    private int appointedById;

    public Role(RoleState roleState,String name_id,int appointedById) {
        this.roleState=roleState;
        this.name_id=name_id;
        this.appointedById=appointedById;
    }

    public String getStoreId() {
        return name_id;
    }

    public void setStoreId(String name_id) {
        this.name_id = name_id;
    }

    public int getAppointedById() {
        return appointedById;
    }

    public void setAppointedById(int appointedById) {
        this.appointedById = appointedById;
    }
}
