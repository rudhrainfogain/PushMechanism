package com.fedex.peripherals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;

import com.fedex.common.cxs.CXSCommonLib;

/**
* Copyright (c) 2019 FedEx. All Rights Reserved.<br/>
* 
 * Theme - Core Retail Peripheral Services<br/>
* Feature - Peripheral Services - Design and Architecture.<br/>
* Description - This class is main class for peripheral application
* 
 * @author Puneet Gupta [3696361]
* @version 0.0.1
* @since 15-Jul-2019
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = {RTPeripheralsApplication.class, CXSCommonLib.class})
public class RTPeripheralsApplication {

    public static void main(
            final String[] args) {

        SpringApplication.run(RTPeripheralsApplication.class, args);
    }

    @Bean
    public SecurityScheme apiKey() {

        return new ApiKey("mykey", "api_key", "header");
    }



}
