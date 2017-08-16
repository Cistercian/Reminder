package ru.hd.olaf.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ru.hd.olaf.util.LogUtil;

/**
 * Created by d.v.hozyashev on 16.08.2017.
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class); //логгер

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getViewIndex(Model model){
        logger.debug(LogUtil.getMethodName());

        return "index";
    }
}
