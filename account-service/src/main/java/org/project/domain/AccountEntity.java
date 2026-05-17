package org.project.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    private String id;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AccountEntity() {
    }

    public AccountEntity(String id, String sagaId, String customerId, String status, double amount) {
        this.id = id;
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.status = status;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBalance(double amount) {
        this.amount = amount;
    }
}
