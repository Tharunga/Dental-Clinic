package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.User;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/patients/edit")
public class PatientEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{9,15}$");

    private final PatientDAO patientDAO = new PatientDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            AdminAccess.flashError(req, "Patient not found.");
            resp.sendRedirect(req.getContextPath() + "/patients");
            return;
        }
        try {
            Patient patient = patientDAO.findById(id);
            if (patient == null) {
                AdminAccess.flashError(req, "Patient not found.");
                resp.sendRedirect(req.getContextPath() + "/patients");
                return;
            }
            populateForm(req, patient);
            req.getRequestDispatcher("/patient-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            AdminAccess.flashError(req, "Could not load the patient. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/patients");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = (User) req.getSession().getAttribute("user");
        Integer id = parseId(req.getParameter("id"));
        String patientName = trim(req.getParameter("patientName"));
        String address = trim(req.getParameter("address"));
        String contactNumber = trim(req.getParameter("contactNumber"));
        boolean active = "1".equals(req.getParameter("isActive"));

        if (id == null) {
            AdminAccess.flashError(req, "Patient not found.");
            resp.sendRedirect(req.getContextPath() + "/patients");
            return;
        }

        Patient formPatient = new Patient();
        formPatient.setPatientId(id);
        formPatient.setPatientName(patientName);
        formPatient.setAddress(address);
        formPatient.setContactNumber(contactNumber);
        formPatient.setActive(active);

        try {
            Patient existing = patientDAO.findById(id);
            if (existing == null) {
                AdminAccess.flashError(req, "Patient not found.");
                resp.sendRedirect(req.getContextPath() + "/patients");
                return;
            }
            formPatient.setNic(existing.getNic());
            populateForm(req, formPatient);

            String error = validate(patientName, address, contactNumber);
            if (error != null) {
                req.setAttribute("error", error);
                req.getRequestDispatcher("/patient-form.jsp").forward(req, resp);
                return;
            }

            patientDAO.update(formPatient);
            systemLogDAO.logQuietly(current, "PATIENT_UPDATE",
                    "Updated patient \"" + patientName + "\" NIC " + existing.getNic()
                            + " (" + (active ? "ACTIVE" : "INACTIVE") + ")");
            AdminAccess.flashSuccess(req, "Patient \"" + patientName + "\" updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/patients");
        } catch (SQLException e) {
            populateForm(req, formPatient);
            req.setAttribute("error", "Could not update the patient. Please try again.");
            req.getRequestDispatcher("/patient-form.jsp").forward(req, resp);
        }
    }

    private void populateForm(HttpServletRequest req, Patient patient) {
        req.setAttribute("patientId", patient.getPatientId());
        req.setAttribute("nic", patient.getNic());
        req.setAttribute("patientName", patient.getPatientName());
        req.setAttribute("address", patient.getAddress());
        req.setAttribute("contactNumber", patient.getContactNumber());
        req.setAttribute("active", patient.isActive());
    }

    private static String validate(String name, String address, String contact) {
        if (name == null || name.isEmpty() || address == null || address.isEmpty()
                || contact == null || contact.isEmpty()) {
            return "Please complete all fields before saving.";
        }
        if (name.length() < 3) {
            return "Patient name must be at least 3 characters.";
        }
        if (!PHONE.matcher(contact).matches()) {
            return "Enter a valid contact number (9 to 15 digits).";
        }
        return null;
    }

    private static Integer parseId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
