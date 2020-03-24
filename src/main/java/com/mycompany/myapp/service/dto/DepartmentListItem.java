package com.mycompany.myapp.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.Region;

import javax.persistence.*;
import java.io.Serializable;

public class DepartmentListItem implements Serializable {


    private Long id;

    private String name;

    private Integer population;

    private String department_number;

    private String regionName;


    public DepartmentListItem(Long id, String name, Integer population, String department_number, String regionName) {
        this.id = id;
        this.name = name;
        this.population = population;
        this.department_number = department_number;
        this.regionName = regionName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getDepartment_number() {
        return department_number;
    }

    public void setDepartment_number(String department_number) {
        this.department_number = department_number;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}
