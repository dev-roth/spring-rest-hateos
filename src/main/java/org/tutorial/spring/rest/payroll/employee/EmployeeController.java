package org.tutorial.spring.rest.payroll.employee;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

  private final EmployeeRepository repository;
  private final EmployeeModelAssembler assembler;

  EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }

  // Aggregate root //

  @GetMapping("/employees")
  List<Employee> all() {
    return repository.findAll();
  }

  @PostMapping("/employees")
  Employee newEmployee(@RequestBody Employee newEmployee) {
    return repository.save(newEmployee);
  }

  @PostMapping("/hateos/employees")
  ResponseEntity<?> hyperNewEmployee(@RequestBody Employee newEmployee) {
    EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));
    
    return ResponseEntity
        // created() creates 201 HTTP status code and sts Location HTTP header
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @GetMapping("/hateos/employees")
  CollectionModel<EntityModel<Employee>> hyperAll() {
    List<EntityModel<Employee>> employees = repository.findAll().stream()
        .map(
          // code without an assembler //
          // employee -> EntityModel.of(employee,
          //   linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
          //   linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
          assembler::toModel)
        .collect(Collectors.toList());

    return CollectionModel.of(employees, 
      linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
  }

  // Single item root //

  @GetMapping("/employees/{id}")
  Employee one(@PathVariable Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));
  }

  @GetMapping("/hateos/employees/{id}")
  EntityModel<Employee> hyperOne(@PathVariable Long id) {
    Employee employee = repository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    // code without an assembler //
    // return EntityModel.of(employee,
    //     linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
    //     linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
    return assembler.toModel(employee);
  }

  @PutMapping("/employees/{id}")
  Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
    return repository.findById(id)
        .map(employee -> {
          employee.setName(newEmployee.getName());
          employee.setRole(newEmployee.getRole());
          return repository.save(employee);
        })
        .orElseGet(() -> {
          newEmployee.setId(id);
          return repository.save(newEmployee);
        });
  }

  @DeleteMapping("/employees/{id}")
  void deleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
  }

  @DeleteMapping("/hateos/employees/{id}")
  ResponseEntity<?> hyperDeleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
  
}