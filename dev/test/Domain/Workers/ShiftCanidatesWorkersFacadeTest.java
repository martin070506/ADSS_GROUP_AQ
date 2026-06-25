package Domain.Workers;

import DAO.Workers.ShiftCandidateIdDAO;
import DAO.Workers.ShiftCandidatesDAO;
import DTO.Workers.ShiftCandidateIdDTO;
import DTO.Workers.ShiftCandidatesDTO;
import Domain.Transportation.Location;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ShiftCanidatesWorkersFacadeTest {

    private ShiftCanidatesWorkersFacade createFacade() {
        ShiftCanidatesWorkersFacade facade = new ShiftCanidatesWorkersFacade(null, null);

        setField(facade, "shift_candidates_dao", new FakeShiftCandidatesDAO());
        setField(facade, "shift_candidate_id_dao", new FakeShiftCandidateIdDAO());

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
    void addCandidate() {
        ShiftCanidatesWorkersFacade facade = createFacade();

        LocalDate date = LocalDate.now().plusDays(1);
        Location location = new Location(1, "Address", "0500000000", "Branch");

        String result = facade.addCandidate(date, true, location, 1);

        assertEquals("Success, a new candidate with id: 1 has been added.", result);
        assertTrue(facade.containWorker(date, true, location, 1));
    }

    @Test
    void removeCandidate() {
        ShiftCanidatesWorkersFacade facade = createFacade();

        LocalDate date = LocalDate.now().plusDays(1);
        Location location = new Location(1, "Address", "0500000000", "Branch");

        facade.addCandidate(date, true, location, 1);

        String result = facade.removeCandidate(date, true, location, 1);

        assertEquals("succeed, a new candidate with id: 1 has been removed.", result);
        assertFalse(facade.containWorker(date, true, location, 1));
    }

    private static class FakeShiftCandidatesDAO extends ShiftCandidatesDAO {
        public FakeShiftCandidatesDAO() {
            super(null);
        }

        @Override
        public void add(ShiftCandidatesDTO dto) {
        }
    }

    private static class FakeShiftCandidateIdDAO extends ShiftCandidateIdDAO {
        public FakeShiftCandidateIdDAO() {
            super(null);
        }

        @Override
        public void add(ShiftCandidateIdDTO dto) {
        }

        @Override
        public void remove(ShiftCandidateIdDTO dto) {
        }
    }
}