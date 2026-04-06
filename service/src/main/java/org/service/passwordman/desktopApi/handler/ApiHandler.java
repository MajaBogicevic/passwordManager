package org.service.passwordman.desktopApi.handler;

import org.service.passwordman.desktopApi.mapper.ErrorDesktopMapper;

import java.util.function.Supplier;

public class ApiHandler {

    private final ErrorDesktopMapper errorDesktopMapper;

    public ApiHandler(ErrorDesktopMapper errorDesktopMapper) {
        this.errorDesktopMapper = errorDesktopMapper;
    }

    public <T> Object execute(Supplier<T> action) {
        try {
            return action.get();
        } catch (Throwable ex) {
            return errorDesktopMapper.map(ex);
        }
    }
}