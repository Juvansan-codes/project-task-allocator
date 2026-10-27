package com.taskallocator.model;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Abstract base class for all persistent domain entities in the application.
 * Demonstrates inheritance, encapsulation, and common identity handling in Java OOP.
 */
public abstract class BaseEntity {

    protected int id;
    protected Timestamp createdAt;

    public BaseEntity() {
    }

    public BaseEntity(int id, Timestamp createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != 0 && id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
