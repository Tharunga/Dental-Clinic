package com.sunrisedental.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.sunrisedental.dao.DashboardDAO;
import com.sunrisedental.model.NamedMetric;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdminAccess.transferFlash(req);
        try {
            req.setAttribute("appointmentsToday", dashboardDAO.countToday());
            req.setAttribute("bookedToday", dashboardDAO.countBookedToday());
            req.setAttribute("completedToday", dashboardDAO.countCompletedToday());
            req.setAttribute("cancelledThisMonth", dashboardDAO.countCancelledThisMonth());
            req.setAttribute("activePatients", dashboardDAO.countActivePatients());
            req.setAttribute("revenueToday", dashboardDAO.revenueToday());
            req.setAttribute("revenueThisMonth", dashboardDAO.revenueThisMonth());
            req.setAttribute("avgBillThisMonth", dashboardDAO.avgBillThisMonth());

            List<NamedMetric> aptLast7 = dashboardDAO.appointmentsLast7Days();
            List<NamedMetric> revLast7 = dashboardDAO.revenueLast7Days();
            List<NamedMetric> statusMix = dashboardDAO.statusMixThisMonth();
            List<NamedMetric> revByTreatment = dashboardDAO.revenueByTreatmentThisMonth();

            req.setAttribute("appointmentsLast7Days", aptLast7);
            req.setAttribute("revenueLast7Days", revLast7);
            req.setAttribute("statusMixThisMonth", statusMix);
            req.setAttribute("revenueByTreatmentThisMonth", revByTreatment);

            req.setAttribute("todaySchedule", dashboardDAO.findTodaySchedule());
            req.setAttribute("upcomingAppointments", dashboardDAO.findUpcomingBooked(8));
            req.setAttribute("dentistWorkload", dashboardDAO.dentistWorkloadThisMonth());

            req.setAttribute("chartAptLabels", toJsonLabels(aptLast7));
            req.setAttribute("chartAptCounts", toJsonCounts(aptLast7));
            req.setAttribute("chartRevLabels", toJsonLabels(revLast7));
            req.setAttribute("chartRevAmounts", toJsonAmounts(revLast7));
            req.setAttribute("chartStatusLabels", toJsonLabels(statusMix));
            req.setAttribute("chartStatusCounts", toJsonCounts(statusMix));
            req.setAttribute("chartTreatmentLabels", toJsonLabels(revByTreatment));
            req.setAttribute("chartTreatmentAmounts", toJsonAmounts(revByTreatment));
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dashboard data. Please check the database connection.");
            req.setAttribute("appointmentsToday", 0);
            req.setAttribute("bookedToday", 0);
            req.setAttribute("completedToday", 0);
            req.setAttribute("cancelledThisMonth", 0);
            req.setAttribute("activePatients", 0);
            req.setAttribute("revenueToday", BigDecimal.ZERO);
            req.setAttribute("revenueThisMonth", BigDecimal.ZERO);
            req.setAttribute("avgBillThisMonth", BigDecimal.ZERO);
            req.setAttribute("todaySchedule", Collections.emptyList());
            req.setAttribute("upcomingAppointments", Collections.emptyList());
            req.setAttribute("dentistWorkload", Collections.emptyList());
            req.setAttribute("chartAptLabels", "[]");
            req.setAttribute("chartAptCounts", "[]");
            req.setAttribute("chartRevLabels", "[]");
            req.setAttribute("chartRevAmounts", "[]");
            req.setAttribute("chartStatusLabels", "[]");
            req.setAttribute("chartStatusCounts", "[]");
            req.setAttribute("chartTreatmentLabels", "[]");
            req.setAttribute("chartTreatmentAmounts", "[]");
        }
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }

    private static String toJsonLabels(List<NamedMetric> metrics) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < metrics.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('"').append(escapeJson(metrics.get(i).getLabel())).append('"');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String toJsonCounts(List<NamedMetric> metrics) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < metrics.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(metrics.get(i).getCount());
        }
        sb.append(']');
        return sb.toString();
    }

    private static String toJsonAmounts(List<NamedMetric> metrics) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < metrics.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            BigDecimal amt = metrics.get(i).getAmount();
            sb.append(amt != null ? amt.toPlainString() : "0");
        }
        sb.append(']');
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
