package com.onesion.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.onesion.reggie.common.BaseContext;
import com.onesion.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 * 这里是用的过滤器
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 登录功能完善 使用过滤器
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     *
     * 1、获取本次请求的URI
     * 2、判断本次请求是否需要处理
     * 3、如果不需要处理，则直接放行
     * 4、判断登录状态，如果已登录，则直接放行
     * 5、如果未登录则返回未登录结果
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 1. 获取本次请求的URI
        String requestURI = request.getRequestURI(); // /backend/index.html

        log.info("拦截到请求：{}", requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[] {
                "/employee/login",
                "employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",  // 这里对上传下载不登录也是可以访问的
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        // 2. 判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3. 如果不需要处理，则直接放行
        if(check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 4-1. 判断登录状态，如果已登录，则直接放行  这个是后台员工登录
        if(request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));

            Long empId = (Long)request.getSession().getAttribute("employee");

            // ThreadLocal相关的工具类 将当前员工id存放到ThreadLocal中
            BaseContext.setCurrentId(empId);

//            long id = Thread.currentThread().getId();
//            log.info("线程id为：{}", id);

            filterChain.doFilter(request, response);
            return;
        }

        // 4-2. 判断登录状态，如果已登录，则直接放行  // 这个是用户登录
        if(request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            Long userId = (Long)request.getSession().getAttribute("user");

            // ThreadLocal相关的工具类 将当前用户id存放到ThreadLocal中
            BaseContext.setCurrentId(userId);

//            long id = Thread.currentThread().getId();
//            log.info("线程id为：{}", id);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        // 5. 如果未登录则返回未登录结果，通过输出流的方式向客户端页面来响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;
    }

    /**
     * 路径匹配方法
     * 检查本次请求是否需要放行，判断请求是否需要处理
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {
                return true;
            }
        }
        return false;
    }

}
