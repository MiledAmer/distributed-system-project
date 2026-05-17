package org.project.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class TicketEntity {
    @Id
    private String id;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TicketEntity() {
    }

    public TicketEntity(String id, String sagaId, String customerId, String status) {
        this.id = id;
        this.sagaId = sagaId;
        this.customerId = customerId;
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
