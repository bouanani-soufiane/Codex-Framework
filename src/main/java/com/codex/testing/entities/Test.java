package com.codex.testing.entities;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Id.ID;

@Entity
public class Test {
    @ID
    private String id;
    @Column
    private Types name;
}
