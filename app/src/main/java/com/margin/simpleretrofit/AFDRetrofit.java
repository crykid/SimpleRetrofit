package com.margin.simpleretrofit;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class AFDRetrofit {
    public final HttpUrl baseUrl;
    public final okhttp3.Call.Factory callFactory;

    final Map<Method, AFDServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();

    public AFDRetrofit(HttpUrl baseUrl, Call.Factory callFactory) {
        this.baseUrl = baseUrl;
        this.callFactory = callFactory;
    }

    public <T> T create(Class<T> apiInterface) {

        return (T) Proxy.newProxyInstance(apiInterface.getClassLoader(), new Class[]{apiInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                AFDServiceMethod serviceMethod = loadServiceMethod(method);
                return serviceMethod.invoke(args);
            }

        });
    }

    private AFDServiceMethod loadServiceMethod(Method method) {
        //先不上锁，避免性能消耗
        AFDServiceMethod serviceMethod = serviceMethodCache.get(method);
        if (serviceMethod != null) {
            return serviceMethod;
        }
        synchronized (serviceMethodCache) {
            serviceMethod = serviceMethodCache.get(method);
            if (serviceMethod == null) {
                serviceMethod = new AFDServiceMethod.Builder(this, method).build();
                serviceMethodCache.put(method, serviceMethod);
                return serviceMethod;
            }
        }
        return serviceMethod;
    }

    public static final class Builder {
        private HttpUrl baseUrl;
        private okhttp3.Call.Factory callFactory;

        public Builder callFactory(okhttp3.Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = HttpUrl.get(URI.create(baseUrl));
            return this;
        }

        public AFDRetrofit build() {
            if (baseUrl == null) throw new IllegalStateException("Base URL is required !");
            if (callFactory == null) {
                callFactory = new OkHttpClient();
            }

            return new AFDRetrofit(baseUrl, callFactory);
        }
    }


}
