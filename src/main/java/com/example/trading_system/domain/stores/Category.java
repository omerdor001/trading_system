package com.example.trading_system.domain.stores;

public enum Category {
    Sport(1),
    Art(2),
    Food(3),
    Clothes(4),
    Films(5);

    private final int intValue;

    Category(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }
    public static Category getCategoryFromInt(int categoryInt) {
        switch (categoryInt) {
            case 1:
                return Category.Sport;
            case 2:
                return Category.Art;
            case 3:
                return Category.Food;
            case 4:
                return Category.Clothes;
            case 5:
                return Category.Films;
            default:
                throw new IllegalArgumentException("Invalid category integer: " + categoryInt);
        }
    }
}