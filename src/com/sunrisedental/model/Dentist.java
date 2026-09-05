package com.sunrisedental.model;

import java.io.Serializable;

public class Dentist implements Serializable {

    private static final long serialVersionUID = 1L;

    private int dentistId;
    private String dentistName;
    private String specialization;

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
