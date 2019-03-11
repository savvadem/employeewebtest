package com.example.test.employee.model

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
@Table(name="Employees")
data class Employee(
        @JsonProperty("first_name")
        val firstName: String,
        @JsonProperty("last_name")
        val lastName: String,
        @ManyToOne
        @JoinColumn(name="department_id")
        @JsonProperty("department")
        val department: Department? = null,
        @JsonProperty("id")
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1) {

    private constructor() : this("", "")
}

@Entity
@Table(name="Departments")
data class Department(
        @Column(unique = true)
        @JsonProperty("department_name")
        val departmentName: String,
        @JsonProperty("id")
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = -1) {

    private constructor() : this("")
}