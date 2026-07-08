package com.example.billing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class InvoiceItem extends PanacheEntity {

    @ManyToOne
    @JsonIgnore
    public Invoice invoice;

    public String description;
    public Integer quantity;
    public BigDecimal unitPrice;
    public BigDecimal totalPrice;

    public void calculateTotal() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
