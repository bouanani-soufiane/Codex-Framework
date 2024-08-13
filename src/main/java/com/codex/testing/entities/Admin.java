package com.codex.testing.entities;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;

import com.codex.framework.EntityManager.Annotations.Relationship.*;

import java.util.Set;



public class Admin {
    @ID
    private String id;
    @Column
    private String name;
    @ManyToOne(cascade = "PERSIST")
    private Employee employee_id;

    @ManyToMany(cascade = "REFRESH")
    @JoinTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id" )
    )
    private Set<Roles> roles;


}
