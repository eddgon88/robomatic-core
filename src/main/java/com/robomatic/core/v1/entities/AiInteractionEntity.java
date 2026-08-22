package com.robomatic.core.v1.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_interaction", schema = "core")
public class AiInteractionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "test_execution_id")
    private Integer testExecutionId;

    @Column(name = "agent_name")
    private String agentName;

    private String prompt;

    private String response;

    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "token_usage")
    private Integer tokenUsage;

    private LocalDateTime timestamp;

}
