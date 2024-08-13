package com.codex;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Entity.Table;
import com.codex.framework.EntityManager.Annotations.Id.ID;

@Entity
@Table(name = "userTable")
public class User {
    @ID
    private String id;
    @Column
    private String name;
}
