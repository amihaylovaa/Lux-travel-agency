package adelina.luxtravel.service;

import adelina.luxtravel.domain.BookingData;
import adelina.luxtravel.domain.TravelingPoint;
import adelina.luxtravel.domain.transport.Transport;
import adelina.luxtravel.domain.wrapper.*;
import adelina.luxtravel.exception.*;
import adelina.luxtravel.repository.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingDataService {
    private BookingDataRepository bookingDataRepository;
    private TransportRepository transportRepository;
    private TravelingPointRepository travelingPointRepository;

    @Autowired
    public BookingDataService(BookingDataRepository bookingDataRepository, TransportRepository transportRepository, TravelingPointRepository travelingPointRepository) {
        this.bookingDataRepository = bookingDataRepository;
        this.transportRepository = transportRepository;
        this.travelingPointRepository = travelingPointRepository;
    }

    public BookingData save(BookingData bookingData) {
        validateBookingData(bookingData);
        return bookingDataRepository.save(bookingData);
    }

    public BookingData findById(long id) {
        if (id <= NumberUtils.LONG_ZERO) {
            throw new InvalidArgumentException("Invalid id");
        }
        return getBookingData(id);
    }

    public List<BookingData> findByDates(LocalDate from, LocalDate to) {
        validateDates(from, to);

        List<BookingData> bookingData = bookingDataRepository.findByDates(from, to);

        if (ObjectUtils.isEmpty(bookingData)) {
            throw new NonExistentItemException("There are no booking data for these days");
        }
        return bookingData;
    }

    public List<BookingData> findAll() {
        List<BookingData> bookingData = bookingDataRepository.findAll();

        if (ObjectUtils.isEmpty(bookingData)) {
            throw new NonExistentItemException("No booking data found");
        }
        return bookingData;
    }

    public BookingData updateTransport(long bookingDataId, Transport transport) {
        if (bookingDataId <= NumberUtils.LONG_ZERO || transport == null || transport.getId() <= NumberUtils.LONG_ZERO) {
            throw new InvalidArgumentException("Update can not be executed, invalid parameters");
        }

        long transportId = transport.getId();

        bookingDataRepository.updateTransport(transportId, bookingDataId);
        return findById(bookingDataId);
    }

    public void deleteById(long id) {
        if (id <= NumberUtils.LONG_ZERO) {
            throw new InvalidArgumentException("Invalid id");
        }
        bookingDataRepository.deleteById(id);
    }

    public void deleteAll() {
        bookingDataRepository.deleteAll();
    }

    private void validateBookingData(BookingData bookingData) {
        if (bookingData == null) {
            throw new InvalidArgumentException("Invalid booking data");
        }
        validateBookingDataFields(bookingData);
    }

    private void validateBookingDataFields(BookingData bookingData) {
        Date date = bookingData.getDate();
        LocalDate from = date.getFromDate();
        LocalDate to = date.getToDate();

        validateDates(from, to);

        StartingEndingPoint startingEndingPoint = bookingData.getStartingEndingPoint();

        if (startingEndingPoint == null) {
            throw new InvalidArgumentException("Invalid traveling points");
        }

        Transport transport = bookingData.getTransport();

        if (transport == null) {
            throw new InvalidArgumentException("Invalid transport");
        }

        TravelingPoint startingPoint = startingEndingPoint.getStartingPoint();
        TravelingPoint targetPoint = startingEndingPoint.getTargetPoint();

        validateFieldsExist(startingPoint, targetPoint, transport);
    }

    private void validateFieldsExist(TravelingPoint startingPoint, TravelingPoint targetPoint, Transport transport) {
        long transportId = transport.getId();
        long startingPointId = startingPoint.getId();
        long targetPointId = targetPoint.getId();
        Optional<TravelingPoint> fromPoint = travelingPointRepository.findById(startingPointId);
        Optional<TravelingPoint> toPoint = travelingPointRepository.findById(targetPointId);
        Optional<Transport> transports = transportRepository.findById(transportId);

        if (!fromPoint.isPresent()) {
            throw new NonExistentItemException("Source traveling point does not exist");
        }
        if (!toPoint.isPresent()) {
            throw new NonExistentItemException("Destination traveling point does not exist");
        }
        if (!transports.isPresent()) {
            throw new NonExistentItemException("Transport does not exist");
        }
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isEqual(to)
                || from.isAfter(to) || from.isBefore(LocalDate.now())) {
            throw new InvalidArgumentException("Invalid dates");
        }
    }

    private BookingData getBookingData(long id) {
        BookingData bookingData = bookingDataRepository.findById(id);

        if (bookingData == null) {
            throw new NonExistentItemException("This booking data does not exist");
        }
        return bookingData;
    }
}