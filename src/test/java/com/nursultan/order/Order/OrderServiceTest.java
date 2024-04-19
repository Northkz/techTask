package com.nursultan.order.Order;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertAll;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import org.junit.jupiter.api.Disabled;


@DataJpaTest
@ActiveProfiles("test")
@Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class OrderServiceTest {
    private String api_key = "AIzaSyBQ977dMGnKv4uNGCt1lUc2K4dUA4YEGRc"; //Please, replace with your own key, this is key is no longer valid
    private final OrderRepository orderRepositoryMock = mock(OrderRepository.class, "orderRepository");


    @Test()
    void getOrdersTest() {
        OrderService target = new OrderService(orderRepositoryMock);
        Page<Order> pageMock = mock(Page.class);
        Pageable pageableMock = mock(Pageable.class);
        doReturn(pageMock).when(orderRepositoryMock).findAll(pageableMock);

        Page<Order> result = target.getOrders(pageableMock);

        assertAll("result", () -> {
            assertThat(result, equalTo(pageMock));
            verify(orderRepositoryMock).findAll(pageableMock);
        });
    }


    @Test()
    void isValidCoordinateWhenLongitudeNotGreaterThan180() {

        OrderService target = new OrderService(orderRepositoryMock);
        String[] stringArray = new String[]{"0.25 ", "0.5 "};

        boolean result = target.isValidCoordinate(stringArray);

        assertAll("result", () -> assertThat(result, equalTo(Boolean.TRUE)));
    }


    @Test()
    void isValidCoordinateWhenLongitudeGreaterThan180() {

        OrderService target = new OrderService(orderRepositoryMock);
        String[] stringArray = new String[]{"90.0 ", "180.5 "};
        boolean result = target.isValidCoordinate(stringArray);

        assertAll("result", () -> assertThat(result, equalTo(Boolean.FALSE)));
    }


    @Test()
    void takeOrderWhenOrderIsNull() {

        OrderService target = new OrderService(orderRepositoryMock);
        doReturn(Optional.empty()).when(orderRepositoryMock).findById(0L);
        boolean result = target.takeOrder(0L);
        assertAll("result", () -> {
            assertThat(result, equalTo(Boolean.FALSE));
            verify(orderRepositoryMock).findById(0L);
        });
    }

    @Test()
    void calculateTest() throws IOException {

        //Arrange Statement(s)
        OrderService target = new OrderService(orderRepositoryMock);
        //Act Statement(s)
        String result = target.calculate("A", "B", "C", "D", api_key);
        //Assert statement(s)
        String expected = """
                             {
                                "destination_addresses" :\s
                                [
                                   ""
                                ],
                                "origin_addresses" :\s
                                [
                                   ""
                                ],
                                "rows" :\s
                                [
                                   {
                                      "elements" :\s
                                      [
                                         {
                                            "status" : "NOT_FOUND"
                                         }
                                      ]
                                   }
                                ],
                                "status" : "OK"
                             }""";
        assertAll("result", () -> assertThat(result, equalTo(expected)));
    }

    @Test
    void calculateTestCorrect() throws IOException {
        // Arrange
        OrderService target = new OrderService(orderRepositoryMock);
        String expectedJson = """
{
   \"destination_addresses\" :\s
   [
      \"5 Le Plaix, 18160 Saint-Hilaire-en-Lignières, France\"
   ],
   \"origin_addresses\" :\s
   [
      \"111 Le Plaix, 18160 Saint-Hilaire-en-Lignières, France\"
   ],
   \"rows\" :\s
   [
      {
         \"elements\" :\s
         [
            {
               \"distance\" :\s  
               {
                  \"text\" : \"0.5 km\",
                  \"value\" : 466
               },
               \"duration\" :\s
               {
                  \"text\" : \"1 min\",
                  \"value\" : 75
               },
               \"status\" : \"OK\"
            }
         ]
      }
   ],
   \"status\" : \"OK\"
}""";

        // Act
        String result = target.calculate("46.73776", "2.181573", "46.73860", "2.176467", api_key);

        // Assert
        assertAll("result", () -> assertThat(result, equalTo(expectedJson)));
    }

}
