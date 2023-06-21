package ru.victor.FirstRestApp.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.validation.Valid;
import ru.victor.FirstRestApp.dto.PersonDTO;
import ru.victor.FirstRestApp.models.Person;
import ru.victor.FirstRestApp.services.PeopleService;
import ru.victor.FirstRestApp.util.PersonErrorResponse;
import ru.victor.FirstRestApp.util.PersonNotCreatedException;
import ru.victor.FirstRestApp.util.PersonNotFoundExeption;

@RestController
@RequestMapping("/people")
public class PeopleController {

  private final PeopleService peopleService;
  private final ModelMapper modelMapper;

  @Autowired
  public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
    this.peopleService = peopleService;
    this.modelMapper = modelMapper;
  }

  @GetMapping()
  public List<PersonDTO> getPeople() {
    return peopleService
        .findAll()
        .stream()
        .map(this::convertToPersonDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public PersonDTO getPerson(@PathVariable("id") int id) {
    return convertToPersonDTO(peopleService.findOne(id));
  }

  @PostMapping
  public ResponseEntity<HttpStatus> create(
      @Valid @RequestBody PersonDTO personDTO,
      BindingResult bindingResult) {

    System.out.println("has errors " + bindingResult.hasErrors());

    if (bindingResult.hasErrors()) {
      System.out.println("Errors");

      StringBuilder errorMsg = new StringBuilder();

      List<FieldError> errors = bindingResult.getFieldErrors();

      for (FieldError error : errors) {
        errorMsg
            .append(error.getField())
            .append(" - ")
            .append(error.getDefaultMessage())
            .append(";");
      }

      throw new PersonNotCreatedException(errorMsg.toString());
    }

    peopleService.save(convertToPerson(personDTO));

    // отправляем HTTP ответ с пустым телом и со статусом 200
    return ResponseEntity.ok(HttpStatus.OK);
  }

  @ExceptionHandler
  private ResponseEntity<PersonErrorResponse> handleExeption(PersonNotFoundExeption e) {
    PersonErrorResponse response = new PersonErrorResponse(
        "Person with this id wasn't found",
        System.currentTimeMillis());

    return new ResponseEntity<PersonErrorResponse>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  private ResponseEntity<PersonErrorResponse> handleExeption(PersonNotCreatedException e) {
    PersonErrorResponse response = new PersonErrorResponse(
        e.getMessage(),
        System.currentTimeMillis());

    return new ResponseEntity<PersonErrorResponse>(response, HttpStatus.BAD_REQUEST);
  }

  private Person convertToPerson(PersonDTO personDTO) {
    return modelMapper.map(personDTO, Person.class);
  }

  private PersonDTO convertToPersonDTO(Person person) {
    return modelMapper.map(person, PersonDTO.class);
  }
}
