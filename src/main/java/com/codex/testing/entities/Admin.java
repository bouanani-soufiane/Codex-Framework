package com.codex.testing.entities;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;

import com.codex.framework.EntityManager.Annotations.Relationship.ManyToOne;


@Entity
@Table(name = "Administrator")
public class Admin {
    @ID
    private String id;
    @Column
    private String name;
    @ManyToOne()
    private Employee employee_id;



}
