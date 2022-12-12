package com.ecnu.tripmap.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private Integer code;

    private String message;

    private Object data;

    public Response(ResponseStatus status, Object aData) {
        code = status.getCode();
        message = status.getMessage();
        data = aData;
    }

    public static Response success(Object data) {
        return new Response(ResponseStatus.OK, data);
    }

    public static Response success() {
        return new Response(ResponseStatus.OK, null);
    }

    public static Response success(String message) {
        return new Response(ResponseStatus.OK.getCode(), message, null);
    }

    public static Response status(ResponseStatus status) {
        return new Response(status, null);
    }

    public static Response status(ResponseStatus status, Object data) {
        return new Response(status, data);
    }


    public static <T extends Exception> Response exception(T t, Object data) {
        return new Response(-1, t.getMessage(), data);
    }

    public static <T extends Exception> Response exception(T t) {
        return exception(t, null);
    }


}
