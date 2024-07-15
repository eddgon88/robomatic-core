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

}
