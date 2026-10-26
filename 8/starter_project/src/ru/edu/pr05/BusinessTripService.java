package ru.edu.pr05;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Координирует работу с заявками; бизнес-инварианты остаются в BusinessTrip. */
public class BusinessTripService {
    private final List<BusinessTrip> trips = new ArrayList<>();

    public BusinessTrip create(String id, LocalDate start, LocalDate end) {
        if (findById(id).isPresent()) {
            throw new IllegalArgumentException("Заявка с идентификатором " + id + " уже существует");
        }
        BusinessTrip trip = new BusinessTrip(id, start, end);
        trips.add(trip);
        return trip;
    }

    public Optional<BusinessTrip> findById(String id) {
        if (id == null) return Optional.empty();
        return trips.stream().filter(t -> t.getId().equals(id.trim())).findFirst();
    }

    public List<BusinessTrip> findByStatus(TripStatus status) {
        if (status == null) throw new IllegalArgumentException("Статус не должен быть null");
        return Collections.unmodifiableList(
            trips.stream().filter(t -> t.getStatus() == status).collect(Collectors.toList()));
    }

    public List<BusinessTrip> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(trips));
    }
}
