package com.nexo.ecommerce.orders.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexo.ecommerce.orders.application.usecases.CreateIntentPaymentUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrdenCreateUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderReadAllUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderReadByIdUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderReadMeUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderUpdateStatusUseCase;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentResponse;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateRequestDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateResponseDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping("/orders")
public class OrderController {
    private final OrdenCreateUseCase orderCreateUseCase;
    private final OrderReadAllUseCase orderReadAllUseCase;
    private final OrderReadMeUseCase orderReadMeUseCase;
    private final OrderReadByIdUseCase orderReadByIdUseCase;
    private final OrderUpdateStatusUseCase orderUpdateStatusUseCase;
       private final CreateIntentPaymentUseCase createIntentPaymentUseCase;

    public OrderController(OrdenCreateUseCase orderCreateUseCase, OrderReadAllUseCase orderReadAllUseCase, OrderReadMeUseCase orderReadMeUseCase, OrderReadByIdUseCase orderReadByIdUseCase, OrderUpdateStatusUseCase orderUpdateStatusUseCase, CreateIntentPaymentUseCase createIntentPaymentUseCase) {
        this.orderCreateUseCase = orderCreateUseCase;
        this.orderReadAllUseCase = orderReadAllUseCase;
        this.orderReadMeUseCase = orderReadMeUseCase;
        this.orderReadByIdUseCase = orderReadByIdUseCase;
        this.orderUpdateStatusUseCase = orderUpdateStatusUseCase;
        this.createIntentPaymentUseCase = createIntentPaymentUseCase;
    }

    @PostMapping
    public ResponseEntity<OrdenCreateResponseDto> createOrder(@RequestBody OrdenCreateRequestDto request) {
        // Lógica para crear la orden
        return ResponseEntity.ok(orderCreateUseCase.execute(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@RequestParam String userId) {
        // Lógica para obtener las órdenes del usuario
        return ResponseEntity.ok(orderReadMeUseCase.execute(userId));
    }
    
    @GetMapping()
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        // Lógica para obtener todas las órdenes
        return ResponseEntity.ok(orderReadAllUseCase.execute());
    }
    
    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String id) {
        // Lógica para obtener la orden por ID
        return ResponseEntity.ok(orderReadByIdUseCase.execute(id));
    }

    @PatchMapping("{id}/status")
    public ResponseEntity<UpdateStatusResponse> updateOrderStatus(@PathVariable String id, @RequestBody UpdateStatusRequest request) {
        // Lógica para actualizar el estado de la orden
        return ResponseEntity.ok(orderUpdateStatusUseCase.execute(request));
    }


    @PostMapping("/intent-payment")
   public ResponseEntity<CreateIntentPaymentResponse> createIntentPayment(@RequestBody CreateIntentPaymentRequest request) {
        // Lógica para crear el intent de pago
        return ResponseEntity.ok(createIntentPaymentUseCase.execute(request));
    }
    

}
