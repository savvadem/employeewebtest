package com.example.test.employee.controllers

import com.example.test.employee.model.Department
import com.example.test.employee.model.Employee
import com.example.test.employee.repository.DepartmentsRepository
import com.example.test.employee.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/init")
class InitController {

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var departmentsRepository: DepartmentsRepository

    @RequestMapping("/initdb")
    fun init(): String {
        clearDb()
        initDepartments()
        employeeRepository.save(Employee("Jack", "Smith",
                departmentsRepository.findByDepartmentName("HR")))
        employeeRepository.save(Employee("Adam", "Johnson",
                departmentsRepository.findByDepartmentName("DEV")))
        employeeRepository.save(Employee("Kim", "Smith",
                departmentsRepository.findByDepartmentName("DEV")))
        employeeRepository.save(Employee("David", "WilliamTs"))
        employeeRepository.save(Employee("Peter", "Davis"))

        return "Done"
    }

    fun initDepartments() {
        departmentsRepository.save(Department("DEV"))
        departmentsRepository.save(Department("HR"))
        departmentsRepository.save(Department("DBA"))
        departmentsRepository.save(Department("PAY"))
    }

    fun clearDb() {
        employeeRepository.deleteAll()
        departmentsRepository.deleteAll()
    }
}


@RestController
@RequestMapping("/employee")
class EmployeeController {

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var departmentsRepository: DepartmentsRepository

    @RequestMapping("/findbydepartment/{departmentName}")
    fun findByDepartment(@PathVariable departmentName: String): String {
        val departmentId = departmentsRepository.findByDepartmentName(departmentName)
        if (departmentId != null) {
            val findByDepartment = employeeRepository.findByDepartment(
                    departmentId)
            return findByDepartment.joinToString()
        }
        return "Didn't found department by name $departmentName"
    }


    @RequestMapping("/findall")
    fun getAllEmployee(): Iterable<Employee> = employeeRepository.findAll()


    @RequestMapping("/createEmployee", method = [RequestMethod.POST],
            consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createEmployee(@RequestBody employee: Employee): String {
        val result = employeeRepository.save(Employee(employee.firstName, employee.lastName,
                employee.department?.let {
                    departmentsRepository.findByDepartmentName(it.departmentName)
                }))
        return result.toString()
    }

    @RequestMapping("/updateEmployee", method = [RequestMethod.POST],
            consumes = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun updateEmployee(@RequestBody employee: Employee): String {
        val result = employeeRepository.save(Employee(employee.firstName, employee.lastName,
                employee.department?.let {
                    departmentsRepository.findByDepartmentName(it.departmentName)
                }, employee.id))
        return result.toString()
    }

    @RequestMapping("/deleteEmployee/{employeeId}")
    fun deleteEmployee(@PathVariable employeeId: Long) = employeeRepository.deleteById(employeeId)

    @RequestMapping("/removeEmployeeDepartment/{employeeId}")
    fun removeEmployeeDepartment(@PathVariable employeeId: Long): String {
        val employee = employeeRepository.findById(employeeId).map {
            Employee(it.firstName, it.lastName,
                    null, it.id)
        }
        if (employee.isPresent) employeeRepository.save(employee.get())

        return "Removed department from employee $employeeId"
    }
}
