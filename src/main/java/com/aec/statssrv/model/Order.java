package com.aec.statssrv.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long id;
    
    @Column(name = "cliente_username")
    private String clienteUsername;
    
    @Column(name = "creator_username")
    private String creatorUsername;
    
    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
    
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @Column(name = "paypal_order_id")
    private String paypalOrderId;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_status")
    private String paymentStatus;
    
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    // Constructors, getters and setters
    public Order() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClienteUsername() { return clienteUsername; }
    public void setClienteUsername(String clienteUsername) { this.clienteUsername = clienteUsername; }
    
    public String getCreatorUsername() { return creatorUsername; }
    public void setCreatorUsername(String creatorUsername) { this.creatorUsername = creatorUsername; }
    
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}