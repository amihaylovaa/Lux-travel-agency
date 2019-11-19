package adelina.luxtravel.repository;

import adelina.luxtravel.wrapper.BookingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDataRepository extends JpaRepository<BookingData, Long> {


}
