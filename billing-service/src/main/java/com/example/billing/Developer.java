package com.example.billing;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Developer extends PanacheEntity {

    public String name;
    public String specialty;

    public static List<Developer> findBySpecialty(String specialty) {
        return list("specialty", specialty);
    }
}
