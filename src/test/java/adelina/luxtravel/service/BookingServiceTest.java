package adelina.luxtravel.service;

import adelina.luxtravel.domain.*;
import adelina.luxtravel.domain.transport.Airplane;
import adelina.luxtravel.domain.transport.Transport;
import adelina.luxtravel.domain.wrapper.Date;
import adelina.luxtravel.domain.wrapper.DepartureDestination;
import adelina.luxtravel.domain.wrapper.ExcursionTransport;
import adelina.luxtravel.enumeration.RoleType;
import adelina.luxtravel.enumeration.TransportClass;
import adelina.luxtravel.exception.InvalidArgumentException;
import adelina.luxtravel.exception.NonExistentItemException;
import adelina.luxtravel.repository.BookingRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    UserService userService;
    @Mock
    ExcursionService excursionService;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingService bookingService;
    static Booking expectedBooking;

    @BeforeAll
    static void init() {
        int reservedTicketsCount = 7;
        long bookingId = NumberUtils.LONG_ONE;
        double price = 456.32;
        LocalDateTime reservationDate = LocalDateTime.now();
        Excursion excursion = createExcursion();
        Transport transport = createTransport();
        ExcursionTransport excursionTransport = new ExcursionTransport(excursion, transport, price);
        User user = createUser();

        expectedBooking = new Booking(bookingId, user, excursionTransport, reservationDate, reservedTicketsCount);
    }

    @Test
    void save_BookingIsNull_ExceptionThrown() {
        Booking booking = null;

        assertThrows(InvalidArgumentException.class, () -> bookingService.save(booking));
    }

    @Test
    void save_ExcursionIsNull_ExceptionThrown() {
        double price = 212.34;
        int reservedTicketsCount = expectedBooking.getReservedTicketsCount();
        Transport transport = expectedBooking.getExcursionTransport().getTransport();
        Excursion excursion = null;
        ExcursionTransport excursionTransport = new ExcursionTransport(excursion, transport, price);
        User user = expectedBooking.getUser();
        Booking booking = new Booking(excursionTransport, user, reservedTicketsCount);

        assertThrows(InvalidArgumentException.class, () -> bookingService.save(booking));
    }

    @Test
    void save_ValidData_SavedBooking() {
        ExcursionTransport excursionTransport = expectedBooking.getExcursionTransport();
        Excursion excursion = excursionTransport.getExcursion();
        long excursionId = excursion.getId();
        long bookingId = expectedBooking.getId();

        when(excursionService.findById(excursionId)).thenReturn(excursion);
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expectedBooking));

        Booking actualBooking = bookingService.save(expectedBooking);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void save_ReservedTicketsCountIsZero_ExceptionThrown() {
        double price = 212.34;
        int reservedTicketsCount = NumberUtils.INTEGER_ZERO;
        Transport transport = expectedBooking.getExcursionTransport().getTransport();
        Excursion excursion = null;
        ExcursionTransport excursionTransport = new ExcursionTransport(excursion, transport, price);
        User user = expectedBooking.getUser();
        Booking booking = new Booking(excursionTransport, user, reservedTicketsCount);

        assertThrows(InvalidArgumentException.class, () -> bookingService.save(booking));
    }

    @Test
    void findById_IdIsNegative_ExceptionThrown() {
        long negativeId = Long.MIN_VALUE;

        assertThrows(InvalidArgumentException.class, () -> bookingService.findById(negativeId));
    }

    @Test
    void findById_BookingWithGivenIdDoesNotExist_ExceptionThrown() {
        long id = NumberUtils.LONG_ONE + NumberUtils.LONG_ONE;

        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> bookingService.findById(id));
    }

    @Test
    void findById_BookingWithGivenIdExists_ExceptionThrown() {
        long id = expectedBooking.getId();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(expectedBooking));

        Booking actualBooking = bookingService.findById(id);

        assertEquals(expectedBooking.getId(), actualBooking.getId());
    }

    @Test
    void findAllUserBookings_UsernameIsEmpty_ExceptionThrown() {
        String emptyUsername = StringUtils.EMPTY;

        assertThrows(InvalidArgumentException.class, () -> bookingService.findAllUserBookings(emptyUsername));
    }

    @Test
    void findAllUserBookings_BookingsForGivenUserDoNotExist_ExceptionThrown() {
        String username = "some-username";

        when(userService.findByUsername(username)).thenThrow(NonExistentItemException.class);

        assertThrows(NonExistentItemException.class, () -> bookingService.findAllUserBookings(username));
    }

    @Test
    void findAllUserBookings_BookingsForGivenUserExist_ListOfUserBookings() {
        User expectedUser = expectedBooking.getUser();
        String username = expectedUser.getUsername();
        List<Booking> expectedBookings = new ArrayList<>();

        expectedBookings.add(expectedBooking);

        when(userService.findByUsername(username)).thenReturn(expectedUser);
        when(bookingRepository.findAllUserBookings(username)).thenReturn(expectedBookings);

        List<Booking> actualBookings = bookingService.findAllUserBookings(username);

        assertEquals(expectedBookings.size(), actualBookings.size());
        assertTrue(expectedBookings.containsAll(actualBookings));
    }

    @Test
    void findAll_BookingsAreNotFound_ExceptionThrown() {
        List<Booking> emptyList = new ArrayList<>();

        when(bookingRepository.findAll()).thenReturn(emptyList);

        assertThrows(NonExistentItemException.class, () -> bookingService.findAll());
    }

    @Test
    void findAll_BookingsExist_ListOfAllBookings() {
        List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        List<Booking> actualBookings = bookingService.findAll();

        assertEquals(expectedBookings.size(), actualBookings.size());
        assertTrue(expectedBookings.containsAll(actualBookings));
    }

    @Test
    void updateReservedTicketsCount_BookingIdIsNegative_ExceptionThrown() {
        long negativeId = NumberUtils.LONG_MINUS_ONE;
        int reservedTicketsCount = 7;

        assertThrows(InvalidArgumentException.class, () -> bookingService.updateReservedTicketsCount(negativeId, reservedTicketsCount));
    }

    @Test
    void updateReservedTicketsCount_ReservedTicketsCountIsZero_ExceptionThrown() {
        long id = expectedBooking.getId();
        int reservedTicketsCount = NumberUtils.INTEGER_ZERO;

        assertThrows(InvalidArgumentException.class, () -> bookingService.updateReservedTicketsCount(id, reservedTicketsCount));
    }

    @Test
    void updateReservedTicketsCount_ReservedTicketsCountIsMoreThanTheAvailable_ExceptionThrown() {
        long id = expectedBooking.getId();
        int reservedTicketsCount = Integer.MAX_VALUE;

        when(bookingRepository.findById(id)).thenReturn(Optional.of(expectedBooking));

        assertThrows(InvalidArgumentException.class, () -> bookingService.updateReservedTicketsCount(id, reservedTicketsCount));
    }

    @Test
    void deleteById_IdIsZero_ExceptionThrown() {
        long zeroId = NumberUtils.LONG_ZERO;

        assertThrows(InvalidArgumentException.class, () -> bookingService.deleteById(zeroId));
    }

    @Test
    void deleteById_BookingWithGivenIdDoesNotExist_ExceptionThrown() {
        long nonExistentId = 45;

        when(bookingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NonExistentItemException.class, () -> bookingService.deleteById(nonExistentId));
    }

    @AfterAll
    static void clear() {
        expectedBooking = null;
    }

    static Excursion createExcursion() {
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

        return new Excursion(id, departureDestination, date, excursionTransports, availableTicketsCount);
    }

    static User createUser() {
        long id = 5;
        String username = "username";
        String email = "email@gmail.com";
        String password = "password";
        Role role = new Role(RoleType.USER);

        return new User(id, username, email, password, role);
    }

    static Transport createTransport() {
        long id = NumberUtils.LONG_ONE;
        TransportClass transportClass = TransportClass.ECONOMY;

        return new Airplane(id, transportClass);
    }
}