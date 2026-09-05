package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/appointments/register")
public class AppointmentRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{9,15}$");

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final DentistDAO dentistDAO = new DentistDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        loadLookups(req);
        req.getRequestDispatcher("/appointment-register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        loadLookups(req);

        String patientName = trim(req.getParameter("patientName"));
        String address = trim(req.getParameter("address"));
        String contact = trim(req.getParameter("contactNumber"));
        String dentistIdRaw = trim(req.getParameter("dentistId"));
        String treatmentIdRaw = trim(req.getParameter("treatmentId"));
        String dateRaw = trim(req.getParameter("appointmentDate"));
        String timeRaw = trim(req.getParameter("appointmentTime"));

        req.setAttribute("patientName", patientName);
        req.setAttribute("address", address);
        req.setAttribute("contactNumber", contact);
        req.setAttribute("dentistId", dentistIdRaw);
        req.setAttribute("treatmentId", treatmentIdRaw);
        req.setAttribute("appointmentDate", dateRaw);
        req.setAttribute("appointmentTime", timeRaw);

        String error = validate(patientName, address, contact, dentistIdRaw, treatmentIdRaw, dateRaw, timeRaw);
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/appointment-register.jsp").forward(req, resp);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateRaw);
            LocalTime time = LocalTime.parse(timeRaw);
            int dentistId = Integer.parseInt(dentistIdRaw);

            Dentist dentist = dentistDAO.findById(dentistId);
            if (dentist == null || !dentist.isActive()) {
                req.setAttribute("error", "Selected dentist is not available for new appointments.");
                req.getRequestDispatcher("/appointment-register.jsp").forward(req, resp);
                return;
            }

            if (appointmentDAO.slotTaken(dentistId, date, time)) {
                req.setAttribute("error", "This dentist already has an appointment at the selected date and time. Please choose another slot.");
                req.getRequestDispatcher("/appointment-register.jsp").forward(req, resp);
                return;
            }

            Appointment appointment = new Appointment();
            appointment.setPatientName(patientName);
            appointment.setAddress(address);
            appointment.setContactNumber(contact);
            appointment.setDentistId(dentistId);
            appointment.setTreatmentId(Integer.parseInt(treatmentIdRaw));
            appointment.setAppointmentDate(date);
            appointment.setAppointmentTime(time);

            String number = appointmentDAO.insert(appointment);
            User current = (User) req.getSession().getAttribute("user");
            systemLogDAO.logQuietly(current, "APPOINTMENT_REGISTER",
                    "Registered appointment " + number + " for " + appointment.getPatientName());
            req.getSession().setAttribute("success",
                    "Appointment registered successfully. Appointment number: " + number);
            resp.sendRedirect(req.getContextPath() + "/appointments/search?number=" + number);
        } catch (SQLException e) {
            if ("DOUBLE_BOOKING".equals(e.getMessage())) {
                req.setAttribute("error", "This dentist already has an appointment at the selected date and time.");
            } else {
                req.setAttribute("error", "Could not save the appointment. Please try again.");
            }
            req.getRequestDispatcher("/appointment-register.jsp").forward(req, resp);
        }
    }

    private void loadLookups(HttpServletRequest req) {
        try {
            req.setAttribute("dentists", dentistDAO.findAllActive());
            req.setAttribute("treatments", treatmentDAO.findAll());
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dentists or treatments from the database.");
        }
    }

    private String validate(String name, String address, String contact, String dentistId, String treatmentId,
            String dateRaw, String timeRaw) {
        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || dentistId.isEmpty()
                || treatmentId.isEmpty() || dateRaw.isEmpty() || timeRaw.isEmpty()) {
            return "Please complete all fields before saving.";
        }
        if (name.length() < 3) {
            return "Patient name must be at least 3 characters.";
        }
        if (!PHONE.matcher(contact).matches()) {
            return "Enter a valid contact number (9 to 15 digits).";
        }
        try {
            LocalDate date = LocalDate.parse(dateRaw);
            if (date.isBefore(LocalDate.now())) {
                return "Appointment date cannot be in the past.";
            }
            LocalTime.parse(timeRaw);
        } catch (DateTimeParseException e) {
            return "Please enter a valid date and time.";
        }
        return null;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
