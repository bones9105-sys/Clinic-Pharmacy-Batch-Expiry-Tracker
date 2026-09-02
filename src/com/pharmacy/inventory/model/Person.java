package com.pharmacy.inventory.model;

// =====================================================
// Person (Abstract class)
// Done by: Sandesh Aryal
// This is the PARENT class for all people in the system.
// It is ABSTRACT - you cannot create a Person directly,
// only its child classes (like User).
// This demonstrates ABSTRACTION.
// =====================================================
public abstract class Person {

    // private fields = ENCAPSULATION
    private int id;
    private String name;

    // constructor
    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // abstract method - child classes MUST write their own version
    // this demonstrates POLYMORPHISM
    public abstract String getRole();
}