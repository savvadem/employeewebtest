package com.example.test.employee.controllers

import com.example.test.employee.model.Department
import com.example.test.employee.model.Employee
import com.example.test.employee.repository.DepartmentsRepository
import com.example.test.employee.repository.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/init")
class InitController {

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var departmentsRepository: DepartmentsRepository

    @RequestMapping("/initdb", method = [RequestMethod.GET])
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

        return "Done adding values"
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
class EmployeeController {

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var departmentsRepository: DepartmentsRepository

    @RequestMapping("/employees/department/{departmentName}", method = [RequestMethod.GET])
    fun findByDepartment(@PathVariable departmentName: String): String {
        val departmentId = departmentsRepository.findByDepartmentName(departmentName)
        if (departmentId != null) {
            val findByDepartment = employeeRepository.findByDepartment(
                    departmentId)
            return findByDepartment.joinToString()
        }
        return "Didn't found department by name $departmentName"
    }


    @RequestMapping("/employees/{employeeId}", method = [RequestMethod.GET])
    fun getEmployeeById(@PathVariable employeeId: Long): String {
        return employeeRepository.findById(employeeId).toString()
    }

    @RequestMapping(method = [RequestMethod.GET])
    fun getAllEmployee(): Iterable<Employee> = employeeRepository.findAll()


    @RequestMapping("/employees", method = [RequestMethod.POST],
            consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createEmployee(@RequestBody employee: Employee): String {
        val result = employeeRepository.save(Employee(employee.firstName, employee.lastName,
                employee.department?.let {
                    departmentsRepository.findByDepartmentName(it.departmentName)
                }))
        return result.toString()
    }

    @RequestMapping("/employees", method = [RequestMethod.PUT],
            consumes = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun updateEmployee(@RequestBody employee: Employee): String {
        val result = employeeRepository.save(Employee(employee.firstName, employee.lastName,
                employee.department?.let {
                    departmentsRepository.findByDepartmentName(it.departmentName)
                }, employee.id))
        return result.toString()
    }

    @RequestMapping("/employees/{employeeId}", method = [RequestMethod.DELETE])
    fun deleteEmployee(@PathVariable employeeId: Long) = employeeRepository.deleteById(employeeId)

    @RequestMapping("/employees/{employeeId}/department", method = [RequestMethod.DELETE])
    fun removeEmployeeDepartment(@PathVariable employeeId: Long): String {
        val employee = employeeRepository.findById(employeeId).map {
            Employee(it.firstName, it.lastName,
                    null, it.id)
        }
        if (employee.isPresent) employeeRepository.save(employee.get())

        return "Removed department from employee $employeeId"
    }
}
