package com.or2go.or2gopartner;

import android.util.Log;

public class Or2GoLogger {

    public enum LOGGER_DEPTH{
        ACTUAL_METHOD(4),
        LOGGER_METHOD(3),
        STACK_TRACE_METHOD(1),
        JVM_METHOD(0);

        private final int value;
        private LOGGER_DEPTH(final int newValue){
            value = newValue;
        }
        public int getValue(){
            return value;
        }
    }
    //contents
    private static final String personalTAG = "Logger";
    //fields
    private StringBuilder sb;
    //constructors
    public Or2GoLogger() {
        if(LoggerLoader.instance != null){
            Log.e(personalTAG,"Error: Logger already instantiated");
            throw new IllegalStateException("Already Instantiated");
        }else{
            this.sb = new StringBuilder(255);
        }
    }
    //methods
    public static Or2GoLogger getLogger(){
        return LoggerLoader.instance;
    }

    private String getTag(LOGGER_DEPTH depth){
        try{
            String className = Thread.currentThread().getStackTrace()[depth.getValue()].getClassName();
            sb.append(className.substring(className.lastIndexOf(".")+1));
            sb.append("[");
            sb.append(Thread.currentThread().getStackTrace()[depth.getValue()].getMethodName());
            sb.append("] - ");
            sb.append(Thread.currentThread().getStackTrace()[depth.getValue()].getLineNumber());
            return sb.toString();
        }catch (Exception ex){
            ex.printStackTrace();
            Log.d(personalTAG, ex.getMessage());
        }finally{
            sb.setLength(0);
        }
        return null;
    }

    public void d(String msg) {
        try {
            Log.d(getTag(LOGGER_DEPTH.ACTUAL_METHOD), msg);
        } catch (Exception exception) {
            Log.e(getTag(LOGGER_DEPTH.ACTUAL_METHOD), "Logger failed, exception: " + exception.getMessage());
        }
    }

    //inner class
    /**
     * Logger Loader Class
     * The Perfect Singleton Pattern as Joshua Bosch Explained at his Effective Java Reloaded talk at Google I/O 2008
     */
    private static class LoggerLoader {
        private static final Or2GoLogger instance = new Or2GoLogger();
    }
}
