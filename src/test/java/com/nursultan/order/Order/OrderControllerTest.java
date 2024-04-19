package com.nursultan.order.Order;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.doReturn;
@DataJpaTest
@ActiveProfiles("test")
@Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class OrderControllerTest {

    private final OrderService orderServiceMock = mock(OrderService.class, "orderService");

    private final Order orderMock = mock(Order.class);

    private final Page<Order> pageMock = mock(Page.class);

    private final PageRequest pageRequestMock = mock(PageRequest.class);


    @Test()
    void listOrdersWhenLimitNumLessThan1() {

        OrderController target = new OrderController(orderServiceMock);
        //Act Statement(s)
        ResponseEntity<?> result = target.listOrders("2", "0");
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.badRequest();
        ResponseEntity responseEntity = bodyBuilder.body("{\"error\":\"Invalid page or limit values\"}");
        //Assert statement(s)
        assertAll("result", () -> assertThat(result, equalTo(responseEntity)));
    }

    @Test()
    void listOrdersWhenOrdersIsEmpty() {
        try (MockedStatic<PageRequest> pageRequest = mockStatic(PageRequest.class, CALLS_REAL_METHODS)) {
            pageRequest.when(() -> PageRequest.of(1, 1)).thenReturn(pageRequestMock);
            OrderController target = new OrderController(orderServiceMock);
            doReturn(pageMock).when(orderServiceMock).getOrders(pageRequestMock);
            doReturn(true).when(pageMock).isEmpty();
            //Act Statement(s)
            ResponseEntity<?> result = target.listOrders("2", "1");
            ResponseEntity<String> responseEntity = ResponseEntity.ok("[]");
            //Assert statement(s)
            assertAll("result", () -> {
                assertThat(result, equalTo(responseEntity));
                pageRequest.verify(() -> PageRequest.of(1, 1), atLeast(1));
                verify(orderServiceMock, atLeast(1)).getOrders(pageRequestMock);
                verify(pageMock, atLeast(1)).isEmpty();
            });
        }
    }

    @Test()
    void listOrdersWhenOrdersNotIsEmpty() {
        ResponseEntity<List> responseEntityMock = mock(ResponseEntity.class);
        try (MockedStatic<ResponseEntity> responseEntity = mockStatic(ResponseEntity.class);
             MockedStatic<PageRequest> pageRequest = mockStatic(PageRequest.class, CALLS_REAL_METHODS)) {
            pageRequest.when(() -> PageRequest.of(1, 1)).thenReturn(pageRequestMock);
            responseEntity.when(() -> ResponseEntity.ok(anyList())).thenReturn(responseEntityMock);
            OrderController target = new OrderController(orderServiceMock);
            doReturn(pageMock).when(orderServiceMock).getOrders(pageRequestMock);
            doReturn(false).when(pageMock).isEmpty();
            List list = new ArrayList<>();
            doReturn(list).when(pageMock).getContent();
            //Act Statement(s)
            ResponseEntity<?> result = target.listOrders("2", "1");
            assertAll("result", () -> {
                assertThat(result, equalTo(responseEntityMock));
                pageRequest.verify(() -> PageRequest.of(1, 1), atLeast(1));
                responseEntity.verify(() -> ResponseEntity.ok(anyList()), atLeast(1));
                verify(orderServiceMock, atLeast(1)).getOrders(pageRequestMock);
                verify(pageMock, atLeast(1)).isEmpty();
                verify(pageMock, atLeast(1)).getContent();
            });
        }
    }

    @Test()
    void takeOrderWhenSuccess() {
        OrderController target = new OrderController(orderServiceMock);
        doReturn(true).when(orderServiceMock).takeOrder(0L);
        //Act Statement(s)
        ResponseEntity<?> result = target.takeOrder(0L);
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok();
        ResponseEntity responseEntity = bodyBuilder.body("{\"status\":\"SUCCESS\"}");
        assertAll("result", () -> {
            assertThat(result, equalTo(responseEntity));
            verify(orderServiceMock).takeOrder(0L);
        });
    }

    @Test()
    void takeOrderWhenNotSuccess() {

        OrderController target = new OrderController(orderServiceMock);
        doReturn(false).when(orderServiceMock).takeOrder(0L);
        ResponseEntity<?> result = target.takeOrder(0L);
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(400);
        ResponseEntity responseEntity = bodyBuilder.body("{\"error\":\"Order already taken or does not exist\"}");
        assertAll("result", () -> {
            assertThat(result, equalTo(responseEntity));
            verify(orderServiceMock).takeOrder(0L);
        });
    }

    @Test()
    void placeOrderWhenOrderServiceNotIsValidCoordinateOrderRequestGetDestination() throws Exception {
        OrderController target = new OrderController(orderServiceMock);
        String[] stringArray = new String[]{"originItem1", "originItem1"};
        doReturn(true).when(orderServiceMock).isValidCoordinate(stringArray);
        String[] stringArray2 = new String[]{"destinationItem1", "destinationItem1"};
        doReturn(false).when(orderServiceMock).isValidCoordinate(stringArray2);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setOrigin(stringArray);
        orderRequestDTO.setDestination(stringArray2);
        //Act Statement(s)
        ResponseEntity<?> result = target.placeOrder(orderRequestDTO);
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.badRequest();
        ResponseEntity responseEntity = bodyBuilder.body("{\"error\":\"Invalid coordinate format or coordinates\"}");
        //Assert statement(s)
        assertAll("result", () -> {
            assertThat(result, equalTo(responseEntity));
            verify(orderServiceMock).isValidCoordinate(stringArray);
            verify(orderServiceMock).isValidCoordinate(stringArray2);
        });
    }

    @Test()
    void placeOrderWhenOrderServiceIsValidCoordinateOrderRequestGetDestination() throws Exception {
        ResponseEntity<Order> responseEntityMock = mock(ResponseEntity.class);
        try (MockedStatic<ResponseEntity> responseEntity = mockStatic(ResponseEntity.class)) {
            responseEntity.when(() -> ResponseEntity.ok(orderMock)).thenReturn(responseEntityMock);
            OrderController target = new OrderController(orderServiceMock);
            String[] stringArray = new String[]{"originItem1", "originItem1"};
            doReturn(true).when(orderServiceMock).isValidCoordinate(stringArray);
            String[] stringArray2 = new String[]{"destinationItem1", "destinationItem1"};
            doReturn(true).when(orderServiceMock).isValidCoordinate(stringArray2);
            doReturn(orderMock).when(orderServiceMock).createOrder("originItem1", "originItem1", "destinationItem1", "destinationItem1");
            OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
            orderRequestDTO.setOrigin(stringArray);
            orderRequestDTO.setDestination(stringArray2);
            //Act Statement(s)
            ResponseEntity<?> result = target.placeOrder(orderRequestDTO);
            //Assert statement(s)
            assertAll("result", () -> {
                assertThat(result, equalTo(responseEntityMock));
                responseEntity.verify(() -> ResponseEntity.ok(orderMock), atLeast(1));
                verify(orderServiceMock).isValidCoordinate(stringArray);
                verify(orderServiceMock).isValidCoordinate(stringArray2);
                verify(orderServiceMock).createOrder("originItem1", "originItem1", "destinationItem1", "destinationItem1");
            });
        }
    }

    @Test()
    void placeOrderWhenCaughtException() throws Exception {
        try (MockedStatic<ResponseEntity> responseEntity = mockStatic(ResponseEntity.class, CALLS_REAL_METHODS)) {
            RuntimeException runtimeException = new RuntimeException();
            responseEntity.when(() -> ResponseEntity.ok(orderMock)).thenThrow(runtimeException);
            OrderController target = new OrderController(orderServiceMock);
            String[] stringArray = new String[]{"originItem1", "originItem1"};
            doReturn(true).when(orderServiceMock).isValidCoordinate(stringArray);
            String[] stringArray2 = new String[]{"destinationItem1", "destinationItem1"};
            doReturn(true).when(orderServiceMock).isValidCoordinate(stringArray2);
            doReturn(orderMock).when(orderServiceMock).createOrder("originItem1", "originItem1", "destinationItem1", "destinationItem1");
            OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
            orderRequestDTO.setOrigin(stringArray);
            orderRequestDTO.setDestination(stringArray2);
            //Act Statement(s)
            ResponseEntity<?> result = target.placeOrder(orderRequestDTO);
            ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.internalServerError();
            ResponseEntity responseEntity2 = bodyBuilder.body("{\"error\":\"Error processing your request\"}");
            assertAll("result", () -> {
                assertThat(result, equalTo(responseEntity2));
                responseEntity.verify(() -> ResponseEntity.ok(orderMock), atLeast(1));
                verify(orderServiceMock, atLeast(1)).isValidCoordinate(stringArray);
                verify(orderServiceMock, atLeast(1)).isValidCoordinate(stringArray2);
                verify(orderServiceMock, atLeast(1)).createOrder("originItem1", "originItem1", "destinationItem1", "destinationItem1");
            });
        }
    }
}
