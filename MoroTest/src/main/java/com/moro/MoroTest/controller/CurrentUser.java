package com.moro.MoroTest.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

//https://docs.spring.io/spring-security/site/docs/4.0.0.RELEASE/reference/html/mvc.html
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {}
