package com.example.asyncrunner.service;

import com.example.asyncrunner.domain.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncTaskRunner {
    
    public void processTask(Task task) {
        // Заглушка. Реальную логику с CompletableFuture добавим на Шаге 10.
        log.info("🚀 [STUB] AsyncTaskRunner получил задачу: {}", task.getId());
        
        // Имитация успешной обработки
        log.info("✅ [STUB] Задача {} успешно обработана (имитация)", task.getId());
    }
}