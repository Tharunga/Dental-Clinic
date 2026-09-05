package com.sunrisedental.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Treatment implements Serializable {

    private static final long serialVersionUID = 1L;

    private int treatmentId;
    private String treatmentName;
    private BigDecimal cost;

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
