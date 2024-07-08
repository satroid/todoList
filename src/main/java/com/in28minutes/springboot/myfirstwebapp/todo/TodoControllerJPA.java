package com.in28minutes.springboot.myfirstwebapp.todo;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("name")
public class TodoControllerJPA {
	

	
	public TodoControllerJPA(TodoRepository todoRepository) {
		super();
		this.todoRepository = todoRepository;
	}
	
	private TodoRepository todoRepository;

	@RequestMapping("list-todos")
	public String listAllTodos(ModelMap model) {
		String userName = getLoggedInUserName(model);
		List<Todo> todos = todoRepository.findByUsername(userName);
		model.addAttribute("todos", todos);
		return "listTodos";
	}
	
	@RequestMapping(value="add-todo", method = RequestMethod.GET)
	public String showNewTodo(ModelMap model, Todo todo) {
		String userName = getLoggedInUserName(model);
		todo = new Todo(0, userName, "", todo.getTargetDate(), false);
		model.put("todo", todo);
		return "todo";
	}


	@RequestMapping(value="add-todo", method = RequestMethod.POST)
	public String addNewTodo(ModelMap model,@Valid Todo todo, BindingResult result) {
		if(result.hasErrors()) {
			return "todo";
		}
		String userName = getLoggedInUserName(model);
		todo.setUserName(userName);
		todoRepository.save(todo);
		//todoService.add(userName, todo.getDescription(), todo.getTargetDate(), todo.isDone());
		
		return "redirect:list-todos";
	}
	
	@RequestMapping("delete-todo")
	public String deleteTodo(@RequestParam int id) {
		todoRepository.deleteById(id);
		return "redirect:list-todos";
	}
	
	@RequestMapping(value="update-todo", method = RequestMethod.GET)
	public String showUpdateTodoPage(@RequestParam int id, ModelMap model) {
		Todo todo = todoRepository.findById(id).get();
		model.addAttribute(todo);
		return "todo";
	}
	
	@RequestMapping(value="update-todo", method = RequestMethod.POST)
	public String updateTodo(ModelMap model,@Valid Todo todo, BindingResult result) {
		if(result.hasErrors()) {
			return "todo";
		}
		String userName = getLoggedInUserName(model);
		todo.setUserName(userName);
		todoRepository.save(todo);
		
		return "redirect:list-todos";
	}
	private String getLoggedInUserName(ModelMap model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}
