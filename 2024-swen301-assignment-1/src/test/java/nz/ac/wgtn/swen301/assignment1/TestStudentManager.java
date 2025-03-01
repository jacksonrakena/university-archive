package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentManager, to be extended.
 */
public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @BeforeEach
    public  void init () {
        StudentDB.init();
        StudentManager.reset();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    public void assertStudentEquals(Student expected, Student actual) {
        assertEquals(expected, actual);
        assertEquals(expected.getDegree(), actual.getDegree());
    }

    @Test
    public void testFetchStudent() throws NoSuchRecordException {
        Student student = StudentManager.fetchStudent("id42");
        Student copy = StudentManager.fetchStudent("id42");
        assertSame(student, copy);
        assertNotNull(student);
        Student compare = new Student("id42", "Tipene", "Sue", new Degree("deg2", "BE Cybersecurity"));
        assertStudentEquals(compare, student);
    }

    @Test
    public void testFetchDegree() throws NoSuchRecordException {
        Degree degree = StudentManager.fetchDegree("deg0");
        Degree copy = StudentManager.fetchDegree("deg0");
        assertSame(degree, copy);
        assertNotNull(degree);
        assertEquals("BSc Computer Science", degree.getName());
        assertEquals("deg0", degree.getId());
    }

    @Test
    public void testRemove() throws NoSuchRecordException {
        StudentManager.remove(new Student("id0", "", "", null));
        assertThrows(NoSuchRecordException.class, () -> StudentManager.fetchStudent("id0"));
    }

    @Test
    public void testUpdate() throws NoSuchRecordException {
        StudentManager.update(new Student("id0", "Rakena", "Jackson", new Degree("deg0", "")));

        var expected = new Student("id0", "Rakena", "Jackson", new Degree("deg0", "BSc Computer Science"));
        var actual = StudentManager.fetchStudent("id0");
        assertStudentEquals(expected, actual);
    }

    @Test
    public void testNewStudent() throws NoSuchRecordException {
        Student s = StudentManager.newStudent("Rakena", "Jackson", new Degree("deg0", "BSc Computer Science"));

        var actual = StudentManager.fetchStudent(s.getId());
        assertStudentEquals(s, actual);
    }

    @Test
    public void testFetchAllStudentIds() {
        Collection<String> ids = StudentManager.fetchAllStudentIds();
        assertEquals(10000, ids.size());
    }

    @Test
    @Timeout(5000)
    public void testPerformance() throws NoSuchRecordException {
        long DELTA_OPS = 10_000;

        Random r = new Random();
        List<String> ids = StudentManager.fetchAllStudentIds().stream().toList();
        List<String> degrees = StudentManager.fetchAllStudents().stream().map(e -> e.getDegree().getId()).distinct().toList();

        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < DELTA_OPS; i++) {
            int op = r.nextInt(2);
            switch (op) {
                case 0:
                    StudentManager.fetchStudent(ids.get(r.nextInt(0,ids.size())));
                    break;
                case 1:
                    StudentManager.fetchDegree(degrees.get(r.nextInt(0,degrees.size())));
                    break;
            }

        }
        long deltaTime = System.currentTimeMillis() - startTimeMillis;

        float rps = ((float)DELTA_OPS/(float)deltaTime)*1000;
        System.out.println("Completed " + DELTA_OPS + " requests in " + deltaTime + "ms");
        System.out.println("Requests per second: " + rps);
        assertTrue(rps >= 500, "Only reached " + rps + " requests per second, expected >= 500 RPS");
    }
}
