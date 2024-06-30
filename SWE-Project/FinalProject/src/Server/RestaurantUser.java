package Server;

public class RestaurantUser extends User {
    private String restaurantName;
    private String restaurantPhone;
    private String restaurantHours;
    private String restaurantCuisine;
    private String restaurantMenu;

    public RestaurantUser(String username, String password, String email, String phoneNumber, String address, String restaurantName, String restaurantPhone, String restaurantHours, String restaurantCuisine, String restaurantMenu) {
        super(username, password, email, phoneNumber, address);
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

}
