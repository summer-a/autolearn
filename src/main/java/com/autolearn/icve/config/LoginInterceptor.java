package com.autolearn.icve.config;

import com.auth0.jwt.exceptions.*;
import com.autolearn.icve.entity.icve.ResultVO;
import com.autolearn.icve.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.xiaoleilu.hutool.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: LoginIn
 * @projectName autolearn
 * @description: TODO
 * @date 2020/8/21 12:13
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 前置处理器
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 结果集
        ResultVO resultVO = new ResultVO();

        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            response.setHeader("Access-control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", request.getMethod());
            response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
            response.setStatus(HttpStatus.OK.value());
            return false;
        }

        String token = request.getHeader("token");

        try {
            JWTUtil.require(token);
            // 验证通过
            return true;
        } catch (NullPointerException e) {
            resultVO.setMsg("token为空");
            e.printStackTrace();
        } catch (TokenExpiredException e) {
            resultVO.setMsg("token已过期");
            e.printStackTrace();
        } catch (AlgorithmMismatchException e) {
            resultVO.setMsg("算法匹配异常");
            e.printStackTrace();
        } catch (InvalidClaimException e) {
            resultVO.setMsg("无效字段");
            e.printStackTrace();
        } catch (SignatureVerificationException e) {
            resultVO.setMsg("签名验证失败");
            e.printStackTrace();
        } catch (JWTDecodeException e) {
            resultVO.setMsg("jwt解码失败");
            e.printStackTrace();
        } catch (Exception e) {
            resultVO.setMsg("token验证失败");
            e.printStackTrace();
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        resultVO.setCode(500);
        JSONObject jsonObject = new JSONObject(resultVO);
        response.getWriter().write(jsonObject.toString());
        return false;
    }
}