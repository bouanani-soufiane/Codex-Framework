package com.codex.testing.entities;

import com.codex.framework.EntityManager.Annotations.Column.Column;
import com.codex.framework.EntityManager.Annotations.Entity.Entity;
import com.codex.framework.EntityManager.Annotations.Id.ID;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Entity
public class Admin {
    @ID
    private String id;
    @Column
    private String name;
    @Column
    private String password;
    @Column(name = "byte" , nullable = true , unique = true)
    private byte email;
    @Column(name = "char" , nullable = true , unique = true)
    private char charr;
    @Column(name = "loong" , nullable = true , unique = true)
    private Long loong;
    @Column(name = "boolean" , nullable = true , unique = true)
    private boolean bool;
    @Column
    private double doublee;
    @Column
    private float floatee;
    @Column
    private Date dateEE;
    @Column
    private Timestamp timestampEE;
    @Column
    private BigDecimal bigDecimalEE;
    @Column
    private BigInteger bigIntegerEE;
    @Column
    private LocalDate localDateEE;
    @Column
    private LocalDateTime localDateTimeEE;
    @Column
    private LocalTime localTimeEE;
    @Column
    private UUID uuid;

}
