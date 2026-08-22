package com.robomatic.core.v1.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test", schema = "core")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "test_id")
    private String testId;

    private String name;

    private Integer threads;

    private String script;

    @Column(name = "before_script")
    private String beforeScript;

    @Column(name = "after_script")
    private String afterScript;

    private boolean web;

    @Column(name = "folder_id")
    private Integer folderId;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "test_ai_agent",
        schema = "core",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "ai_agent_id")
    )
    private List<AiAgentEntity> agents;

}
