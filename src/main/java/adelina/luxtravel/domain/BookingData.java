package adelina.luxtravel.domain;

import adelina.luxtravel.domain.transport.Transport;
import adelina.luxtravel.domain.transport.TransportClass;

import static adelina.luxtravel.utility.Constants.MINUTE;
import static adelina.luxtravel.utility.Constants.TEN_PERCENT;

import adelina.luxtravel.domain.wrapper.*;
import adelina.luxtravel.exception.FailedInitializationException;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "booking_data")
@Getter
public class BookingData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    DepartureDestination departureDestination;
    @Embedded
    Date date;
    @OneToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;
    @Column(name = "count_available_tickets", nullable = false)
    private int availableTicketsCount;
    @Column(name = "price", nullable = false, precision = 6, scale = 2)
    private double price;

    public BookingData(BookingData bookingData) {
        this(bookingData.id, bookingData.departureDestination,
                bookingData.transport, bookingData.date,
                bookingData.availableTicketsCount);
    }

    public BookingData(long id, DepartureDestination departureDestination,
                       Transport transport, Date date, int availableTicketsCount) {
        this(transport, departureDestination, date, availableTicketsCount);
        this.id = id;
    }

    public BookingData(Transport transport, DepartureDestination departureDestination,
                       Date date, int availableTicketsCount) {
        initializeFields(transport, departureDestination, date, availableTicketsCount);
    }

    private void initializeFields(Transport transport, DepartureDestination departureDestination,
                                  Date date, int availableTicketsCount) {
        if (transport == null) {
            throw new FailedInitializationException("Invalid transport");
        } else if (date == null) {
            throw new FailedInitializationException("Invalid dates");
        } else if (departureDestination == null) {
            throw new FailedInitializationException("Invalid source or destination");
        } else {
            this.date = date;
            this.departureDestination = departureDestination;
            this.transport = transport;
            this.availableTicketsCount = availableTicketsCount;
            setPrice();
        }
    }

    private void setPrice() {
        TransportClass transportClass = transport.getTransportClass();
        double priceCoefficient = transportClass.getPriceCoefficient();
        TravelingPoint departurePoint = departureDestination.getDeparturePoint();
        TravelingPoint destinationPoint = departureDestination.getDestinationPoint();

        LocalTime localTime = transport.calculateDuration(departurePoint, destinationPoint);

        price = ((localTime.getHour() * MINUTE + localTime.getMinute()) / priceCoefficient) * TEN_PERCENT;
    }
}