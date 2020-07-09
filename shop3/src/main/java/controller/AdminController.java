package controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import logic.ShopService;
import logic.User;

@RequestMapping("admin") 
@Controller
public class AdminController {
	@Autowired
	private ShopService service;
	
	@GetMapping("list")
	public ModelAndView list(HttpSession session) {
		ModelAndView mav= new ModelAndView();
		List<User> list = service.getlistAll();
		System.out.println(list);
		mav.addObject("list", list); //    "list"   :     list.jsp ÀÇ  forEach items ÀÇ "${list}"
		
		return mav;
		
	}
}
