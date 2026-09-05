package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.Patient;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/appointments/edit")
public class AppointmentEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{9,15}$");
    private static final Set<String> ALLOWED_STATUS = Set.of("BOOKED", "COMPLETED", "CANCELLED");

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = trim(req.getParameter("number")).toUpperCase();
        if (number.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/appointments/search");
            return;
        }
        try {
            Appointment appointment = appointmentDAO.findByNumber(number);
            if (appointment == null) {
                req.getSession().setAttribute("error", "No appointment found for number " + number + ".");
                resp.sendRedirect(req.getContextPath() + "/appointments/search");
                return;
            }
            fillForm(req, appointment);
            loadLookups(req, appointment.getDentistId());
            req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.getSession().setAttribute("error", "Could not load the appointment. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/appointments/search");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = trim(req.getParameter("number")).toUpperCase();
        String patientName = trim(req.getParameter("patientName"));
        String address = trim(req.getParameter("address"));
        String contact = trim(req.getParameter("contactNumber"));
        String dentistIdRaw = trim(req.getParameter("dentistId"));
        String treatmentIdRaw = trim(req.getParameter("treatmentId"));
        String dateRaw = trim(req.getParameter("appointmentDate"));
        String timeRaw = trim(req.getParameter("appointmentTime"));
        String status = trim(req.getParameter("status")).toUpperCase();

        req.setAttribute("number", number);
        req.setAttribute("nic", trim(req.getParameter("nic")));
        req.setAttribute("patientName", patientName);
        req.setAttribute("address", address);
        req.setAttribute("contactNumber", contact);
        req.setAttribute("dentistId", dentistIdRaw);
        req.setAttribute("treatmentId", treatmentIdRaw);
        req.setAttribute("appointmentDate", dateRaw);
        req.setAttribute("appointmentTime", timeRaw);
        req.setAttribute("status", status);

        try {
            Appointment existing = appointmentDAO.findByNumber(number);
            if (existing == null) {
                req.setAttribute("error", "No appointment found for number " + number + ".");
                loadLookups(req, parseIntOrZero(dentistIdRaw));
                req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
                return;
            }

            String error = validate(patientName, address, contact, dentistIdRaw, treatmentIdRaw, dateRaw, timeRaw,
                    status);
            if (error != null) {
                req.setAttribute("error", error);
                loadLookups(req, existing.getDentistId());
                req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
                return;
            }

            LocalDate date = LocalDate.parse(dateRaw);
            LocalTime time = LocalTime.parse(timeRaw);
            int dentistId = Integer.parseInt(dentistIdRaw);
            int treatmentId = Integer.parseInt(treatmentIdRaw);

            Dentist dentist = dentistDAO.findById(dentistId);
            if (dentist == null) {
                req.setAttribute("error", "Selected dentist was not found.");
                loadLookups(req, existing.getDentistId());
                req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
                return;
            }
            if (!dentist.isActive() && dentistId != existing.getDentistId()) {
                req.setAttribute("error", "Selected dentist is not available for appointments.");
                loadLookups(req, existing.getDentistId());
                req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
                return;
            }

            if (appointmentDAO.slotTaken(dentistId, date, time, existing.getAppointmentId())) {
                req.setAttribute("error",
                        "This dentist already has an appointment at the selected date and time. Please choose another slot.");
                loadLookups(req, existing.getDentistId());
                req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
                return;
            }

            Patient patient = patientDAO.findById(existing.getPatientId());
            if (patient == null) {
                req.setAttribute("error", "Patient record for this appointment was not found.");
                loadLookups(req, existing.getDentistId());
                req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
                return;
            }
            patient.setPatientName(patientName);
            patient.setAddress(address);
            patient.setContactNumber(contact);
            patientDAO.update(patient);

            existing.setDentistId(dentistId);
            existing.setTreatmentId(treatmentId);
            existing.setAppointmentDate(date);
            existing.setAppointmentTime(time);
            existing.setStatus(status);
            appointmentDAO.update(existing);

            User current = (User) req.getSession().getAttribute("user");
            systemLogDAO.logQuietly(current, "APPOINTMENT_UPDATE",
                    "Updated appointment " + number + " (status " + status + ")");
            req.getSession().setAttribute("success",
                    "Appointment " + number + " updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/appointments/search?number=" + number);
        } catch (SQLException e) {
            if ("DOUBLE_BOOKING".equals(e.getMessage())) {
                req.setAttribute("error", "This dentist already has an appointment at the selected date and time.");
            } else {
                req.setAttribute("error", "Could not save the appointment. Please try again.");
            }
            loadLookups(req, parseIntOrZero(dentistIdRaw));
            req.getRequestDispatcher("/appointment-edit.jsp").forward(req, resp);
        }
    }

    private void fillForm(HttpServletRequest req, Appointment a) {
        req.setAttribute("number", a.getAppointmentNumber());
        req.setAttribute("nic", a.getNic());
        req.setAttribute("patientName", a.getPatientName());
        req.setAttribute("address", a.getAddress());
        req.setAttribute("contactNumber", a.getContactNumber());
        req.setAttribute("dentistId", String.valueOf(a.getDentistId()));
        req.setAttribute("treatmentId", String.valueOf(a.getTreatmentId()));
        req.setAttribute("appointmentDate", a.getAppointmentDate().toString());
        req.setAttribute("appointmentTime", a.getAppointmentTime());
        req.setAttribute("status", a.getStatus());
    }

    private void loadLookups(HttpServletRequest req, int currentDentistId) {
        try {
            List<Dentist> dentists = new ArrayList<>(dentistDAO.findAllActive());
            boolean hasCurrent = dentists.stream().anyMatch(d -> d.getDentistId() == currentDentistId);
            if (!hasCurrent && currentDentistId > 0) {
                Dentist current = dentistDAO.findById(currentDentistId);
                if (current != null) {
                    dentists.add(0, current);
                }
            }
            req.setAttribute("dentists", dentists);
            req.setAttribute("treatments", treatmentDAO.findAll());
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dentists or treatments from the database.");
        }
    }

    private String validate(String name, String address, String contact, String dentistId, String treatmentId,
            String dateRaw, String timeRaw, String status) {
        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || dentistId.isEmpty() || treatmentId.isEmpty()
                || dateRaw.isEmpty() || timeRaw.isEmpty() || status.isEmpty()) {
            return "Please complete all fields before saving.";
        }
        if (name.length() < 3) {
            return "Patient name must be at least 3 characters.";
        }
        if (!PHONE.matcher(contact).matches()) {
            return "Enter a valid contact number (9 to 15 digits).";
        }
        if (!ALLOWED_STATUS.contains(status)) {
            return "Select a valid status (Booked, Completed, or Cancelled).";
        }
        try {
            LocalDate.parse(dateRaw);
            LocalTime.parse(timeRaw);
        } catch (DateTimeParseException e) {
            return "Please enter a valid date and time.";
        }
        return null;
    }

    private int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
