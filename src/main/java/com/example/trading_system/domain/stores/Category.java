package com.example.trading_system.domain.stores;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getCategoriesString(){
        List<String> categories=new ArrayList<>();
        for(Category category: Category.values()){
            categories.add(category.toString());
        }
        return categories;
    }

    public int getIntValue() {
        return intValue;
    }

    public static boolean isValidCategory(int category) {
        for (Category c : Category.values()) {
            if (c.getIntValue() == category) {
                return true;
            }
        }
        return false;
    }
}