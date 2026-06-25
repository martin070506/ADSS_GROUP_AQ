package Domain.Workers;

import DAO.Workers.WorkerDAO;
import DTO.Workers.WorkerDTO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkersFacadeTest {

    private WorkersFacade createFacade() {
        WorkersFacade facade = new WorkersFacade();
        setField(facade, "workers_dao", new FakeWorkerDAO());
        return facade;
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addDriver() {
        WorkersFacade facade = createFacade();

        String result = facade.addDriver(
                "David",
                1,
                "123456",
                10000,
                "monthly",
                LocalDate.of(2024, 1, 1),
                true,
                555
        );

        assertEquals("success, worker with id: 1 has been added.", result);
        assertTrue(facade.hasWorker(1));
        assertTrue(facade.isDriver(1));
        assertEquals(555, facade.getLicense(1));
    }

    @Test
    void removeWorker() {
        WorkersFacade facade = createFacade();

        facade.addDriver(
                "David",
                1,
                "123456",
                10000,
                "monthly",
                LocalDate.of(2024, 1, 1),
                true,
                555
        );

        String result = facade.removeWorker(1);

        assertEquals("success, worker with id: 1 has been removed.", result);
        assertFalse(facade.hasWorker(1));
    }

    @Test
    void editWorkerStartDate() {
        WorkersFacade facade = createFacade();

        facade.addDriver(
                "David",
                1,
                "123456",
                10000,
                "monthly",
                LocalDate.of(2024, 1, 1),
                true,
                555
        );

        LocalDate newDate = LocalDate.of(2025, 1, 1);

        String result = facade.editWorkerStartDate(1, newDate);

        assertEquals("success, worker's start date has been changed to: 2025-01-01", result);
    }

    private static class FakeWorkerDAO extends WorkerDAO {
        public FakeWorkerDAO() {
            super(null);
        }

        @Override
        public void addWorker(WorkerDTO worker) throws SQLException {
        }

        @Override
        public void deleteWorker(int id) throws SQLException {
        }

        @Override
        public void updateStartJobDate(int id, LocalDate startDate) throws SQLException {
        }
    }
}