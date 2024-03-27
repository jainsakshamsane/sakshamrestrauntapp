package com.sakshamrestrauntapp.Models;

import java.util.List;

public class OrdersModel {
    private String userId;
    private List<Dish> dishes;
    private String status;
    private String timestamp;
    private String orderId;

    public OrdersModel() {
        // Default constructor required for Firebase
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    // Getters and setters for the fields

    public static class Dish {
        private String cartId;
        private String cost;
        private String dish_id;
        private String dish_name;
        private String imageurl;
        private long quantity;
        private String restaurant_id;
        private String timestamp1;
        private String userid;

        // Constructor for Dish class
        public Dish(String cost, String dish_id, String dish_name, String imageurl, long quantity, String restaurant_id, String timestamp1, String userid) {
            this.cost = cost;
            this.dish_id = dish_id;
            this.dish_name = dish_name;
            this.imageurl = imageurl;
            this.quantity = quantity;
            this.restaurant_id = restaurant_id;
            this.timestamp1 = timestamp1;
            this.userid = userid;
        }


        public String getCartId() {
            return cartId;
        }

        public void setCartId(String cartId) {
            this.cartId = cartId;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getDish_id() {
            return dish_id;
        }

        public void setDish_id(String dish_id) {
            this.dish_id = dish_id;
        }

        public String getDish_name() {
            return dish_name;
        }

        public void setDish_name(String dish_name) {
            this.dish_name = dish_name;
        }

        public String getImageurl() {
            return imageurl;
        }

        public void setImageurl(String imageurl) {
            this.imageurl = imageurl;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public String getRestaurant_id() {
            return restaurant_id;
        }

        public void setRestaurant_id(String restaurant_id) {
            this.restaurant_id = restaurant_id;
        }

        public String getTimestamp1() {
            return timestamp1;
        }

        public void setTimestamp1(String timestamp1) {
            this.timestamp1 = timestamp1;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        // Constructor, getters, and setters for Dish class
    }
}
