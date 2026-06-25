package Domain.Workers;

import DAO.Transportation.LocationDAO;
import Domain.Transportation.Location;
import Service.Transportation.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import DB.DatabaseManager;
import static org.junit.jupiter.api.Assertions.*;

public class ShiftJobsFacadeTest {
    private ShiftJobs shiftJobs;
    private LocalDate testDate;
    private Location testLocation;
    private LocationService locationService;
    @BeforeEach
    public void setUp() {

        locationService = new LocationService(new LocationDAO(DatabaseManager.getConnection()) );
        testDate = LocalDate.of(2056, 4, 7);
        shiftJobs = new ShiftJobs(testDate, true, testLocation);
    }

    @Test
    public void testAddCasheerSuccess() {
        String result = shiftJobs.addJob(0);
        assertEquals("succeed, added job casheer to shift", result);
    }

    @Test
    public void testAddShopkeeperSuccess() {
        String result = shiftJobs.addJob(1);
        assertEquals("succeed, added job shop keeper to shift", result);
    }

    @Test
    public void testAddInvalidJob() {
        String result = shiftJobs.addJob(99);
        assertEquals("faild, job not founded", result);
    }

    @Test
    public void testRemoveCasheerSuccess() {
        shiftJobs.addJob(0);
        String result = shiftJobs.removeJob(0);
        assertEquals("succeed, removed job casheer from shift", result);
    }

    @Test
    public void testRemoveCasheerFailWhenEmpty() {
        String result = shiftJobs.removeJob(0);
        assertEquals("failed to lower casheer jobs, because it already 0", result);
    }

    @Test
    public void testRemoveShopkeeperFailWhenEmpty() {
        String result = shiftJobs.removeJob(1);
        assertEquals("failed to lower shop keeper jobs, because it already 0", result);
    }

    @Test
    public void testRemoveInvalidJob() {
        String result = shiftJobs.removeJob(5);
        assertEquals("failed, job not founded", result);
    }

    @Test
    public void testContainAllJobsTrue() {
        shiftJobs.addJob(0);
        shiftJobs.addJob(1);

        List<Integer> jobsList = Arrays.asList(0, 1);
        assertTrue(shiftJobs.containAllJobs(jobsList));
    }

    @Test
    public void testContainAllJobsFalseMissing() {
        shiftJobs.addJob(0);
        shiftJobs.addJob(0);

        List<Integer> jobsList = Arrays.asList(0);
        assertFalse(shiftJobs.containAllJobs(jobsList));
    }

    @Test
    public void testGetShiftDate() {
        assertEquals(testDate, shiftJobs.getShift().getDate());
    }

    @Test
    public void testToStringNotEmpty() {
        shiftJobs.addJob(0);
        String output = shiftJobs.toString();
        assertFalse(output.isEmpty());
        assertTrue(output.contains("CASHEER"));
    }
}
