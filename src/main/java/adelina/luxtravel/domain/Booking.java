package adelina.luxtravel.domain;

import adelina.luxtravel.domain.transport.Transport;
import adelina.luxtravel.exception.FailedInitializationException;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalTime;

import static adelina.luxtravel.utility.Constants.MINUTE;


@Entity
@Table(name = "booking")
@Getter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "booking_data_id")
    private BookingData bookingData;
    @Column(name = "price", nullable = false, precision = 6, scale = 2)
    private double price;

    public Booking(double price, BookingData bookingData, User user) {
        initializeFields(price, bookingData, user);
    }

    public Booking(long id, double price, BookingData bookingData, User user) {
        this(price, bookingData, user);
        this.id = id;
    }

    public Booking(Booking booking) {
        this(booking.id, booking.price, booking.bookingData, booking.user);
    }

    private void initializeFields(double price, BookingData bookingData, User user) {
        if (bookingData == null) {
            throw new FailedInitializationException("Null booking date");
        } else if (user == null) {
            throw new FailedInitializationException("Invalid user");
        } else {
            this.user = user;
            this.bookingData = bookingData;
            calculatePrice();
        }
    }

    private void calculatePrice() {
        //  double durationInMinutes = getDurationInMinutes();

        //   if (vehicle instanceof Airplane) {
        //    AirplaneClass airplaneClass = ((Airplane) vehicle).getAirplaneClass();
        //   price = durationInMinutes / airplaneClass.getPriceCoefficient();
        // } else {
        // price = durationInMinutes / TEN_PERCENT;
        //}
    }

    /*
    private double getDurationInMinutes() {
        Transport transport = bookingData.getTransport();
        TravelingPoint source = bookingData.getSource();
        TravelingPoint destination = bookingData.getDestination();
        LocalTime duration = transport.calculateDuration(source, destination);

        return (duration.getHour() * MINUTE) + duration.getMinute();
    }*/
}