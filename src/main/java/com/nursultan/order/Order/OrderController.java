package com.nursultan.order.Order;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;



@RestController
@RequestMapping(path="orders")
public class OrderController {


    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<?> listOrders(@RequestParam String page, @RequestParam String limit) {
        int pageNum, limitNum;

        try {
            pageNum = Integer.parseInt(page);
            limitNum = Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"Page and limit must be valid integers\"}");
        }

        if (pageNum < 1 || limitNum < 1) {
            return ResponseEntity.badRequest().body("{\"error\":\"Invalid page or limit values\"}");
        }

        Pageable pageable = PageRequest.of(pageNum - 1, limitNum);
        Page<Order> orders = orderService.getOrders(pageable);

        if (orders.isEmpty()) {
            return ResponseEntity.ok("[]");
        }

        return ResponseEntity.ok(orders.getContent());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> takeOrder(@PathVariable Long id) {
        boolean success = orderService.takeOrder(id);
        if (success) {
            return ResponseEntity.ok().body("{\"status\":\"SUCCESS\"}");
        } else {
            return ResponseEntity.status(400).body("{\"error\":\"Order already taken or does not exist\"}");
        }
    }


    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        if (orderRequest.getOrigin().length != 2 || orderRequest.getDestination().length != 2
                || !orderService.isValidCoordinate(orderRequest.getOrigin())
                || !orderService.isValidCoordinate(orderRequest.getDestination())) {
            return ResponseEntity.badRequest().body("{\"error\":\"Invalid coordinate format or coordinates\"}");
        }
        try {
            Order newOrder = orderService.createOrder(
                    orderRequest.getOrigin()[0], orderRequest.getOrigin()[1],
                    orderRequest.getDestination()[0], orderRequest.getDestination()[1]);
            return ResponseEntity.ok(newOrder);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"Invalid coordinate format or coordinates\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"Error processing your request\"}");
        }

    }
}
