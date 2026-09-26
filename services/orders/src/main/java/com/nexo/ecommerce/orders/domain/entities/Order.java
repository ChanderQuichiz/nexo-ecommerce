package com.nexo.ecommerce.orders.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Date;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.OrderId;
import com.nexo.ecommerce.orders.domain.value_objects.PaymentIntentId;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.ShippingFee;
import com.nexo.ecommerce.orders.domain.value_objects.Status;
import com.nexo.ecommerce.orders.domain.value_objects.SubTotal;
import com.nexo.ecommerce.orders.domain.value_objects.Tax;
import com.nexo.ecommerce.orders.domain.value_objects.Total;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;


public class Order {
  
    private OrderId id;

    private UserId userId;

    private SubTotal subtotal;

    private ShippingFee shippingFee;

    private Tax tax;

    private Total total;

    private Status status;


    private Date date;

    private Address address;

    private City city;

    private Phone phone;

    private List<PaymentIntentId> paymentIntentId;

    private List<Item> items;

    private List<Object> domainEvents;

 

    public static Order create(UserId userId, Address address, City city, Phone phone, List<Item> items) {

        Order order = new Order();
        order.id = new OrderId(java.util.UUID.randomUUID());
        order.items = items;
        order.subtotal = order.calculateSubtotal();
        order.shippingFee = order.calculateShippingFee();
        order.tax = order.calculateTax();
        order.total = order.calculateTotal();
        order.status = Status.CREATED;
        order.date = new Date(LocalDateTime.now());
        order.address = address;
        order.city = city;
        order.phone = phone;
        order.userId = userId;
        order.paymentIntentId = new java.util.ArrayList<>();
        order.domainEvents = new java.util.ArrayList<>();
        order.addDomainEvent(new OrderCreatedEvent(order.id.value().toString()));
        return order;
    }
    public Order() {
        // Default constructor for JPA
        this.paymentIntentId = new java.util.ArrayList<>();
        this.domainEvents = new java.util.ArrayList<>();
    }
public Order (OrderId id, UserId userId, SubTotal subtotal, ShippingFee shippingFee, Tax tax, Total total, Status status, Date date, Address address, City city, Phone phone, List<PaymentIntentId> paymentIntentId, List<Item> items) {
        this.id = id;
        this.userId = userId;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.tax = tax;
        this.total = total;
        this.status = status;
        this.date = date;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.paymentIntentId = paymentIntentId;
        this.items = items;
    }

    public Total calculateTotal() {
        BigDecimal totalValue = subtotal.value().add(shippingFee.value()).add(tax.value());
        return new Total(totalValue);
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }


    public SubTotal calculateSubtotal() {
        BigDecimal subtotalValue = items.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SubTotal(subtotalValue);
    }

    public ShippingFee calculateShippingFee() {
        BigDecimal shippingFeeValue = new BigDecimal("0.00");
        return new ShippingFee(shippingFeeValue);
    }

    public Tax calculateTax() {
        BigDecimal taxRate = new BigDecimal("0.07");
        BigDecimal taxVal = subtotal.value().multiply(taxRate).setScale(2, java.math.RoundingMode.HALF_UP);
        return new Tax(taxVal);
    }

    public void addPaymentIntentId(PaymentIntentId paymentIntentId) {
        this.paymentIntentId.add(paymentIntentId);
    }
    public OrderId getId() {
        return id;
    }
    public List<Item> getItems() {
        return items;
    }

       public UserId getUserId() {
        return userId;
    }

    public SubTotal getSubtotal() {
        return subtotal;
    }

    public ShippingFee getShippingFee() {
        return shippingFee;
    }

    public Tax getTax() {
        return tax;
    }

    public Total getTotal() {
        return total;
    }

    public Status getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    public Address getAddress() {
        return address;
    }

    public City getCity() {
        return city;
    }

    public Phone getPhone() {
        return phone;
    }

    public List<PaymentIntentId> getPaymentIntentId() {
        return paymentIntentId;
    }

    public List<Object> getDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
    public void addDomainEvent(Object event) {
        domainEvents.add(event);
    }

}
