package com.speculo.mercator.models;

public class DonationModel {
    String product_image;
    String product_name;
    String product_description;
    String seller_name;
    String seller_email;
    String seller_number;

    public DonationModel() {}

    public DonationModel(String product_image, String product_name, String product_description, String seller_name, String seller_email, String seller_number) {
        this.product_image = product_image;
        this.product_name = product_name;
        this.product_description = product_description;
        this.seller_name = seller_name;
        this.seller_email = seller_email;
        this.seller_number = seller_number;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_email() {
        return seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public String getSeller_number() {
        return seller_number;
    }

    public void setSeller_number(String seller_number) {
        this.seller_number = seller_number;
    }
}
