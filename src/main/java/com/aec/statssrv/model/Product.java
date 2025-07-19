package com.aec.statssrv.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "id_producto")
    private Long idProducto;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion_prod")
    private String descripcionProd;
    
    @Column(name = "precio_individual")
    private BigDecimal precioIndividual;
    
    @Column(name = "uploader_username")
    private String uploaderUsername;
    
    @Column(name = "estado")
    private String estado;
    
    @Column(name = "pais")
    private String pais;
    
    // Constructors, getters and setters
    public Product() {}
    
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcionProd() { return descripcionProd; }
    public void setDescripcionProd(String descripcionProd) { this.descripcionProd = descripcionProd; }
    
    public BigDecimal getPrecioIndividual() { return precioIndividual; }
    public void setPrecioIndividual(BigDecimal precioIndividual) { this.precioIndividual = precioIndividual; }
    
    public String getUploaderUsername() { return uploaderUsername; }
    public void setUploaderUsername(String uploaderUsername) { this.uploaderUsername = uploaderUsername; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}

