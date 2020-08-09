package com.margin.simpleretrofit;

/**
 * Created by  : blank
 * Create one : 2020/8/9 at 18:15
 * Description :
 */
public abstract class ParameterHandler {

    protected String key;

    public ParameterHandler(String key) {
        this.key = key;
    }

    abstract void apply(AFDServiceMethod serviceMethod, String value);

    /**
     * Get
     */
    static class QueryParameterHandler extends ParameterHandler {

        public QueryParameterHandler(String key) {
            super(key);
        }

        @Override
        void apply(AFDServiceMethod serviceMethod, String value) {
            serviceMethod.addQueryParameter(key,value);
        }
    }

    /**
     * Post
     */
    static class FieldParameterHandler extends ParameterHandler {

        public FieldParameterHandler(String key) {
            super(key);
        }

        @Override
        void apply(AFDServiceMethod serviceMethod, String value) {
            serviceMethod.addFieldParameter(key,value);
        }
    }
}
