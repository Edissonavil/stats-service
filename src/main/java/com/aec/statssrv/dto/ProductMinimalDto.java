// src/main/java/com/aec/statssrv/dto/ProductMinimalDto.java
package com.aec.statssrv.dto;

public class ProductMinimalDto {
    private Long idProducto;
    private String nombre;
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long id) { this.idProducto = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }
}
