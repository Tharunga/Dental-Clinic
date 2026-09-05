package com.sunrisedental.model;

import java.io.Serializable;
import java.math.BigDecimal;

/** Label plus optional count/amount for dashboard charts and tables. */
public class NamedMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;
    private long count;
    private BigDecimal amount;
    private long booked;
    private long completed;
    private long cancelled;

    public NamedMetric() {
    }

    public NamedMetric(String label, long count) {
        this.label = label;
        this.count = count;
    }

    public NamedMetric(String label, long count, BigDecimal amount) {
        this.label = label;
        this.count = count;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getBooked() {
        return booked;
    }

    public void setBooked(long booked) {
        this.booked = booked;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public long getCancelled() {
        return cancelled;
    }

    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
    }
}
