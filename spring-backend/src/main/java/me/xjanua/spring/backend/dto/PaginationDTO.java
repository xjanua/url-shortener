package me.xjanua.spring.backend.dto;

import lombok.Getter;
import lombok.Setter;

public class PaginationDTO {

    @Getter
    @Setter
    public static class Info {
        private int page;
        private int size;
        private int pages;
        private long total;
    }

    @Getter
    @Setter
    public static class Response {
        private Info info;
        private Object response;
    }
}

