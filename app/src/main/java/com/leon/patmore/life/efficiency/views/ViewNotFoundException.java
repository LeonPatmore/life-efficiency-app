package com.leon.patmore.life.efficiency.views;

public class ViewNotFoundException extends Exception {

    public ViewNotFoundException(String viewName) {
        super(String.format("Could not find view with name '%s'", viewName));
    }

}
