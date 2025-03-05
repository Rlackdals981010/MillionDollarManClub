package com.dolloer.million.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("https://milliondollarmanclubfront.onrender.com")
      .allowedMethods("GET", "POST", "PUT", "DELETE")
      .allowedHeaders("*")
      .allowCredentials(true); // 쿠키나 인증 헤더 허용
  }

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//            .allowedOrigins("http://localhost:3000")
//            .allowedMethods("GET", "POST", "PUT", "DELETE")
//            .allowedHeaders("*")
//            .allowCredentials(true); // 쿠키나 인증 헤더 허용
//  }
}