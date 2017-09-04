package br.com.eb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.google.gson.Gson;

public abstract class BaseController {

	protected String getJson(List<?> list) {
		Gson gson = new Gson();
		return gson.toJson(list);
	}
	
	protected List<String> getListErrors(BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			return null;
		}
		
		List<ObjectError> listErrors = bindingResult.getAllErrors();
		List<String> listMessages = new ArrayList<>(listErrors.size());
		listErrors.stream().forEach((error) -> {
			listMessages.add(error.getDefaultMessage());
		});
		
		return listMessages;
	}
}
