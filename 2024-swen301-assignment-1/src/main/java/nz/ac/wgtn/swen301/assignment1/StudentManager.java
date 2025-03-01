package nz.ac.wgtn.swen301.assignment1;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nz.ac.wgtn.swen301.studentdb.*;

import java.sql.*;
import java.util.*;

/**
 * A student manager providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {
        StudentDB.init();
    }
    private static final Cache<String, Student> students = CacheBuilder.newBuilder().build();
    private static final Cache<String, Degree> degrees = CacheBuilder.newBuilder().build();
    // DO NOT REMOVE BLOCK ENDS HERE

    // THE FOLLOWING METHODS MUST BE IMPLEMENTED :

    static void reset() {
        students.cleanUp();
        degrees.cleanUp();
    }

    private static Connection connection = null;
    private static PreparedStatement getStudentStatement = null;

    private static Statement createStatement() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:derby:memory:studentdb");
                getStudentStatement = connection.prepareStatement("select STUDENTS.id, STUDENTS.name, STUDENTS.first_name, DEGREES.id as d_id, DEGREES.name as d_name from STUDENTS left join DEGREES on STUDENTS.degree = DEGREES.id where STUDENTS.id = ?");
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student fetchStudent(String id) throws NoSuchRecordException {
        Student s = students.getIfPresent(id);
        if (s != null) return s;
        try (Statement stmt = createStatement()) {
            getStudentStatement.setString(1, id);
            try (ResultSet rs = getStudentStatement.executeQuery()) {
                if (!rs.next()) throw new NoSuchRecordException("No record for student " + id);
                Student ns = new Student(rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("first_name"),
                        new Degree(
                                rs.getString("d_id"),
                                rs.getString("d_name")
                        )
                );
                students.put(ns.getId(), ns);
                return ns;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id
     * @return
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree fetchDegree(String id) throws NoSuchRecordException {
        Degree d = degrees.getIfPresent(id);
        if (d != null) return d;
        try (Statement stmt = createStatement()) {
            ResultSet rs = stmt.executeQuery("select id, name from DEGREES where id = '" + id + "'");
            if (rs.next()) {
                var nd = new Degree(rs.getString("id"), rs.getString("name"));
                degrees.put(nd.getId(), nd);
                return nd;
            } else throw new NoSuchRecordException("No record for degree " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testRemove
     */
    public static void remove(Student student) throws NoSuchRecordException {
        try (Statement stmt = createStatement()) {
            int rs = stmt.executeUpdate("delete from students where id = '" + student.getId() + "'");
            if (rs == 0) throw new NoSuchRecordException("No record for student " + student.getId());
            students.invalidate(student.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testUpdate (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException {
        try (Statement stmt = createStatement()) {
            int rs = stmt.executeUpdate("update students set name = '" + student.getName() + "', first_name = '" + student.getFirstName() + "', degree = '" + student.getDegree().getId() + "' where id = '" + student.getId() + "'");
            if (rs == 0) throw new NoSuchRecordException("No record for student " + student.getId());
            students.invalidate(student.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not being used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name
     * @param firstName
     * @param degree
     * @return a freshly created student instance
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testNewStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student newStudent(String name,String firstName,Degree degree) {
        Student student = new Student(UUID.randomUUID().toString().substring(0,6), name, firstName, degree);
        try (Statement stmt = createStatement()) {
            int rc = stmt.executeUpdate("insert into STUDENTS values ('" + student.getId() + "', '" + firstName + "', '" + name + "',  '" + degree.getId() + "')");
            if (rc == 0) return null;
            return student;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all students currently being used in the database.
     */
    public static Collection<Student> fetchAllStudents() {
        try (Statement stmt = createStatement()) {
            ResultSet rs = stmt.executeQuery("select STUDENTS.id, STUDENTS.name, STUDENTS.first_name, DEGREES.id as d_id, DEGREES.name as d_name from STUDENTS left join DEGREES on STUDENTS.degree = DEGREES.id");
            Collection<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(new Student(rs.getString("id"), rs.getString("name"), rs.getString("first_name"), new Degree(rs.getString("d_id"), rs.getString("d_name"))));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all student ids currently being used in the database.
     * @return
     * This functionality is to be tested in nz.ac.wgtn.swen301.assignment1.TestStudentManager::testFetchAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> fetchAllStudentIds() {
        try (Statement stmt = createStatement()) {
            ResultSet rs = stmt.executeQuery("select id from students");
            ArrayList<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("id"));
            }
            return ids;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
