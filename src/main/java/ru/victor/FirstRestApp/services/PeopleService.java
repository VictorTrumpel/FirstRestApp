package ru.victor.FirstRestApp.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.victor.FirstRestApp.models.Person;
import ru.victor.FirstRestApp.repositories.PeopleRepository;
import ru.victor.FirstRestApp.util.PersonNotFoundExeption;

@Service
@Transactional(readOnly = true)
public class PeopleService {

  private final PeopleRepository peopleRepository;

  @Autowired
  public PeopleService(PeopleRepository peopleRepository) {
    this.peopleRepository = peopleRepository;
  }

  public List<Person> findAll() {
    return peopleRepository.findAll();
  }

  public Person findOne(int id) {
    Optional<Person> foundPerson = peopleRepository.findById(id);
    return foundPerson.orElseThrow(PersonNotFoundExeption::new);
  }

  @Transactional
  public void save(Person person) {
    enrichPerson(person);

    peopleRepository.save(person);
  }

  private void enrichPerson(Person person) {
    person.setCreatedAt(LocalDateTime.now());
    person.setUpdatedAt(LocalDateTime.now());
    person.setCreatedWho("ADMIN");
  }
}
