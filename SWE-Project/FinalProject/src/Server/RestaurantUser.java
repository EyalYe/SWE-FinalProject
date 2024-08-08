package Server;

import java.util.ArrayList;
import java.util.List;

public class RestaurantUser extends User {
    private String restaurantName;
    private String restaurantPhone;
    private String restaurantHours;
    private String restaurantCuisine;
    private String restaurantMenu;
    private List<Order> orders = new ArrayList<Order>();

    public RestaurantUser(String username, String password, String email, String phoneNumber, String address, String restaurantName, String restaurantPhone, String restaurantHours, String restaurantCuisine, String restaurantMenu) {
        super(username, password, email, phoneNumber, address, "restaurant");
        this.restaurantName = restaurantName;
        this.restaurantPhone = restaurantPhone;
        this.restaurantHours = restaurantHours;
        this.restaurantCuisine = restaurantCuisine;
        this.restaurantMenu = restaurantMenu;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getRestaurantHours() {
        return restaurantHours;
    }

    public void setRestaurantHours(String restaurantHours) {
        this.restaurantHours = restaurantHours;
    }

    public String getRestaurantCuisine() {
        return restaurantCuisine;
    }

    public void setRestaurantCuisine(String restaurantCuisine) {
        this.restaurantCuisine = restaurantCuisine;
    }

    public String getRestaurantMenu() {
        return restaurantMenu;
    }

    public void setRestaurantMenu(String restaurantMenu) {
        this.restaurantMenu = restaurantMenu;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }
}
