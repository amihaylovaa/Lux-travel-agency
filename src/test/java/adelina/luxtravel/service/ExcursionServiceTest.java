package adelina.luxtravel.service;

import adelina.luxtravel.domain.Excursion;
import adelina.luxtravel.domain.TravelingPoint;
import adelina.luxtravel.domain.transport.Airplane;
import adelina.luxtravel.domain.transport.Bus;
import adelina.luxtravel.domain.transport.Transport;
import adelina.luxtravel.domain.wrapper.Date;
import adelina.luxtravel.domain.wrapper.DepartureDestination;
import adelina.luxtravel.domain.wrapper.ExcursionTransport;
import adelina.luxtravel.enumeration.TransportClass;
import adelina.luxtravel.exception.AlreadyExistingItemException;
import adelina.luxtravel.exception.FailedInitializationException;
import adelina.luxtravel.exception.InvalidArgumentException;
import adelina.luxtravel.exception.NonExistentItemException;
import adelina.luxtravel.repository.ExcursionRepository;
import adelina.luxtravel.repository.ExcursionTransportRepository;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcursionServiceTest {

    @Mock
    TravelingPointService travelingPointService;
    @Mock
    TransportService transportService;
    @Mock
    ExcursionRepository excursionRepository;
    @Mock
    ExcursionTransportRepository excursionTransportRepository;
    @InjectMocks
    ExcursionService excursionService;

    static Excursion expectedExcursion;

    @BeforeAll
    static void init() {
        int availableTicketsCount = 26;
        long id = NumberUtils.LONG_ONE;
        TravelingPoint departure = new TravelingPoint(1, "Vienna, Austria", 16.363449, 48.210033);
        TravelingPoint destination = new TravelingPoint(2, "Moscow, Russia", 37.618423, 55.751244);
        DepartureDestination departureDestination = new DepartureDestination(departure, destination);
        LocalDate startingDate = LocalDate.of(2022, 10, 7);
        LocalDate endingDate = LocalDate.of(2022, 10, 26);
        Date date = new Date(startingDate, endingDate);
        Transport transport = new Airplane(1, TransportClass.ECONOMY);
        ExcursionTransport excursionTransport = new ExcursionTransport(transport);
        List<ExcursionTransport> excursionTransports = new ArrayList<>();

        excursionTransports.add(excursionTransport);

        expectedExcursion = new Excursion(id, departureDestination, date, excursionTransports, availableTicketsCount);
    }

    @Test
    void save_ExcursionIsNull_ExceptionThrown() {
        Excursion excursion = null;

        assertThrows(InvalidArgumentException.class, () -> excursionService.save(excursion));
    }

    @Test
    void save_ValidData_SavedExcursion() {
        when(excursionRepository.save(expectedExcursion)).thenReturn(expectedExcursion);

        Excursion actualExcursion = excursionService.save(expectedExcursion);

        assertEquals(expectedExcursion, actualExcursion);
    }

    @Test
    void findById_IdIsZero_ExceptionThrown() {
        long invalidId = NumberUtils.LONG_ZERO;

        assertThrows(InvalidArgumentException.class, () -> excursionService.findById(invalidId));
    }

    @Test
    void findById_ExcursionWithGivenIdDoesNotExist_ExceptionThrown() {
        long id = NumberUtils.LONG_ONE;

        when(excursionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> excursionService.findById(id));
    }

    @Test
    void findById_ExcursionWithGivenIdExists_FoundExcursions() {
        long id = expectedExcursion.getId();

        when(excursionRepository.findById(id)).thenReturn(Optional.of(expectedExcursion));

        Excursion actualExcursion = excursionService.findById(id);

        assertEquals(expectedExcursion, actualExcursion);
    }

    @Test
    void findByDates_EndingDateIsBeforeStartingDate_ExceptionThrown() {
        LocalDate startingDate = LocalDate.of(2020, 5, 23);
        LocalDate endingDate = LocalDate.of(2020, 3, 12);

        assertThrows(InvalidArgumentException.class, () -> excursionService.findByDates(startingDate, endingDate));
    }

    @Test
    void findByDates_StartingDateIsNull_ExceptionThrown() {
        LocalDate startingDate = null;
        LocalDate endingDate = LocalDate.of(2020, 3, 12);

        assertThrows(InvalidArgumentException.class, () -> excursionService.findByDates(startingDate, endingDate));
    }

    @Test
    void findByDates_ExcursionsOnTheseDatesDoNotExist_ExceptionThrown() {
        LocalDate startingDate = LocalDate.of(2022, 11, 3);
        LocalDate endingDate = LocalDate.of(2022, 11, 12);
        List<Excursion> emptyList = new ArrayList<>();

        when(excursionRepository.findByDates(startingDate, endingDate)).thenReturn(emptyList);

        assertThrows(NonExistentItemException.class, () -> excursionService.findByDates(startingDate, endingDate));
    }

    @Test
    void findByDates_ValidDate_ListOfFoundExcursions() {
        Date date = expectedExcursion.getDate();
        LocalDate startingDate = date.getFromDate();
        LocalDate endingDate = date.getToDate();
        List<Excursion> expectedExcursions = new ArrayList<>();
        expectedExcursions.add(expectedExcursion);

        when(excursionRepository.findByDates(startingDate, endingDate)).thenReturn(expectedExcursions);

        List<Excursion> actualExcursions = excursionService.findByDates(startingDate, endingDate);

        assertTrue(expectedExcursions.containsAll(actualExcursions));
    }

    @Test
    void findExcursionAvailableTicketsCountById_IdIsNegative_ExceptionThrown() {
        long negativeId = NumberUtils.LONG_MINUS_ONE;

        assertThrows(InvalidArgumentException.class, () -> excursionService.findExcursionAvailableTicketsCountById(negativeId));
    }

    @Test
    void findExcursionAvailableTicketsCountById_ExcursionWithGivenIdDoesNotExist_ExceptionThrown() {
        long id = NumberUtils.LONG_ONE;

        when(excursionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> excursionService.findExcursionAvailableTicketsCountById(id));
    }

    @Test
    void findExcursionAvailableTicketsCountById_ExcursionWithGivenIdExists_AvailableTicketsCountForTheExcursion() {
        long id = expectedExcursion.getId();
        int expectedAvailableTicketsCount = expectedExcursion.getAvailableTicketsCount();

        when(excursionRepository.findById(id)).thenReturn(Optional.of(expectedExcursion));
        when(excursionRepository.findExcursionAvailableTicketsCountById(id)).thenReturn(expectedAvailableTicketsCount);

        int actualAvailableTicketsCount = excursionService.findExcursionAvailableTicketsCountById(id);

        assertEquals(expectedAvailableTicketsCount, actualAvailableTicketsCount);
    }

    @Test
    void findAll_ExcursionsAreNotFound_ExceptionThrown() {
        List<Excursion> emptyList = new ArrayList<>();

        when(excursionRepository.findAll()).thenReturn(emptyList);

        assertThrows(NonExistentItemException.class, () -> excursionService.findAll());
    }

    @Test
    void updateDates_NewDatesAreNull_ExceptionThrown() {
        long id = expectedExcursion.getId();
        Date newDates = null;

        assertThrows(InvalidArgumentException.class, () -> excursionService.updateDates(id, newDates));
    }

    @Test
    void updateDates_NewDatesAreTheSameAsTheOldOnes_ExceptionThrown() {
        long id = expectedExcursion.getId();
        Date newDates = expectedExcursion.getDate();

        when(excursionRepository.findById(id)).thenReturn(Optional.of(expectedExcursion));

        assertThrows(AlreadyExistingItemException.class, () -> excursionService.updateDates(id, newDates));
    }

    @Test
    void reserveTickets_ExcursionIdIsZero_ExceptionThrown() {
        long excursionId = NumberUtils.LONG_ZERO;
        int reservedTicketsCount = 12;

        assertThrows(InvalidArgumentException.class, () -> excursionService.reserveTickets(excursionId, reservedTicketsCount));
    }

    @Test
    void reserveTickets_RequestedTicketsCountIsMoreThanTheAvailable_ExceptionThrown() {
        long excursionId = expectedExcursion.getId();
        int reservedTicketsCount = 56;

        when(excursionRepository.findById(excursionId)).thenReturn(Optional.of(expectedExcursion));

        assertThrows(InvalidArgumentException.class, () -> excursionService.reserveTickets(excursionId, reservedTicketsCount));
    }

    @Test
    void reserveTickets_ExcursionWithGivenIdDoesNotExist_ExceptionThrown() {
        long nonExistentId = expectedExcursion.getId() + 1;
        int reservedTicketsCount = 56;

        when(excursionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> excursionService.reserveTickets(nonExistentId, reservedTicketsCount));
    }

    @Test
    void reserveTickets_ValidData_NewAvailableTicketsCount() {
        long id = expectedExcursion.getId();
        int reservedTicketsCount = 5;
        int availableTicketsCount = expectedExcursion.getAvailableTicketsCount();
        int expectedAvailableTicketsCountAfterReservation = availableTicketsCount - reservedTicketsCount;

        when(excursionRepository.findById(id)).thenReturn(Optional.of(expectedExcursion));
        when(excursionService.reserveTickets(id, reservedTicketsCount)).thenReturn(expectedAvailableTicketsCountAfterReservation);

        int actualAvailableTicketsCountAfterReservation = excursionService.reserveTickets(id, availableTicketsCount);

        assertEquals(expectedAvailableTicketsCountAfterReservation, actualAvailableTicketsCountAfterReservation);
    }

    @Test
    void cancelTicketsReservation_IdIsNegative_ExceptionThrown() {
        long negativeId = Long.MIN_VALUE;
        int reservedTicketsCount = 5;

        assertThrows(InvalidArgumentException.class, () -> excursionService.cancelTicketsReservation(negativeId, reservedTicketsCount));
    }

    @Test
    void cancelTicketsReservation_ExcursionWithGivenIdDoesNotExist_ExceptionThrown() {
        long nonExistentId = Long.MAX_VALUE;
        int reservedTicketsCount = 5;

        when(excursionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> excursionService.cancelTicketsReservation(nonExistentId, reservedTicketsCount));
    }

    @Test
    void cancelTicketsReservation_ValidData_NewAvailableTicketsCount() {
        long id = expectedExcursion.getId();
        int reservedTicketsCount = 5;
        int availableTicketsCount = expectedExcursion.getAvailableTicketsCount();
        int expectedAvailableTicketsCountAfterCancellation = availableTicketsCount + reservedTicketsCount;

        when(excursionRepository.findById(id)).thenReturn(Optional.of(expectedExcursion));
        when(excursionService.cancelTicketsReservation(id, reservedTicketsCount)).thenReturn(expectedAvailableTicketsCountAfterCancellation);

        int actualAvailableTicketsCountAfterCancellation = excursionService.reserveTickets(id, availableTicketsCount);

        assertEquals(expectedAvailableTicketsCountAfterCancellation, actualAvailableTicketsCountAfterCancellation);
    }

    @Test
    void deleteById_IdIsZero_ExceptionThrown() {
        long id = NumberUtils.LONG_ZERO;

        assertThrows(InvalidArgumentException.class, () -> excursionService.deleteById(id));
    }

    @Test
    void deleteById_ExcursionWithGivenIdDoesNotExist_ExceptionThrown() {
        long nonExistentId = Long.MAX_VALUE - NumberUtils.LONG_ONE;

        when(excursionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> excursionService.deleteById(nonExistentId));
    }

    @AfterAll
    static void clear() {
        expectedExcursion = null;
    }
}