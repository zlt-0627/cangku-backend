package com.chat.interceptor;
//这里使用拦截器，不使用过滤器，这个拦截器是spring原生的，不用使用severlet

import com.chat.pojo.Result;
import com.chat.util.Jwt;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

//做登录拦截，和生份校验；顺带做接口限流
@Component   // 交给 Spring 管理，这样才注得进 StringRedisTemplate / Jwt
@Slf4j
public class Interceptor implements HandlerInterceptor {

    /* 窗口长度：默认 20 秒（可在 application.yaml 的 rate-limit 段覆盖） */
    @Value("${rate-limit.window-seconds:20}")
    private long windowSeconds;

    /* 窗口内最多允许多少次，超过就拦截 */
    @Value("${rate-limit.limit:20}")
    private long limit;

    /* 限流总开关：压测或排障时可以整体关掉 */
    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private Jwt jwtUtil;

    // 统一用 Spring 自带的 Jackson 写 JSON，不再混用 fastjson2
    @Autowired
    private ObjectMapper objectMapper;

    //进入controller层理面
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // ① 预检请求直接放行。
        // 浏览器跨域时会先发一个 OPTIONS 预检，这个请求不带 token（浏览器规定不能带），
        // 如果这里也要求 token，预检就会拿到 401，浏览器直接判定跨域失败，
        // 前端看到的就是 "Network Error: 可能是后端未启动或 CORS 被拦截"。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ② 限流：拦在最前面，省得后面还要解析 token、查库
        if (!isStatic(request) && !allowRequest(clientIp(request), response)) {
            return false;
        }

        // ③ 最后校验登录
        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            writeJson(response, 401, Result.error("NOT_login"));
            return false;
        }
        //解析令牌，解析后把 role / id 放进 request，controller 不用再解析
        try {
            Claims claims = jwtUtil.parse(token);
            Integer role = (Integer) claims.get("role");
            request.setAttribute("role", role);
            Integer id = (Integer) claims.get("id");
            request.setAttribute("id", id);
        } catch (Exception e) {
            log.warn("token 解析失败：{}", e.getMessage());
            writeJson(response, 401, Result.error("NOT_login"));
            return false;
        }
        return true;
    }

    /*
     * 用 Redis 的 INCR 计数：key 是「rate:req:IP」，每次请求 +1。
     * 第一次访问（返回值等于 1）时才给它设 20 秒过期，之后这个 key 在 20 秒内一直累加，
     * 到点自动删除、计数从 0 重来。计数超过 LIMIT 就拒绝。
     * 为什么只在 count == 1 时设过期：如果每次都 expire，窗口会被不断续命，
     * 只要请求不停就永远不过期，那就不叫 "20 秒" 了。
     */
    private boolean allowRequest(String ip, HttpServletResponse response) {
        // 开关关掉就完全不限流（压测、排障时用）
        if (!rateLimitEnabled) {
            return true;
        }
        String key = "rate:req:" + ip;
        try {
            Long count = redis.opsForValue().increment(key);
            if (count == null) {
                return true;                 // 理论上不会走到，保险起见放行
            }
            if (count == 1L) {
                redis.expire(key, Duration.ofSeconds(windowSeconds));
            }
            if (count > limit) {
                // 兜底：万一 INCR 成功了、EXPIRE 还没执行（比如进程正好在这一瞬间挂了），
                // key 会永不过期，那这个 IP 就被永久封死了。这里顺手补一次过期时间。
                Long ttl = redis.getExpire(key, TimeUnit.SECONDS);
                if (ttl == null || ttl < 0) {
                    redis.expire(key, Duration.ofSeconds(windowSeconds));
                    ttl = windowSeconds;
                }
                response.setHeader("Retry-After", String.valueOf(Math.max(ttl, 1)));
                writeJson(response, 429,
                        Result.error("请求过于频繁，" + windowSeconds + " 秒内最多 " + limit + " 次，请稍后再试"));
                return false;
            }
            return true;
        } catch (Exception e) {
            // Redis 挂了或者网络抖了，放行——限流是保护措施，不该把整个系统拖死
            log.warn("限流计数失败，本次请求直接放行：{}", e.getMessage());
            return true;
        }
    }

    /* 统一把 Result 写成 JSON 响应（以前这里用的是 fastjson2，现在统一成 Jackson） */
    private void writeJson(HttpServletResponse response, int status, Result result) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    /* 取真实客户端 IP：经过 Nginx 之类的代理时 remoteAddr 会是代理的地址，所以先看 X-Forwarded-For */
    private String clientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            // 这个头可能是 "客户端IP, 代理1, 代理2"，第一个才是真实客户端
            int comma = ip.indexOf(',');
            return comma > 0 ? ip.substring(0, comma).trim() : ip.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    /* 静态资源不占额度，否则打开一次页面就把额度用完了 */
    private boolean isStatic(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/static")
                || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".map")
                || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg")
                || uri.endsWith(".gif") || uri.endsWith(".svg") || uri.endsWith(".ico")
                || uri.endsWith(".html") || uri.endsWith(".woff") || uri.endsWith(".woff2")
                || uri.endsWith(".ttf");
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

}
