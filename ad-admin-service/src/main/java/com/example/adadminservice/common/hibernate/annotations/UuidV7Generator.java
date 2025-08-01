package com.example.adadminservice.common.hibernate.annotations;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@IdGeneratorType(com.example.adadminservice.common.hibernate.UuidV7Generator.class)
public @interface UuidV7Generator {
}
