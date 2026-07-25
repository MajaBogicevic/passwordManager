package org.service.passwordman.desktopApi.handler;

import java.util.function.Supplier;

import org.service.passwordman.desktopApi.mapper.ErrorDesktopMapper;

public class ApiHandler {

    private final ErrorDesktopMapper errorDesktopMapper;

    public ApiHandler(ErrorDesktopMapper errorDesktopMapper) {
        this.errorDesktopMapper = errorDesktopMapper;
    }

    public <T> Object execute(Supplier<T> action) {
        try {
            return action.get();
        } catch (RuntimeException ex) {
            return errorDesktopMapper.map(ex);
        }
    }
}