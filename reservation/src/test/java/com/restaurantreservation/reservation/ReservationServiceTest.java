package com.restaurantreservation.reservation;

import com.restaurantreservation.clients.restaurant.RestaurantClient;
import com.restaurantreservation.exception.BadRequestException;
import com.restaurantreservation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ReservationService} class.
 *
 * <p>This class contains tests to validate the behavior of the methods within the
 * {@link ReservationService}. It uses Mockito to mock dependencies and verify interactions.</p>
 */
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RestaurantClient restaurantClient;

    @InjectMocks
    private ReservationService underTest;

    /**
     * Initializes mocks before each test.
     */
    public ReservationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the {@link ReservationService#getAllReservations(Pageable)} method.
     * <p>
     * Given a pageable request, when called, it returns a page of reservations
     * and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void getAllReservationsWillReturnReservationsPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservations = new PageImpl<>(Arrays.asList(new Reservation(), new Reservation()));
        when(reservationRepository.findAll(pageable)).thenReturn(reservations);

        // When
        Page<Reservation> result = underTest.getAllReservations(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(reservationRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests the {@link ReservationService#getReservationsNotCanceled(Pageable)} method.
     * <p>
     * Given a pageable request, when called, it returns a page of non-canceled reservations
     * and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void getReservationsNotCanceledWillReturnNotCanceledReservations() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservations = new PageImpl<>(Arrays.asList(new Reservation()));
        when(reservationRepository.findAllByCanceledIsFalse(pageable)).thenReturn(reservations);

        // When
        Page<Reservation> result = underTest.getReservationsNotCanceled(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findAllByCanceledIsFalse(pageable);
    }

    /**
     * Tests the {@link ReservationService#getReservationsCanceled(Pageable)} method.
     * <p>
     * Given a pageable request, when called, it returns a page of canceled reservations
     * and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void getReservationsCanceledWillReturnCanceledReservations() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservations = new PageImpl<>(Arrays.asList(new Reservation()));
        when(reservationRepository.findAllByCanceledIsTrue(pageable)).thenReturn(reservations);

        // When
        Page<Reservation> result = underTest.getReservationsCanceled(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findAllByCanceledIsTrue(pageable);
    }

    /**
     * Tests the {@link ReservationService#getReservationsByRestaurant(Pageable, Long)} method.
     * <p>
     * Given a pageable request and a restaurant ID, when called, it returns a page of reservations
     * for the specified restaurant and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void getReservationsByRestaurantWillReturnReservationsForGivenRestaurant() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Long idRestaurant = 1L;
        Page<Reservation> reservations = new PageImpl<>(Arrays.asList(new Reservation()));
        when(reservationRepository.findAllByIdRestaurantEquals(pageable, idRestaurant)).thenReturn(reservations);

        // When
        Page<Reservation> result = underTest.getReservationsByRestaurant(pageable, idRestaurant);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findAllByIdRestaurantEquals(pageable, idRestaurant);
    }

    /**
     * Tests the {@link ReservationService#getReservationsByCustomer(Pageable, String)} method.
     * <p>
     * Given a pageable request and a customer name, when called, it returns a page of reservations
     * for the specified customer and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void getReservationsByCustomerWillReturnReservationsForGivenCustomer() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String customerName = "John Doe";
        Page<Reservation> reservations = new PageImpl<>(Arrays.asList(new Reservation()));
        when(reservationRepository.findAllByCustomerNameEquals(pageable, customerName)).thenReturn(reservations);

        // When
        Page<Reservation> result = underTest.getReservationsByCustomer(pageable, customerName);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findAllByCustomerNameEquals(pageable, customerName);
    }

    /**
     * Tests the {@link ReservationService#getReservation(Long)} method for a valid ID.
     * <p>
     * Given a valid reservation ID, when called, it returns the reservation and verifies
     * the interaction with the repository.
     * </p>
     */
    @Test
    public void getReservationWillReturnReservationForValidId() {
        // Given
        Long id = 1L;
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // When
        Reservation result = underTest.getReservation(id);

        // Then
        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).findById(id);
    }

    /**
     * Tests the {@link ReservationService#getReservation(Long)} method for an invalid ID.
     * <p>
     * Given an invalid reservation ID, when called, it throws a {@link ResourceNotFoundException}
     * and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void getReservationWillThrowResourceNotFoundException() {
        // Given
        Long id = 1L;
        when(reservationRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            underTest.getReservation(id);
        });

        // Then
        assertThat(exception.getMessage()).contains("Reservation with id [1] not found");
        verify(reservationRepository, times(1)).findById(id);
    }

    /**
     * Tests the {@link ReservationService#register(ReservationRequest)} method.
     * <p>
     * Given a reservation request, when called, it saves the reservation and verifies the
     * interactions with the restaurant client and repository.
     * </p>
     */
    @Test
    public void registerWillSaveReservationAndCheckRestaurantAvailability() {
        // Given
        ReservationRequest request = new ReservationRequest(1L, "John Doe", LocalDate.now());
        when(restaurantClient.checkRestaurantExist(request.idRestaurant())).thenReturn(true);
        when(restaurantClient.checkAvailability(request.idRestaurant())).thenReturn(true);
        Reservation savedReservation = new Reservation();
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        // When
        Reservation result = underTest.register(request);

        // Then
        assertThat(result).isNotNull();
        verify(restaurantClient, times(1)).checkRestaurantExist(request.idRestaurant());
        verify(restaurantClient, times(1)).checkAvailability(request.idRestaurant());
        verify(restaurantClient, times(1)).reserveTable(request.idRestaurant());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    /**
     * Tests the {@link ReservationService#cancelReservation(Long)} method for a valid ID.
     * <p>
     * Given a valid reservation ID, when called, it cancels the reservation and verifies
     * the interactions with the restaurant client and repository.
     * </p>
     */
    @Test
    public void cancelReservationWillCancelReservationAndReleaseRestaurantTable() {
        // Given
        Long id = 1L;
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // When
        Reservation result = underTest.cancelReservation(id);

        // Then
        assertThat(result).isNotNull();
        verify(restaurantClient, times(1)).cancelTableReservation(reservation.getIdRestaurant());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    /**
     * Tests the {@link ReservationService#cancelReservation(Long)} method for a canceled reservation.
     * <p>
     * Given an already canceled reservation ID, when called, it throws a {@link BadRequestException}
     * and verifies that the repository save method is not called.
     * </p>
     */
    @Test
    public void cancelReservationWillThrowBadRequestException() {
        // Given
        Long id = 1L;
        Reservation reservation = new Reservation();
        reservation.setCanceled(true);

        // When
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        // Then
        assertThatThrownBy(() -> underTest.cancelReservation(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Reservation already canceled !!!");
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    /**
     * Tests the {@link ReservationService#updateInformations(ReservationRequest, Long)} method.
     * <p>
     * Given a valid reservation ID and a request to update the reservation,
     * when called, it updates the reservation and verifies the interaction with the repository.
     * </p>
     */
    @Test
    public void updateReservationWillUpdateReservationInformation() {
        // Given
        Long id = 1L;
        Reservation reservation = new Reservation();
        reservation.setCustomerName("Old Name");
        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        ReservationRequest request = new ReservationRequest(1L, "New Name", LocalDate.now());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // When
        Reservation result = underTest.updateInformations(request, id);

        // Then
        assertThat(result).isNotNull();
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}