package com.quartz.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public final class SimpleService {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleService.class);

    public void hello(final int id) {
        LOG.info("Hello World! {}", id);
    }
}
