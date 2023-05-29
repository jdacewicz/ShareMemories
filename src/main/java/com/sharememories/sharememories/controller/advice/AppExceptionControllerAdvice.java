package com.sharememories.sharememories.controller.advice;

import com.sharememories.sharememories.controller.AppController;
import com.sharememories.sharememories.exception.NotMatchException;
import com.sharememories.sharememories.exception.NotUniqueException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestControllerAdvice(assignableTypes = {AppController.class})
public class AppExceptionControllerAdvice {

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("errorMessage",
                "An error occurred while saving image. Please try again later.");

        return modelAndView;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolationException() {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("errorMessage",
                "Only PNG, JPG or JPEG images are allowed.");

        return modelAndView;
    }

    @ExceptionHandler(NotUniqueException.class)
    public ModelAndView handleNotUniqueLoginException(NotUniqueException ex) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("errorMessage", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(NotMatchException.class)
    public ModelAndView handleNotMatchException(NotMatchException ex) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("errorMessage", ex.getMessage());

        return modelAndView;
    }
}
