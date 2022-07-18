package com.robomatic.core.v1.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "action", schema = "core")
public class ActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_from")
    private Integer userFrom;

    @Column(name = "user_to")
    private Integer userTo;

    @Column(name = "action_id")
    private Integer actionId;

    private LocalDateTime date;

    @Column(name = "folder_id")
    private Integer folderId;

    @Column(name = "test_id")
    private Integer testId;

    @Column(name = "test_execution_id")
    private Integer testExecutionId;

}
