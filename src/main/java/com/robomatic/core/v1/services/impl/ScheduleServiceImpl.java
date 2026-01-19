package com.robomatic.core.v1.services.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.robomatic.core.v1.clients.SchedulerClient;
import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.entities.ScheduleEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.enums.ActionEnum;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.enums.ScheduleStatusEnum;
import com.robomatic.core.v1.enums.TriggerTypeEnum;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.models.CreateScheduleRequestModel;
import com.robomatic.core.v1.models.JobModel;
import com.robomatic.core.v1.models.ScheduleListModel;
import com.robomatic.core.v1.models.ScheduleModel;
import com.robomatic.core.v1.models.UpdateScheduleRequestModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import com.robomatic.core.v1.repositories.ScheduleRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import com.robomatic.core.v1.services.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.robomatic.core.v1.exceptions.messages.BadRequestErrorCode.*;
import static com.robomatic.core.v1.exceptions.messages.NotFoundErrorCode.*;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final String SCHEDULE_QUEUE = "task.execute_scheduled_test";
    private static final Gson gson = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private ActionRelationalRepository actionRelationalRepository;

    @Autowired
    private SchedulerClient schedulerClient;

    @Autowired
    private UserModel currentUser;

    @Value("${constants.testPrefix}")
    private String testPrefix;

    @Override
    public ScheduleListModel getAllSchedules() {
        List<ScheduleEntity> entities = scheduleRepository.findAllActive();
        
        // Filtrar solo los schedules donde el usuario tiene permisos de owner o editor
        List<ScheduleModel> schedules = entities.stream()
                .filter(entity -> hasPermissionOnTest(entity.getTestId()))
                .map(this::mapToModel)
                .collect(Collectors.toList());

        return ScheduleListModel.builder()
                .schedules(schedules)
                .total(schedules.size())
                .build();
    }

    @Override
    public ScheduleModel getScheduleById(String scheduleId) {
        ScheduleEntity entity = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(E404013));
        
        // Validar permisos antes de devolver
        validatePermissions(entity.getTestId());
        
        return mapToModel(entity);
    }

    @Override
    public ScheduleModel getScheduleByTestId(Integer testId) {
        ScheduleEntity entity = scheduleRepository.findByTestId(testId)
                .orElse(null);
        
        if (entity == null) {
            return null;
        }
        
        // Validar permisos antes de devolver
        validatePermissions(entity.getTestId());
        
        return mapToModel(entity);
    }

    @Override
    @Transactional
    public ScheduleModel createSchedule(CreateScheduleRequestModel request) {
        // Validar que el test existe
        TestEntity test = testRepository.findById(request.getTestId())
                .orElseThrow(() -> new NotFoundException(E404002));

        // Validar permisos
        validatePermissions(request.getTestId());

        // Validar que no existe otro schedule para este test
        if (scheduleRepository.existsByTestId(request.getTestId())) {
            throw new BadRequestException(E400023);
        }

        // Validar trigger type
        if (!TriggerTypeEnum.isValid(request.getTriggerType())) {
            throw new BadRequestException(E400024);
        }

        // Generar schedule ID
        String scheduleId = "SCH-" + testPrefix + request.getTestId() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // Crear job en scheduler-api
        JobModel job = JobModel.builder()
                .jobId(scheduleId)
                .triggerType(request.getTriggerType())
                .expression(request.getExpression())
                .queue(SCHEDULE_QUEUE)
                .message(String.valueOf(request.getTestId()))
                .build();

        schedulerClient.createJob(job);

        // Obtener próxima ejecución del scheduler
        JobModel createdJob = schedulerClient.getJobById(scheduleId);

        // Crear entidad
        ScheduleEntity entity = ScheduleEntity.builder()
                .scheduleId(scheduleId)
                .testId(request.getTestId())
                .name(request.getName())
                .description(request.getDescription())
                .triggerType(request.getTriggerType())
                .expression(gson.toJson(request.getExpression()))
                .status(ScheduleStatusEnum.ACTIVE.getCode())
                .nextRunTime(parseNextRunTime(createdJob.getNextRunTime()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ScheduleEntity saved = scheduleRepository.save(entity);
        log.info("Schedule created: {} for test: {}", scheduleId, request.getTestId());

        return mapToModel(saved);
    }

    @Override
    @Transactional
    public ScheduleModel updateSchedule(String scheduleId, UpdateScheduleRequestModel request) {
        ScheduleEntity entity = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(E404013));

        // Validar permisos
        validatePermissions(entity.getTestId());

        // Validar trigger type si se está actualizando
        if (request.getTriggerType() != null && !TriggerTypeEnum.isValid(request.getTriggerType())) {
            throw new BadRequestException(E400024);
        }

        // Eliminar job anterior y crear nuevo
        schedulerClient.deleteJob(scheduleId);

        String triggerType = request.getTriggerType() != null ? request.getTriggerType() : entity.getTriggerType();
        Map<String, Object> expression = request.getExpression() != null ? request.getExpression() : 
                gson.fromJson(entity.getExpression(), MAP_TYPE);

        JobModel job = JobModel.builder()
                .jobId(scheduleId)
                .triggerType(triggerType)
                .expression(expression)
                .queue(SCHEDULE_QUEUE)
                .message(String.valueOf(entity.getTestId()))
                .build();

        schedulerClient.createJob(job);

        // Obtener próxima ejecución actualizada
        JobModel updatedJob = schedulerClient.getJobById(scheduleId);

        // Actualizar entidad
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getTriggerType() != null) entity.setTriggerType(request.getTriggerType());
        if (request.getExpression() != null) entity.setExpression(gson.toJson(request.getExpression()));
        entity.setNextRunTime(parseNextRunTime(updatedJob.getNextRunTime()));
        entity.setUpdatedAt(LocalDateTime.now());

        ScheduleEntity saved = scheduleRepository.save(entity);
        log.info("Schedule updated: {}", scheduleId);

        return mapToModel(saved);
    }

    @Override
    @Transactional
    public void deleteSchedule(String scheduleId) {
        ScheduleEntity entity = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(E404013));

        // Validar permisos
        validatePermissions(entity.getTestId());

        // Eliminar job del scheduler
        try {
            schedulerClient.deleteJob(scheduleId);
        } catch (Exception e) {
            log.warn("Could not delete job from scheduler: {}", e.getMessage());
        }

        // Marcar como eliminado (soft delete)
        entity.setStatus(ScheduleStatusEnum.DELETED.getCode());
        entity.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(entity);

        log.info("Schedule deleted: {}", scheduleId);
    }

    @Override
    @Transactional
    public ScheduleModel pauseSchedule(String scheduleId) {
        ScheduleEntity entity = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(E404013));

        // Validar permisos
        validatePermissions(entity.getTestId());

        // Eliminar job del scheduler (pausar = eliminar temporalmente)
        try {
            schedulerClient.deleteJob(scheduleId);
        } catch (Exception e) {
            log.warn("Could not pause job in scheduler: {}", e.getMessage());
        }

        entity.setStatus(ScheduleStatusEnum.PAUSED.getCode());
        entity.setNextRunTime(null);
        entity.setUpdatedAt(LocalDateTime.now());
        ScheduleEntity saved = scheduleRepository.save(entity);

        log.info("Schedule paused: {}", scheduleId);
        return mapToModel(saved);
    }

    @Override
    @Transactional
    public ScheduleModel resumeSchedule(String scheduleId) {
        ScheduleEntity entity = scheduleRepository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new NotFoundException(E404013));

        // Validar permisos
        validatePermissions(entity.getTestId());

        // Recrear job en scheduler
        Map<String, Object> expression = gson.fromJson(entity.getExpression(), MAP_TYPE);

        JobModel job = JobModel.builder()
                .jobId(scheduleId)
                .triggerType(entity.getTriggerType())
                .expression(expression)
                .queue(SCHEDULE_QUEUE)
                .message(String.valueOf(entity.getTestId()))
                .build();

        schedulerClient.createJob(job);

        // Obtener próxima ejecución
        JobModel createdJob = schedulerClient.getJobById(scheduleId);

        entity.setStatus(ScheduleStatusEnum.ACTIVE.getCode());
        entity.setNextRunTime(parseNextRunTime(createdJob.getNextRunTime()));
        entity.setUpdatedAt(LocalDateTime.now());
        ScheduleEntity saved = scheduleRepository.save(entity);

        log.info("Schedule resumed: {}", scheduleId);
        return mapToModel(saved);
    }

    /**
     * Verifica si el usuario tiene permisos de owner o editor sobre un test.
     * Retorna true si tiene permisos, false si no.
     */
    private boolean hasPermissionOnTest(Integer testId) {
        Integer userId = currentUser.getId();
        Integer roleId = currentUser.getRoleId();

        // Admins y Analysts tienen permiso total
        //if (roleId != null && (roleId.equals(RoleEnum.ADMIN.getCode()) || roleId.equals(RoleEnum.ANALYST.getCode()))) {
        //    return true;
        //}

        // Verificar si es owner o tiene permiso de edición
        List<ActionRelationalEntity> actions = actionRelationalRepository.findTestsWithOwnerOrEditPermission(userId);
        return actions.stream().anyMatch(action -> 
            action.getTest() != null && action.getTest().getId().equals(testId)
        );
    }

    /**
     * Valida permisos y lanza excepción si no tiene acceso.
     */
    private void validatePermissions(Integer testId) {
        if (!hasPermissionOnTest(testId)) {
            throw new BadRequestException(E400026);
        }
    }

    private ScheduleModel mapToModel(ScheduleEntity entity) {
        TestEntity test = testRepository.findById(entity.getTestId()).orElse(null);
        String testName = test != null ? test.getName() : "Unknown";

        ScheduleStatusEnum status = ScheduleStatusEnum.getByCode(entity.getStatus());

        return ScheduleModel.builder()
                .id(entity.getId())
                .scheduleId(entity.getScheduleId())
                .testId(entity.getTestId())
                .testName(testName)
                .name(entity.getName())
                .description(entity.getDescription())
                .triggerType(entity.getTriggerType())
                .expression(gson.fromJson(entity.getExpression(), MAP_TYPE))
                .status(status != null ? status.getValue() : "unknown")
                .statusCode(entity.getStatus())
                .nextRunTime(entity.getNextRunTime())
                .lastRunTime(entity.getLastRunTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private LocalDateTime parseNextRunTime(String nextRunTime) {
        if (nextRunTime == null || nextRunTime.equals("None") || nextRunTime.isEmpty()) {
            return null;
        }
        try {
            // Formato esperado: "2025-12-15 08:00:00-03:00" o similar
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
            return LocalDateTime.parse(nextRunTime, formatter);
        } catch (Exception e) {
            try {
                // Intentar sin timezone
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(nextRunTime.substring(0, 19), formatter);
            } catch (Exception ex) {
                log.warn("Could not parse next run time: {}", nextRunTime);
                return null;
            }
        }
    }
}

