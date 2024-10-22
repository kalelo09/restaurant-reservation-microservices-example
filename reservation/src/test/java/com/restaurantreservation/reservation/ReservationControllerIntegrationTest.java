package com.restaurantreservation.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantreservation.clients.restaurant.RestaurantClient;
import com.restaurantreservation.exception.BadRequestException;
import com.restaurantreservation.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ReservationController} class.
 *
 * <p>This class verifies the behavior of the reservation API endpoints by performing
 * integration tests on the ReservationController. It mocks the ReservationService
 * and RestaurantClient to isolate the controller's behavior.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Import(GlobalExceptionHandler.class)
class ReservationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private RestaurantClient restaurantClient;

    private PagedResourcesAssembler<Reservation> pagedResourcesAssembler;

    public static final MediaType APPLICATION_HAL_JSON = MediaType.valueOf("application/hal+json");

    /**
     * Initializes the PagedResourcesAssembler before each test.
     */
    @BeforeEach
    void setUp() {
        pagedResourcesAssembler = new PagedResourcesAssembler<>(null, null); // Mock the PagedResourcesAssembler if necessary
    }

    /**
     * Tests the {@link ReservationController#getAllReservations(Pageable, PagedResourcesAssembler)} ()} endpoint.
     * <p>
     * Given a request to retrieve all reservations, when called, it returns a page of reservations
     * and verifies the response structure and content.
     * </p>
     */
    @Test
    void getAllReservationsWillReturnPagedResults() throws Exception {
        // Given
        List<Reservation> reservations = List.of(
                new Reservation(1L, 1L, "Customer1", LocalDate.now(), false),
                new Reservation(2L, 1L, "Customer2", LocalDate.now(), false)
        );
        Page<Reservation> pagedReservations = new PageImpl<>(reservations);

        when(reservationService.getAllReservations(any(Pageable.class)))
                .thenReturn(pagedReservations);

        // When & Then
        mockMvc.perform(get("/api/v1/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.reservationList[0].customerName").value("Customer1"))
                .andExpect(jsonPath("$._embedded.reservationList[1].customerName").value("Customer2"));
    }

    /**
     * Tests the {@link ReservationController#getCanceledReservations(Pageable, PagedResourcesAssembler)} ()} endpoint.
     * <p>
     * Given a request to retrieve canceled reservations, when called, it returns a page of canceled reservations
     * and verifies the response structure and content.
     * </p>
     */
    @Test
    void getCanceledReservationsWillReturnPagedResults() throws Exception {
        // Given
        List<Reservation> canceledReservations = List.of(
                new Reservation(1L, 1L, "Customer1", LocalDate.now(), true)
        );
        Page<Reservation> pagedReservations = new PageImpl<>(canceledReservations);

        when(reservationService.getReservationsCanceled(any(Pageable.class)))
                .thenReturn(pagedReservations);

        // When & Then
        mockMvc.perform(get("/api/v1/reservations/canceled"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.reservationList[0].canceled").value(true));
    }

    /**
     * Tests the {@link ReservationController#getNotCanceledReservations(Pageable, PagedResourcesAssembler)} ()} endpoint.
     * <p>
     * Given a request to retrieve non-canceled reservations, when called, it returns a page of non-canceled reservations
     * and verifies the response structure and content.
     * </p>
     */
    @Test
    void getNotCanceledReservationsWillReturnPagedResults() throws Exception {
        // Given
        List<Reservation> notCanceledReservations = List.of(
                new Reservation(1L, 1L, "Customer1", LocalDate.now(), false)
        );
        Page<Reservation> pagedReservations = new PageImpl<>(notCanceledReservations);

        when(reservationService.getReservationsNotCanceled(any(Pageable.class)))
                .thenReturn(pagedReservations);

        // When & Then
        mockMvc.perform(get("/api/v1/reservations/not_canceled"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.reservationList[0].canceled").value(false));
    }

    /**
     * Tests the {@link ReservationController#getReservationsByRestaurant(Pageable, PagedResourcesAssembler, Long)} (Long)} endpoint.
     * <p>
     * Given a request to retrieve reservations by restaurant ID, when called, it returns a page of reservations
     * for the specified restaurant and verifies the response structure and content.
     * </p>
     */
    @Test
    void getReservationsByRestaurantWillReturnPagedResults() throws Exception {
        // Given
        List<Reservation> restaurantReservations = List.of(
                new Reservation(1L, 1L, "Customer1", LocalDate.now(), false)
        );
        Page<Reservation> pagedReservations = new PageImpl<>(restaurantReservations);
        Long restaurantId = 1L;

        when(reservationService.getReservationsByRestaurant(any(Pageable.class), anyLong()))
                .thenReturn(pagedReservations);

        // When & Then
        mockMvc.perform(get("/api/v1/reservations/restaurant/{idRestaurant}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.reservationList[0].idRestaurant").value(1L));
    }

    /**
     * Tests the {@link ReservationController#getReservationsByCustomer(Pageable, PagedResourcesAssembler, String)} endpoint.
     * <p>
     * Given a request to retrieve reservations by customer name, when called, it returns a page of reservations
     * for the specified customer name and verifies the response structure and content.
     * </p>
     */
    @Test
    void getReservationsByCustomerWillReturnPagedResults() throws Exception {
        // Given
        List<Reservation> customerReservations = List.of(
                new Reservation(1L, 1L, "Customer1", LocalDate.now(), false)
        );
        Page<Reservation> pagedReservations = new PageImpl<>(customerReservations);
        String customerName = "Customer1";

        when(reservationService.getReservationsByCustomer(any(Pageable.class), anyString()))
                .thenReturn(pagedReservations);

        // When & Then
        mockMvc.perform(get("/api/v1/reservations/customer/{customerName}", customerName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.reservationList[0].customerName").value("Customer1"));
    }

    /**
     * Tests the {@link ReservationController#getReservationById(Long)} endpoint.
     * <p>
     * Given a request to retrieve a reservation by ID, when called, it returns the reservation
     * and verifies the response structure and content.
     * </p>
     */
    @Test
    void getReservationByIdWillReturnReservation() throws Exception {
        // Given
        Long reservationId = 1L;
        Reservation reservation = new Reservation(reservationId, 1L, "Customer1", LocalDate.now(), false);

        when(reservationService.getReservation(anyLong())).thenReturn(reservation);

        // When & Then
        mockMvc.perform(get("/api/v1/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.customerName").value("Customer1"));
    }

    /**
     * Tests the {@link ReservationController#registerReservation(ReservationRequest)} endpoint.
     * <p>
     * Given a request to register a new reservation, when called, it returns the created reservation
     * and verifies the response structure and content.
     * </p>
     */
    @Test
    void registerReservationWillReturnNewReservation() throws Exception {
        // Given
        ReservationRequest request = new ReservationRequest(1L, "Customer1", LocalDate.now());
        Reservation reservation = new Reservation(1L, 1L, "Customer1", LocalDate.now(), false);

        when(reservationService.register(any(ReservationRequest.class))).thenReturn(reservation);
        when(restaurantClient.checkRestaurantExist(anyLong())).thenReturn(true);
        when(restaurantClient.checkAvailability(anyLong())).thenReturn(true);
        when(restaurantClient.reserveTable(anyLong())).thenReturn("Table reserved in the restaurant [%s]".formatted(anyString()));

        // When & Then
        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(APPLICATION_HAL_JSON)
                        .content("{\"idRestaurant\":1,\"customerName\":\"Customer1\",\"reservationDate\":\"" + LocalDate.now() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.customerName").value("Customer1"));
    }

    /**
     * Tests the {@link ReservationController#updateReservation(Long, ReservationRequest)} endpoint.
     * <p>
     * Given a request to update an existing reservation, when called, it returns the updated reservation
     * and verifies the response structure and content.
     * </p>
     */
    @Test
    void updateReservationWillReturnUpdatedReservation() throws Exception {
        // Given
        Long reservationId = 1L;
        ReservationRequest request = new ReservationRequest(2L, "Customer2", LocalDate.now().plusDays(1));
        Reservation updatedReservation = new Reservation(reservationId, 2L, "Customer2", LocalDate.now().plusDays(1), false);

        when(reservationService.updateInformations(any(ReservationRequest.class), anyLong()))
                .thenReturn(updatedReservation);

        // When & Then
        mockMvc.perform(put("/api/v1/reservations/{idReservation}", reservationId)
                        .contentType(APPLICATION_HAL_JSON)
                        .content("{\"idRestaurant\":2,\"customerName\":\"Customer2\",\"reservationDate\":\"" + LocalDate.now().plusDays(1) + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.customerName").value("Customer2"))
                .andExpect(jsonPath("$.idRestaurant").value(2));
    }

    /**
     * Tests the {@link ReservationController#cancelReservation(Long)} endpoint.
     * <p>
     * Given a request to cancel a reservation that has already been canceled, when called, it throws a BadRequestException
     * and verifies the response status and content.
     * </p>
     */
    @Test
    void cancelReservationWillThrowErrorIfAlreadyCanceled() throws Exception {
        // Given
        Long reservationId = 1L;
        Reservation reservation = new Reservation(1L, 1L, "Customer1", LocalDate.now(), true);

        when(reservationService.cancelReservation(1L)).thenThrow(new BadRequestException("Reservation already canceled !!!"));

        // When & Then
        mockMvc.perform(put("/api/v1/reservations/cancel/{idReservation}", 1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"message\":\"Reservation already canceled !!!\"}"));
    }

    /**
     * Tests the {@link ReservationController#updateReservation(Long, ReservationRequest)} endpoint
     * for cases where no changes are detected.
     * <p>
     * Given a request to update a reservation with the same information, when called, it throws a BadRequestException
     * and verifies the response status and content.
     * </p>
     */
    @Test
    void updateReservationWillThrowErrorIfNoChanges() throws Exception {
        // Given
        Long reservationId = 1L;
        ReservationRequest request = new ReservationRequest(1L, "Customer1", LocalDate.now());
        Reservation existingReservation = new Reservation(reservationId, 1L, "Customer1", LocalDate.now(), false);

        when(reservationService.updateInformations(any(ReservationRequest.class), anyLong()))
                .thenThrow(new BadRequestException("no data changes found"));

        // When & Then
        mockMvc.perform(put("/api/v1/reservations/{idReservation}", reservationId)
                        .contentType(APPLICATION_HAL_JSON)
                        .content("{\"idRestaurant\":1,\"customerName\":\"Customer1\",\"reservationDate\":\"" + LocalDate.now() + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"message\":\"no data changes found\"}"));
    }
}