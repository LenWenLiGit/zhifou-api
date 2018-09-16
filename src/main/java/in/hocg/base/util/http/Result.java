package in.hocg.base.util.http;

import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @author hocgin
 * @date 2017/10/14
 * email: hocgin@gmail.com
 * 响应结果对象
 */
public class Result<T> implements Serializable {
    enum Code {
        /**
         * 成功
         */
        Success(200, "成功"),
        Error(500, "异常");
        int code;
        String message;
        
        Code(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
    
    private static final int SUCCESS = 200;
    
    private int code;
    private String message;
    private T data;
    
    private Result() {
    }
    
    public static Result get() {
        return new Result();
    }
    
    public int getCode() {
        return code;
    }
    
    public Result setCode(int code) {
        this.code = code;
        return this;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Result setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public T getData() {
        return data;
    }
    
    public Result setData(T data) {
        this.data = data;
        return this;
    }
    
    
    public static Result success(Object data) {
        return Result.result(Code.Success.code, Code.Success.message, data);
    }
    
    public static Result success() {
        return Result.success(null);
    }
    
    public static Result result(Integer code, String message) {
        return Result.result(code, message, null);
    }
    
    public static Result error(String message) {
        return Result.result(Code.Error.code, message, null);
    }
    
    public static Result error() {
        return Result.result(Code.Error.code, Code.Error.message, null);
    }
    
    public static Result result(Integer code, String message, Object data) {
        Result result = new Result<>();
        return result.setCode(code)
                .setMessage(message)
                .setData(data);
    }
    
    public ResponseEntity<Result> asResponseEntity() {
        return ResponseEntity.ok(this);
    }
}
