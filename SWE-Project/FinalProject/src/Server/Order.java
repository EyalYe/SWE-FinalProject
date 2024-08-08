package Server;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private String customerUsername;
    private String restaurantUsername;
    private List<String> items = new ArrayList<String>();
    private double totalPrice;

    public Order(int orderId, String customerUsername, String restaurantUsername, String ordersDetails) {
        this.orderId = orderId;
        this.customerUsername = customerUsername;
        this.restaurantUsername = restaurantUsername;
        for(String item : ordersDetails.split(",")){
            items.add(item.split(":")[0]);
            totalPrice += Double.parseDouble(item.split(":")[1]);
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getRestaurantUsername() {
        return restaurantUsername;
    }

    public List<String> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}