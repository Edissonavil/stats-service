package com.aec.statssrv.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StatsFilterDto {
    @NotNull
    @Min(2020)
    @Max(2030)
    private Integer year;
    
    @Min(1)
    @Max(12)
    private Integer month;
    
    private String collaboratorUsername;
    
    // Constructors
    public StatsFilterDto() {}
    
    public StatsFilterDto(Integer year, Integer month) {
        this.year = year;
        this.month = month;
    }
    
    // Getters and setters
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public String getCollaboratorUsername() { return collaboratorUsername; }
    public void setCollaboratorUsername(String collaboratorUsername) { 
        this.collaboratorUsername = collaboratorUsername; 
    }
}
