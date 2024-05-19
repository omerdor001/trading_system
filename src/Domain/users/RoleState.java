package users;

public class RoleState {
    Role role;

    public RoleState(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private void addProduct(int storeId,String product_details) {} //Change product_details Type

    private void editProduct(int storeId,int productId) {}

    private void removeProduct(int storeId,int productId) {}

    private void changeProduct(int storeId,int productId) {}

    private void addBuyPolicy(int storeId,int productId) {}

    private void removeBuyPolicy(int storeId,int productId) {}

    private void editBuyPolicy(int storeId,int productId) {}

    private void addDiscountPolicy(int storeId,int productId) {}

    private void removeDiscountPolicy(int storeId,int productId) {}

    private void editDiscountPolicy(int storeId,int productId) {}

    private void addProduct(String product_details) {} //Change product_details Type

    private void editProduct(int productId) {}

    private void removeProduct(int productId) {}

    private void changeProduct(int productId) {}
}
