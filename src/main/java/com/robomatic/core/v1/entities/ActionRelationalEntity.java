package com.robomatic.core.v1.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "action", schema = "core")
public class ActionRelationalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_from")
    private UserEntity userFrom;

    @ManyToOne
    @JoinColumn(name = "user_to")
    private UserEntity userTo;

    @Column(name = "action_id")
    private Integer actionId;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private FolderEntity folder;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private TestEntity test;

    @ManyToOne
    @JoinColumn(name = "test_execution_id")
    private TestExecutionEntity testExecution;

}
