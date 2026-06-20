package com.example.asyncrunner.api;

import com.example.asyncrunner.api.dto.CreateOrderRequest;
import com.example.asyncrunner.api.dto.OrderResponse;
import com.example.asyncrunner.domain.Order;
import com.example.asyncrunner.domain.Task;
import com.example.asyncrunner.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /orders - Creating order for customer: {}", request.customerId());
        
        Order order = orderService.createOrder(request);
        
        // Получаем taskId для ответа
        Task task = orderService.findTaskByOrderId(order.getId())
            .orElse(null);
        
        OrderResponse response = new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getStatus(),
            order.getTotalAmount(),
            task != null ? task.getId() : null,
            order.getCreatedAt()
        );
        
        log.info("Order created successfully: id={}, taskId={}", order.getId(), task != null ? task.getId() : null);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        log.info("GET /orders/{}", id);
        
        return orderService.findById(id)
            .map(order -> {
                Task task = orderService.findTaskByOrderId(order.getId()).orElse(null);
                
                OrderResponse response = new OrderResponse(
                    order.getId(),
                    order.getCustomerId(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    task != null ? task.getId() : null,
                    order.getCreatedAt()
                );
                
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}