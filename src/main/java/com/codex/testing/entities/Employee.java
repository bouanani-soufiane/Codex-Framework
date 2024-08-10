package com.codex.testing.entities;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Id.ID;

@Entity
public class Employee {
    @ID
    private int id;
    @Column(name = "user_name" , type = "VARCHAR(255)")
    private String name;
}
