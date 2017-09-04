package br.com.eb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class IndexController {

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("redirect:swagger-ui.html");
	}
}