package com.robomatic.core.v1.services.impl;

import com.robomatic.core.v1.clients.SchedulerClient;
import com.robomatic.core.v1.entities.ActionRelationalEntity;
import com.robomatic.core.v1.entities.ScheduleEntity;
import com.robomatic.core.v1.entities.TestEntity;
import com.robomatic.core.v1.entities.UserEntity;
import com.robomatic.core.v1.enums.RoleEnum;
import com.robomatic.core.v1.enums.ScheduleStatusEnum;
import com.robomatic.core.v1.exceptions.BadGatewayException;
import com.robomatic.core.v1.models.JobCreatedModel;
import com.robomatic.core.v1.models.UserModel;
import com.robomatic.core.v1.repositories.ActionRelationalRepository;
import com.robomatic.core.v1.repositories.ScheduleRepository;
import com.robomatic.core.v1.repositories.TestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ScheduleServiceImpl.
 *
 * Cubre el bug #116: al pausar/eliminar un scheduler desde un perfil no-admin,
 * si el scheduler externo falla, el estado NO debe persistirse en BD.
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceImplTest {

    @Mock private ScheduleRepository scheduleRepository;
    @Mock private TestRepository testRepository;
    @Mock private ActionRelationalRepository actionRelationalRepository;
    @Mock private SchedulerClient schedulerClient;
    @Mock private UserModel currentUser;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private static final String SCHEDULE_ID = "SCH-TEST42-abc12345";
    private static final Integer TEST_ID    = 42;
    private static final Integer USER_ID    = 10;

    // ── Fixtures ───────────────────────────────────────────────────────────────

    private ScheduleEntity activeScheduleEntity() {
        return ScheduleEntity.builder()
                .id(1L)
                .scheduleId(SCHEDULE_ID)
                .testId(TEST_ID)
                .name("Test Schedule")
                .triggerType("cron")
                .expression("{\"minute\":\"0\",\"hour\":\"8\"}")
                .status(ScheduleStatusEnum.ACTIVE.getCode())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /** Simula permiso de EJECUTOR (actionId=5) sobre un test. */
    private ActionRelationalEntity executorPermissionFor(Integer testId) {
        TestEntity test       = TestEntity.builder().id(testId).build();
        UserEntity userEntity = UserEntity.builder().id(USER_ID).build();
        return ActionRelationalEntity.builder()
                .actionId(5)
                .userTo(userEntity)
                .test(test)
                .build();
    }

    /** Simula permiso de EDITOR (actionId=7) sobre un test. */
    private ActionRelationalEntity editorPermissionFor(Integer testId) {
        TestEntity test       = TestEntity.builder().id(testId).build();
        UserEntity userEntity = UserEntity.builder().id(USER_ID).build();
        return ActionRelationalEntity.builder()
                .actionId(7)
                .userTo(userEntity)
                .test(test)
                .build();
    }

    /** Configura currentUser como usuario no-admin (roleId = EXECUTOR = 2). */
    private void setupNonAdminUser() {
        when(currentUser.getId()).thenReturn(USER_ID);
        when(currentUser.getRoleId()).thenReturn(RoleEnum.EXECUTOR.getCode());
        when(currentUser.isSuperAdmin()).thenReturn(false);
    }

    // ── pauseSchedule ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("pauseSchedule()")
    class PauseScheduleTests {

        @Test
        @DisplayName("Bug #116 — schedulerClient.deleteJob falla => NO se persiste estado PAUSED en BD")
        void whenSchedulerClientFails_shouldNotPersistPausedStatus() {
            ScheduleEntity entity = activeScheduleEntity();
            setupNonAdminUser();

            when(scheduleRepository.findByScheduleId(SCHEDULE_ID)).thenReturn(Optional.of(entity));
            when(actionRelationalRepository.findTestsWithSchedulablePermission(USER_ID))
                    .thenReturn(List.of(executorPermissionFor(TEST_ID)));
            doThrow(new BadGatewayException("502000", "Scheduler service unavailable"))
                    .when(schedulerClient).deleteJob(SCHEDULE_ID);

            assertThatThrownBy(() -> scheduleService.pauseSchedule(SCHEDULE_ID))
                    .isInstanceOf(BadGatewayException.class);

            // La BD NO debe ser modificada — invariante crítico del bug #116
            verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
        }

        @Test
        @DisplayName("Usuario con permiso EJECUCION (actionId=5) puede pausar el scheduler")
        void whenUserHasExecutorPermission_shouldCallSchedulerDelete() {
            ScheduleEntity entity = activeScheduleEntity();
            setupNonAdminUser();

            when(scheduleRepository.findByScheduleId(SCHEDULE_ID)).thenReturn(Optional.of(entity));
            when(actionRelationalRepository.findTestsWithSchedulablePermission(USER_ID))
                    .thenReturn(List.of(executorPermissionFor(TEST_ID)));
            when(schedulerClient.deleteJob(anyString()))
                    .thenReturn(JobCreatedModel.builder().jobId(SCHEDULE_ID).scheduled(false).build());
            when(scheduleRepository.save(any())).thenReturn(entity);
            when(testRepository.findById(TEST_ID))
                    .thenReturn(Optional.of(TestEntity.builder().id(TEST_ID).name("My Test").build()));

            scheduleService.pauseSchedule(SCHEDULE_ID);

            verify(schedulerClient).deleteJob(SCHEDULE_ID);
        }

        @Test
        @DisplayName("Usuario con permiso EDICION (actionId=7) puede pausar el scheduler")
        void whenUserHasEditorPermission_shouldCallSchedulerDelete() {
            ScheduleEntity entity = activeScheduleEntity();
            setupNonAdminUser();

            when(scheduleRepository.findByScheduleId(SCHEDULE_ID)).thenReturn(Optional.of(entity));
            when(actionRelationalRepository.findTestsWithSchedulablePermission(USER_ID))
                    .thenReturn(List.of(editorPermissionFor(TEST_ID)));
            when(schedulerClient.deleteJob(anyString()))
                    .thenReturn(JobCreatedModel.builder().jobId(SCHEDULE_ID).scheduled(false).build());
            when(scheduleRepository.save(any())).thenReturn(entity);
            when(testRepository.findById(TEST_ID))
                    .thenReturn(Optional.of(TestEntity.builder().id(TEST_ID).name("My Test").build()));

            scheduleService.pauseSchedule(SCHEDULE_ID);

            verify(schedulerClient).deleteJob(SCHEDULE_ID);
        }

        @Test
        @DisplayName("Happy path — schedulerClient OK => BD actualiza estado a PAUSED")
        void whenSchedulerSucceeds_shouldPersistPausedStatus() {
            ScheduleEntity entity = activeScheduleEntity();
            setupNonAdminUser();

            when(scheduleRepository.findByScheduleId(SCHEDULE_ID)).thenReturn(Optional.of(entity));
            when(actionRelationalRepository.findTestsWithSchedulablePermission(USER_ID))
                    .thenReturn(List.of(executorPermissionFor(TEST_ID)));
            when(schedulerClient.deleteJob(anyString()))
                    .thenReturn(JobCreatedModel.builder().jobId(SCHEDULE_ID).scheduled(false).build());
            when(scheduleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(testRepository.findById(TEST_ID))
                    .thenReturn(Optional.of(TestEntity.builder().id(TEST_ID).name("My Test").build()));

            var result = scheduleService.pauseSchedule(SCHEDULE_ID);

            verify(scheduleRepository).save(argThat(saved ->
                    saved.getStatus().equals(ScheduleStatusEnum.PAUSED.getCode())
            ));
            assertThat(result.getStatusCode()).isEqualTo(ScheduleStatusEnum.PAUSED.getCode());
        }
    }

    // ── deleteSchedule ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteSchedule()")
    class DeleteScheduleTests {

        @Test
        @DisplayName("Bug #116 — schedulerClient falla con error no-404 => NO se persiste DELETED en BD")
        void whenSchedulerClientFailsWithNon404_shouldNotPersistDeletedStatus() {
            ScheduleEntity entity = activeScheduleEntity();
            setupNonAdminUser();

            when(scheduleRepository.findByScheduleId(SCHEDULE_ID)).thenReturn(Optional.of(entity));
            when(actionRelationalRepository.findTestsWithSchedulablePermission(USER_ID))
                    .thenReturn(List.of(executorPermissionFor(TEST_ID)));
            doThrow(new BadGatewayException("502000", "Scheduler service unavailable"))
                    .when(schedulerClient).deleteJob(SCHEDULE_ID);

            assertThatThrownBy(() -> scheduleService.deleteSchedule(SCHEDULE_ID))
                    .isInstanceOf(BadGatewayException.class);

            verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
        }

        @Test
        @DisplayName("Happy path — schedulerClient OK => BD realiza soft-delete con status DELETED")
        void whenSchedulerSucceeds_shouldPersistDeletedStatus() {
            ScheduleEntity entity = activeScheduleEntity();
            setupNonAdminUser();

            when(scheduleRepository.findByScheduleId(SCHEDULE_ID)).thenReturn(Optional.of(entity));
            when(actionRelationalRepository.findTestsWithSchedulablePermission(USER_ID))
                    .thenReturn(List.of(executorPermissionFor(TEST_ID)));
            when(schedulerClient.deleteJob(anyString()))
                    .thenReturn(JobCreatedModel.builder().jobId(SCHEDULE_ID).scheduled(false).build());

            scheduleService.deleteSchedule(SCHEDULE_ID);

            verify(scheduleRepository).save(argThat(saved ->
                    saved.getStatus().equals(ScheduleStatusEnum.DELETED.getCode())
            ));
        }
    }
}
