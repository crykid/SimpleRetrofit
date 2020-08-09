package com.margin.simpleretrofit;

import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDField;
import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDGet;
import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDPost;
import com.margin.afd_annotation.customer_retrofit.afd_annotation.AFDQuery;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by  : blank
 * Create one : 2020/8/9 at 17:54
 * Description :
 */
public class AFDServiceMethod {

    private okhttp3.Call.Factory callFactory;
    private final String httpMethod;
    private final boolean hasBody;
    private final ParameterHandler[] parameterHandlers;
    private FormBody.Builder formBuilder;
    private final String relativeUrl;
    private HttpUrl baseUrl;
    private HttpUrl.Builder urlBuilder;

    public AFDServiceMethod(Builder builder) {
        this.baseUrl = builder.retrofit.baseUrl;
        this.callFactory = builder.retrofit.callFactory;
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.hasBody = builder.hasBody;
        this.parameterHandlers = builder.parameterHandlers;
        //如果有请求体，创建一个http的请求对象
        if (hasBody) {
            formBuilder = new FormBody.Builder();
        }
    }

    public Object invoke(Object[] args) {
        //处理请求的地址与参数
        for (int i = 0; i < parameterHandlers.length; i++) {
            ParameterHandler parameterHandler = parameterHandlers[i];
            parameterHandler.apply(this, args[i].toString());
        }

        HttpUrl url = null;
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        url = urlBuilder.build();
        FormBody formBody = null;
        if (formBuilder != null) {
            formBody = formBuilder.build();

        }
        //开始请求
        Request request = new Request.Builder().url(url).method(httpMethod, formBody).build();
        return callFactory.newCall(request);
    }

    /**
     * get 请求，把key拼接到url上
     *
     * @param key
     * @param value
     */
    public void addQueryParameter(String key, String value) {
        if (urlBuilder == null) {
            urlBuilder = baseUrl.newBuilder(relativeUrl);
        }
        urlBuilder.addQueryParameter(key, value);
    }

    /**
     * post请求，把参数放到body里
     *
     * @param key
     * @param value
     */
    public void addFieldParameter(String key, String value) {

        formBuilder.add(key, value);
    }

    public static class Builder {
        private final AFDRetrofit retrofit;
        private final Annotation[] methodAnotations;
        private final Annotation[][] parameterAnnotations;
        private String httpMethod;
        private String relativeUrl;
        private boolean hasBody;
        private ParameterHandler[] parameterHandlers;

        public Builder(AFDRetrofit retrofit, Method method) {
            this.retrofit = retrofit;
            methodAnotations = method.getAnnotations();
            //获得方法参数的所有注解（一个参数可以有多个注解，一个方法又会有多个参数）
            parameterAnnotations = method.getParameterAnnotations();
        }

        public AFDServiceMethod build() {

            for (Annotation methodAnotation : methodAnotations) {
                if (methodAnotation instanceof AFDPost) {
                    this.httpMethod = "POST";
                    this.relativeUrl = ((AFDPost) methodAnotation).value();
                    //时否有body
                    this.hasBody = true;
                } else if (methodAnotation instanceof AFDGet) {
                    this.httpMethod = "GET";
                    this.relativeUrl = ((AFDGet) methodAnotation).value();
                    //时否有body
                    this.hasBody = false;
                }
            }
            //参数数量
            int length = parameterAnnotations.length;
            parameterHandlers = new ParameterHandler[length];
            for (int i = 0; i < length; i++) {
                //得到一个参数上的注解
                final Annotation[] parameterAnnotation = parameterAnnotations[i];
                //参数上的每个注解
                for (Annotation annotation : parameterAnnotation) {
                    //如果httpMethod是get请求，现在有解析到Field注解，可以提示使用者使用Query注解
                    if (annotation instanceof AFDField) {
                        String value = ((AFDField) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.FieldParameterHandler(value);
                    } else if (annotation instanceof AFDQuery) {
                        String value = ((AFDQuery) annotation).value();
                        parameterHandlers[i] = new ParameterHandler.QueryParameterHandler(value);
                    }
                }
            }

            return new AFDServiceMethod(this);
        }

    }
}
