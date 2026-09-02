package com.pharmacy.inventory.model;

// =====================================================
// User (child class of Person)
// Done by: Sandesh Aryal
// A User IS-A Person -> this is INHERITANCE.
// User extends Person, so it gets id and name for free,
// and adds its own extra field: username.
// =====================================================
public class User extends Person {

    private String username;

    // constructor - super() calls the parent (Person) constructor
    public User(int id, String name, String username) {
        super(id, name);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // we MUST write this because Person declared it abstract
    @Override
    public String getRole() {
        return "Pharmacy Staff";
    }
}