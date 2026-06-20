package com.example.asyncrunner.service;

import com.example.asyncrunner.api.dto.CreateOrderRequest;
import com.example.asyncrunner.domain.Order;
import com.example.asyncrunner.domain.OrderStatus;
import com.example.asyncrunner.domain.Task;
import com.example.asyncrunner.domain.TaskStatus;
import com.example.asyncrunner.repository.OrderRepository;
import com.example.asyncrunner.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final TaskRepository taskRepository;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.customerId());
        
        // Создаем заказ
        Order order = Order.builder()
            .customerId(request.customerId())
            .status(OrderStatus.CREATED)
            .build();
        
        order = orderRepository.save(order);
        log.info("Order created with id: {}", order.getId());
        
        // Создаем задачу для асинхронной обработки
        Task task = Task.builder()
            .order(order)
            .status(TaskStatus.NEW)
            .build();
        
        task = taskRepository.save(task);
        log.info("Task created with id: {} for order: {}", task.getId(), order.getId());
        
        return order;
    }
    
    @Transactional(readOnly = true)
    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Task> findTaskByOrderId(UUID orderId) {
        return taskRepository.findByOrderId(orderId);
    }
}