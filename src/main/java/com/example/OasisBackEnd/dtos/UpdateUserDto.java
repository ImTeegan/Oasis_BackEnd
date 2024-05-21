package com.example.OasisBackEnd.dtos;

public class UpdateUserDto {
    private String email;
    private String password;
    private String lastName;
    private String name;

    public String getName() {
        return name;
    }

    public UpdateUserDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UpdateUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UpdateUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UpdateUserDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Override
    public String toString() {
        return "UpdateUserDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
