package com.aec.statssrv.model;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    private Long id;
    
    private Integer cantidad;
    
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "order_id")
    private Long orderId;
    
    private BigDecimal subtotal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // Constructors, getters and setters
    public OrderItem() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
