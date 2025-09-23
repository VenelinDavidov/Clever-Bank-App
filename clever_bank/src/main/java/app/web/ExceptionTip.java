package app.web;

import app.exception.CustomerAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ExceptionTip {

    @ExceptionHandler(CustomerAlreadyExistException.class)
    public String handleExceptionCustomerAlreadyExist(RedirectAttributes redirectAttributes,
                                                      CustomerAlreadyExistException exception) {


        String message = exception.getMessage ();
        redirectAttributes.addFlashAttribute ("errorMessage", message);

        return "redirect:/register";
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
            {
                    AccessDeniedException.class,
                    NoResourceFoundException.class,
                    MethodArgumentTypeMismatchException.class,
                    MissingRequestValueException.class
            })
    public ModelAndView handleNotFoundException() {

        return new ModelAndView ("not-found");
    }




    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.setViewName ("internal-server-error");
        modelAndView.addObject ("errorMessage", exception.getClass().getSimpleName());
        return modelAndView;
    }
}
