package com.codex.testing.entities;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Id.ID;

@Entity
public class Depaetement {

    @ID
    private Long id;

    @Column(name = "name_dep" ,type = "VARCHAR(255)")
    private String depaetementName;
}
