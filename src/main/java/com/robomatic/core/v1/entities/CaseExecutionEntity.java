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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "case_execution", schema = "core")
public class CaseExecutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "case_execution_id")
    private String caseExecutionId;

    @Column(name = "test_execution_id")
    private String testExecutionId;

    @Column(name = "case_results_dir")
    private String testResultsDir;

    private String status;

}
