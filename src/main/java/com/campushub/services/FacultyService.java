package com.campushub.services;

import com.campushub.dao.FacultyDAO;
import com.campushub.models.Faculty;
import java.sql.SQLException;
import java.util.List;

/**
 * Service managing Faculty member records and credentials.
 */
public class FacultyService {

    private final FacultyDAO facultyDAO;

    public FacultyService() {
        this.facultyDAO = new FacultyDAO();
    }

    public FacultyService(FacultyDAO facultyDAO) {
        this.facultyDAO = facultyDAO;
    }

    public boolean registerFaculty(Faculty f) throws SQLException, IllegalArgumentException {
        if (f == null) {
            throw new IllegalArgumentException("Faculty object cannot be null.");
        }
        if (f.getEmpCode() == null || f.getEmpCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee Code is mandatory.");
        }
        if (f.getName() == null || f.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty Name is mandatory.");
        }
        if (f.getEmail() == null || !f.getEmail().contains("@")) {
            throw new IllegalArgumentException("Valid official email is required.");
        }

        return facultyDAO.insertFaculty(f);
    }

    public boolean updateFaculty(Faculty f) throws SQLException {
        if (f == null || f.getFacultyId() <= 0) {
            throw new IllegalArgumentException("Invalid faculty ID for update.");
        }
        return facultyDAO.updateFaculty(f);
    }

    public boolean deleteFaculty(int facultyId) throws SQLException {
        return facultyDAO.deleteFaculty(facultyId);
    }

    public List<Faculty> listAllFaculty() {
        return facultyDAO.getAllFaculty();
    }

    public int getTotalFacultyCount() {
        return facultyDAO.countFaculty();
    }
}

