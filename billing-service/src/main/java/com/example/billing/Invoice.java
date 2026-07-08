package com.example.billing;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Invoice extends PanacheEntity {

    public String customerId;

    @Enumerated(EnumType.STRING)
    public InvoiceStatus status;

    public LocalDateTime issueDate;
    public LocalDateTime dueDate;
    public LocalDateTime paidDate;

    public BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<InvoiceItem> items;

    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, OVERDUE, CANCELLED
    }

    public static List<Invoice> findByCustomerId(String customerId) {
        return list("customerId", customerId);
    }

    public static List<Invoice> findByStatus(InvoiceStatus status) {
        return list("status", status);
    }

    public static List<Invoice> findOverdue() {
        return list("status = ?1 and dueDate < ?2",
                    InvoiceStatus.ISSUED,
                    LocalDateTime.now());
    }
}
