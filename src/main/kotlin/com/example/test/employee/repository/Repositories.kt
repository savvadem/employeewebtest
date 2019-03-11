package com.example.test.employee.repository

import com.example.test.employee.model.Department
import com.example.test.employee.model.Employee
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository : CrudRepository<Employee, Long> {

    fun findByLastName(lastName: String): Iterable<Employee>

    @Query("select c from Employee c where c.department = :departmentId")
    fun findByDepartment(@Param("departmentId") departmentId: Department) : Iterable<Employee>
}

@Repository
interface DepartmentsRepository : CrudRepository<Department, Long> {

    fun findByDepartmentName(departmentName: String): Department?
}