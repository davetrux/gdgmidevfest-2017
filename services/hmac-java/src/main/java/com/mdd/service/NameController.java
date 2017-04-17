package com.mdd.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author trux
 */
@Controller
@RequestMapping("/api")
public class NameController {

    private NameGenerator _generator = new NameGenerator();

    @RequestMapping(value = "/names/{count}", method = RequestMethod.GET, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public List<Person> getPersonList(@PathVariable("count") int count, HttpServletResponse response) {

        if (!countValid(count)) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        return _generator.getRandomPersons(count);
    }

    @RequestMapping(value = "/name/{gender}", method = RequestMethod.GET, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public Person getSingleNameByGender(@PathVariable("gender") String gender, HttpServletResponse response) {

        if (gender != null && !gender.isEmpty() && genderValid(gender)) {
            Person result = _generator.getRandomName(gender);

            return result;
        } else {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    private boolean genderValid(String gender) {

        if (gender.length() != 1) return false;
        if (gender.equalsIgnoreCase("m")) return true;
        if (gender.equalsIgnoreCase("f")) return true;
        return false;
    }

    @RequestMapping(value = "/name", method = RequestMethod.GET, headers="Accept=*/*", produces = {"application/json"})
    @ResponseBody
    public Person getSingleName() {

        Person result = _generator.getRandomName();

        return result;
    }

    private boolean countValid(int count) {
        return count > 0 && count < 101;
    }
}
