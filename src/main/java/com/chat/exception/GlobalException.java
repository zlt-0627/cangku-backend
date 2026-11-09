package com.chat.exception;


import com.chat.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理。
 * <p>
 * 改造点：
 * 1. 以前只有两个 handler、且不打日志——出了问题只能猜。现在全部落到日志里。
 * 2. 空指针这类"技术异常"不再把 Java 的异常信息直接甩给前端
 * （以前会返回 "Cannot invoke LoginPo.getPassword() because ..." 这种），
 * 统一成一句给用户看的话。
 * 3. 补了参数校验失败的处理器，@Valid 报错时能返回具体哪个字段不合法。
 */
@RestControllerAdvice
@Slf4j
public class GlobalException {

    /** 业务异常：消息本来就是写给人看的（"商品已经存在"这种），原样返回 */
    @ExceptionHandler(BizException.class)
    public Result ownException(BizException e) {
        log.warn("业务异常：{}", e.getMsg());
        return Result.error(e.getMsg());
    }

    /** 参数校验失败（@Valid 抛出来的），把第一个字段的错误提示返回给前端 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError == null ? "参数不合法" : fieldError.getDefaultMessage();
        log.warn("参数校验失败：{}", msg);
        return Result.error(msg);
    }

    /** 其它运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    public Result runningException(RuntimeException e) {
        // 完整堆栈进日志，方便排查；前端只拿一句话
        log.error("运行时异常：{}", e.getMessage(), e);

        String msg = e.getMessage();
        // 空指针 / 异常信息里带 Java 技术细节的，不往外抛
        if (e instanceof NullPointerException || msg == null || msg.isBlank() || msg.contains("because")) {
            msg = "系统繁忙，请稍后再试";
        }
        return Result.error(msg);
    }
}
