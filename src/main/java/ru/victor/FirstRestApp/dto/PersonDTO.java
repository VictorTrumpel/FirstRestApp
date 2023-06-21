package ru.victor.FirstRestApp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PersonDTO {
  @NotEmpty
  @Size(min = 2, max = 30, message = "Should be between 2 and 30")
  private String name;

  @NotEmpty(message = "Age should not be empty")
  @Min(value = 0, message = "Possible min age is 0")
  private int age;

  @NotEmpty(message = "Email shoul not be empty")
  private String email;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
