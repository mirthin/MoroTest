package com.moro.MoroTestKotlin.annotation

import org.springframework.security.core.annotation.AuthenticationPrincipal

//https://docs.spring.io/spring-security/site/docs/4.0.0.RELEASE/reference/html/mvc.html
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal
annotation class CurrentUser