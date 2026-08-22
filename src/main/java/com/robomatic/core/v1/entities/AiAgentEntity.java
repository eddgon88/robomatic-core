package com.robomatic.core.v1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_agent", schema = "core")
public class AiAgentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private String backstory;

    @Column(nullable = false)
    private String llm;

    @Column(nullable = false)
    private String company;

    @Column(name = "max_iterations")
    private Integer maxIterations;

    @Column(name = "verbose_mode")
    private Boolean verbose;

    private Double temperature;

    @Column(name = "folder_id")
    private Integer folderId;

    @Column(name = "user_id")
    private Integer userId;
}
