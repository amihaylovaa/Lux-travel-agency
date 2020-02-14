package adelina.luxtravel.service;

import adelina.luxtravel.domain.TravelingPoint;
import adelina.luxtravel.exception.*;
import adelina.luxtravel.repository.TravelingPointRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import adelina.luxtravel.exception.InvalidArgumentException;

import java.util.List;
import java.util.Optional;

import static adelina.luxtravel.utility.Constants.*;

@Service
public class TravelingPointService {
    private TravelingPointRepository travelingPointRepository;

    @Autowired
    public TravelingPointService(TravelingPointRepository travelingPointRepository) {
        this.travelingPointRepository = travelingPointRepository;
    }

    public TravelingPoint save(TravelingPoint travelingPoint) {
        validateTravelingPoint(travelingPoint);
        validateTravelingPointDoesNotExist(travelingPoint.getName());

        return travelingPointRepository.save(travelingPoint);
    }

    public List<TravelingPoint> saveAll(List<TravelingPoint> travelingPoints) {
        validateTravelingPointsList(travelingPoints);

        return travelingPointRepository.saveAll(travelingPoints);
    }

    public TravelingPoint findById(long id) {
        if (id <= NumberUtils.LONG_ZERO) {
            throw new InvalidArgumentException(INVALID_ID);
        }

        Optional<TravelingPoint> travelingPoint = travelingPointRepository.findById(id);

        if (!travelingPoint.isPresent()) {
            throw new NonExistentItemException("Traveling point with this id does not exist");
        }

        return travelingPoint.get();
    }

    public TravelingPoint findByName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new InvalidArgumentException("Invalid name");
        }

        Optional<TravelingPoint> travelingPoint = travelingPointRepository.findByName(name);

        if (!travelingPoint.isPresent()) {
            throw new NonExistentItemException("Traveling point with this name does not exists");
        }

        return travelingPoint.get();
    }

    public List<TravelingPoint> findAll() {
        List<TravelingPoint> travelingPoints = travelingPointRepository.findAll();

        if (ObjectUtils.isEmpty(travelingPoints)) {
            throw new NonExistentItemException("There are no traveling points found");
        }

        return travelingPoints;
    }

    public void updateName(String newName, String oldName) {
        validateUpdateNameParameters(newName, oldName);

        travelingPointRepository.updateName(newName, oldName);
    }

    public void deleteById(long id) {
        if (id <= NumberUtils.LONG_ZERO) {
            throw new InvalidArgumentException(INVALID_ID);
        }

        Optional<TravelingPoint> travelingPoint = travelingPointRepository.findById(id);

        if (!travelingPoint.isPresent()) {
            throw new NonExistentItemException("Traveling point with this id does not exist");
        }

        travelingPointRepository.deleteById(id);
    }

    private void validateTravelingPointsList(List<TravelingPoint> travelingPoints) {
        if (ObjectUtils.isEmpty(travelingPoints)) {
            throw new InvalidArgumentException("Invalid list of traveling points");
        }

        for (TravelingPoint travelingPoint : travelingPoints) {
            validateTravelingPoint(travelingPoint);
            validateTravelingPointDoesNotExist(travelingPoint.getName());
        }
    }

    private void validateTravelingPoint(TravelingPoint travelingPoint) {
        if (travelingPoint == null) {
            throw new InvalidArgumentException("Invalid traveling point");
        }
    }

    private void validateTravelingPointDoesNotExist(String travelingPointName) {
        Optional<TravelingPoint> travelingPoint = travelingPointRepository.findByName(travelingPointName);

        if (travelingPoint.isPresent()) {
            throw new AlreadyExistingItemException("Traveling point already exists");
        }
    }

    private void validateUpdateNameParameters(String newName, String oldName) {
        if (StringUtils.isEmpty(newName)) {
            throw new InvalidArgumentException("Invalid new name");
        }
        if (StringUtils.isEmpty(oldName)) {
            throw new InvalidArgumentException("Invalid old name");
        }
        if (newName.equals(oldName)) {
            throw new AlreadyExistingItemException("Traveling point with given new name already exists");
        }

        validateTravelingPointDoesNotExist(newName);
    }
}