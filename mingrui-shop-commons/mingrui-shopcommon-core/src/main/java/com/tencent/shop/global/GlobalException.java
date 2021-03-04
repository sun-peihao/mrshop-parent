package com.tencent.shop.global;

import com.google.gson.JsonObject;
import com.tencent.shop.base.Result;
import com.tencent.shop.status.HTTPStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName GlobalException
 * @Description: TODO
 * @Author sunpeihao
 * @Date 2020/12/24
 * @Version V1.0
 **/
@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(value = RuntimeException.class)
    public Result<JsonObject> test(RuntimeException e){
        log.error("code : {},message : {}", HTTPStatus.ERROR,e.getMessage());
        return new Result<JsonObject>(HTTPStatus.ERROR,e.getMessage(),null);
    }

    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public Map<String,Object> methodArgumentNotValidHandler(MethodArgumentNotValidException exception) throws Exception{
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",HTTPStatus.PARAMS_VALIDATE_ERROR);
        List<String> msgList = new ArrayList<>();
//        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
//            msgList.add("Field --> " + error.getField() + " : " + error.getDefaultMessage());
//            log.error("Field --> " + error.getField() + " : " + error.getDefaultMessage());
//        }
        exception.getBindingResult().getFieldErrors().stream().forEach(error -> {
            msgList.add("Field --> " + error.getField() + " : " + error.getDefaultMessage());
            log.error("Field --> " + error.getField() + " : " + error.getDefaultMessage());
        });
        String message = msgList.parallelStream().collect(Collectors.joining(","));
        map.put("message",message);

        return map;
    }

}
