package ru.gb_spring.clientresourceserver.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type User.
 */
@EqualsAndHashCode
@Data
public class User {
    private int id;
    private String firstName;
    private String lastName;
}
