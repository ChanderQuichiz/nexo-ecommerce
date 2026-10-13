package com.nexo.ecommerce.orders.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "orders")
public class OrderEntityJpa{
    @Id 
     
    private String id;

    private String userId;

    private BigDecimal subtotal;

    private BigDecimal shippingFee;

    private BigDecimal tax;

    private BigDecimal total;

    private String status;


    private LocalDateTime date;

    private String address;

    private String city;

    private String phone;

    @ElementCollection 
    @CollectionTable(name = "order_payment_intent_id")
    private List<String> paymentIntentId = new java.util.ArrayList<>();


    @ElementCollection 
    @CollectionTable(name = "order_items")
    private List<ItemEmbeddable> items = new java.util.ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(List<String> paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public List<ItemEmbeddable> getItems() {
        return items;
    }

    public void setItems(List<ItemEmbeddable> items) {
        this.items = items;
    }
}
