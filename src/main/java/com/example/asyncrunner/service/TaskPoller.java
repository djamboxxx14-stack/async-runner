package com.example.asyncrunner.service;

import com.example.asyncrunner.domain.Task;
import com.example.asyncrunner.domain.TaskStatus;
import com.example.asyncrunner.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskPoller {
    
    private final TaskRepository taskRepository;
    private final AsyncTaskRunner asyncTaskRunner;
    
    @Value("${app.polling.batch-size:10}")
    private int batchSize;
    
    @Scheduled(fixedDelayString = "${app.polling.interval-ms:2000}")
    public void pollAndProcess() {
        log.debug("🔍 TaskPoller: Поиск новых задач...");
        
        // 1. Забираем задачи из БД (FOR UPDATE SKIP LOCKED)
        List<Task> tasks = taskRepository.findTasksForProcessing(Instant.now(), batchSize);
        
        if (tasks.isEmpty()) {
            return;
        }
        
        log.info("📦 TaskPoller: Найдено {} задач для обработки", tasks.size());
        
        // 2. Обрабатываем каждую задачу
        for (Task task : tasks) {
            processTask(task);
        }
    }
    
    @Transactional
    public void processTask(Task task) {
        try {
            // Обновляем статус и счетчик попыток
            task.setStatus(TaskStatus.IN_PROGRESS);
            task.setAttempts(task.getAttempts() + 1);
            taskRepository.save(task);
            
            log.info("️ Обработка задачи {} (попытка {}/{})", 
                task.getId(), task.getAttempts(), 3);
            
            // Передаем задачу в асинхронный раннер
            asyncTaskRunner.processTask(task);
            
            // Если раннер отработал без исключений — помечаем как DONE
            // (На Шаге 10 мы заменим это на CompletableFuture)
            task.setStatus(TaskStatus.DONE);
            taskRepository.save(task);
            
            log.info("✅ Задача {} завершена успешно", task.getId());
            
        } catch (Exception e) {
            log.error(" Ошибка при обработке задачи {}: {}", task.getId(), e.getMessage());
            handleProcessingError(task, e);
        }
    }
    
    private void handleProcessingError(Task task, Exception e) {
        if (task.getAttempts() < 3) {
            task.setStatus(TaskStatus.RETRYABLE);
            task.setNextAttemptAt(Instant.now().plusSeconds(5));
            task.setErrorMessage(e.getMessage());
        } else {
            task.setStatus(TaskStatus.ERROR);
            task.setErrorMessage("Max attempts reached: " + e.getMessage());
        }
        taskRepository.save(task);
    }
}