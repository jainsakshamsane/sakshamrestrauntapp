package com.sakshamrestrauntapp.Models;

public class DishModels {

    String dish_id, dish_name, dish_category, dish_category_id, cooktime, category, cost, dishtype, information, ingredients, restaurant_id, owner_id, weight;

    public DishModels(String dishname, String dishtype, String dishcategory, String category, String cooktime, String cost, String dish_category_id, String dish_id, String ingredients, String weight, String information) {
        this.dish_name = dishname;
        this.dishtype = dishtype;
        this.dish_category = dishcategory;
        this.category = category;
        this.cooktime = cooktime;
        this.cost = cost;
        this.dish_category_id = dish_category_id;
        this.dish_id = dish_id;
        this.ingredients = ingredients;
        this.weight = weight;
        this.information = information;
    }

    public DishModels(String dishname, String dishCategoryId, String dishId) {
        this.dish_name = dishname;
        this.dish_category_id = dishCategoryId;
        this.dish_id = dishId;
    }

    public String getDish_category_id() {
        return dish_category_id;
    }

    public void setDish_category_id(String dish_category_id) {
        this.dish_category_id = dish_category_id;
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

    public String getDish_category() {
        return dish_category;
    }

    public void setDish_category(String dish_category) {
        this.dish_category = dish_category;
    }

    public String getCooktime() {
        return cooktime;
    }

    public void setCooktime(String cooktime) {
        this.cooktime = cooktime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDishtype() {
        return dishtype;
    }

    public void setDishtype(String dishtype) {
        this.dishtype = dishtype;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
