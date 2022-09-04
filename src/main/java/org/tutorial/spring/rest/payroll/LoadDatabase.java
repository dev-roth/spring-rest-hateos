package org.tutorial.spring.rest.payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tutorial.spring.rest.payroll.employee.Employee;
import org.tutorial.spring.rest.payroll.employee.EmployeeRepository;
import org.tutorial.spring.rest.payroll.order.Order;
import org.tutorial.spring.rest.payroll.order.OrderRepository;
import org.tutorial.spring.rest.payroll.order.OrderStatus;

@Configuration
class LoadDatabase {

  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  // Spring Boot will run ALL CommandLineRunner beans once the application context is loaded.
  @Bean
  CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

    return args -> {
      employeeRepository.save(new Employee("Bilbo", "Baggins", "burglar"));
      employeeRepository.save(new Employee("Frodo", "Baggins", "thief"));
      employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));

      orderRepository.save(new Order("MacBook Pro", OrderStatus.COMPLETED));
      orderRepository.save(new Order("iPhone", OrderStatus.IN_PROGRESS));
      orderRepository.findAll().forEach(order -> log.info("Preloaded " + order));
    };
  }
}