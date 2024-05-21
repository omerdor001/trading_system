package com.example.trading_system.Domain.users;

public class Manager extends RoleState{
    private boolean watch;
    private boolean editSupply;
    private boolean editBuyPolicy;
    private boolean editDiscountPolicy;

    public Manager(Role role,boolean watch,boolean editSupply,boolean editBuyPolicy,boolean editDiscountPolicy) {
        super(role);
        this.watch=watch;
        this.editBuyPolicy=editBuyPolicy;
        this.editDiscountPolicy=editDiscountPolicy;
        this.editSupply=editSupply;
    }

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public boolean isEditSupply() {
        return editSupply;
    }

    public void setEditSupply(boolean editSupply) {
        this.editSupply = editSupply;
    }

    public boolean isEditBuyPolicy() {
        return editBuyPolicy;
    }

    public void setEditBuyPolicy(boolean editBuyPolicy) {
        this.editBuyPolicy = editBuyPolicy;
    }

    public boolean isEditDiscountPolicy() {
        return editDiscountPolicy;
    }

    public void setEditDiscountPolicy(boolean editDiscountPolicy) {
        this.editDiscountPolicy = editDiscountPolicy;
    }
}
