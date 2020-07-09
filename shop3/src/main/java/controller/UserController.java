package controller;

import java.util.List;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import dao.UserDao;
import exception.LoginException;
import logic.Item;
import logic.Sale;
import logic.SaleItem;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private ShopService service;

	@Autowired
	private UserDao userDao;
	
	

	@GetMapping("*")
	public String form(Model model) {
		model.addAttribute(new User());
		return null;
	}

	@PostMapping("userEntry")
	public ModelAndView join(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		try {
			service.userInsert(user);
			mav.setViewName("redirect:login.shop");
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bresult.reject("error.duplicate.user");
			mav.getModel().putAll(bresult.getModel());
		}
		return mav;
	}

	@PostMapping("login")
	public ModelAndView login(@Valid User user, BindingResult bresult, HttpSession session) {
		System.out.println("================================================================");
		ModelAndView mav = new ModelAndView();
		System.out.println(bresult);
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		// 1.db 정보의 id, password 비교
		// 2.일치: session에 loginUser 정보 저장
		// 3. 불일치: 비밀번호 확인 내용 출력.
		// 4.db에 해당 id 정보가 없는 경우
		try {
			User userinfo=service.getUser(user.getUserid());
			if(user.getPassword().contentEquals(userinfo.getPassword())) {
				session.setAttribute("loginUser", userinfo);
				mav.setViewName("redirect:main.shop");
			}else bresult.reject("error.login.password");
		}catch(Exception e) {
			e.printStackTrace();
			bresult.reject("error.login.id");
		}
		return mav;
	}
	
	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login.shop";
	}
	
	@RequestMapping("main")	//login   되어야 실행가능. 메서드이름을 loginxxx 로 지정
	public String loginCheck(HttpSession session) {
		return null;
	}
	
	
	@RequestMapping("mypage")
	public ModelAndView checkmypage(String id, HttpSession session) {
		ModelAndView mav= new ModelAndView();
		User user = service.getUser(id);
		//sale 테이블에서 saleid,userid,saledate 컬럼값만 저장된 Sale 객체의 List 로 리턴
		List<Sale> salelist = service.salelist(id); 
		int sum = 0;
		for(Sale sa: salelist) {
			List<SaleItem> saleitemlist = service.saleItemList(sa.getSaleid());
			for(SaleItem si : saleitemlist) {
				Item item= service.getItem(Integer.parseInt(si.getItemid()));
				si.setItem(item);
				sum += (si.getQuantity() * si.getItem().getPrice());
			}
			sa.setItemList(saleitemlist);
			sa.setTotal(sum);
		}
		mav.addObject("user",user);
		mav.addObject("salelist",salelist);
		return mav;
	}
	
	@GetMapping(value={"update","delete"})
	public ModelAndView checkview(String id,HttpSession session) {
		ModelAndView mav=new ModelAndView();
		User user = service.getUser(id);
		mav.addObject("user",user);
		return mav;
	}
	/*
	 * 1.유효성검증
	 * 2.비밀번호 검증: 불일치
	 * 		유효성 출력으로 error.login.password 코드로 실행
	 * 3.비밀번호 일치
	 * 		update 실행.
	 * 		로그인정보 수정. 단 admin이 다른사람의 정보 수정시는 로그인 정보 수정안됨.
	 */
	@PostMapping("update")
	public ModelAndView update(@Valid User user, BindingResult bresult, HttpSession session) {
		System.out.println("================================================================");
		ModelAndView mav = new ModelAndView();
		System.out.println(bresult);
		//유효성 검증
		if (bresult.hasErrors()) {
			bresult.reject("error.input.user");
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		// 1.db 정보의 id, password 비교
		// 2.일치: session에 loginUser 정보 저장
		// 3. 불일치: 비밀번호 확인 내용 출력.
		// 4.db에 해당 id 정보가 없는 경우
		
		
		//비밀번호 검증
		User loginUser  = (User)session.getAttribute("loginUser");
		//로그인한 정보의 비밀번호와 입력된 비밀번호 검증
		if(!user.getPassword().contentEquals(loginUser.getPassword())) {
			bresult.reject("error.login.password");
			return mav;
		}
		//비밀번호 일치: 수정이 가능
		try {
			service.userUpdate(user);
			mav.setViewName("redirect:mypage.shop?id="+user.getUserid());
			if(loginUser.getUserid().equals(user.getUserid())) {
				session.setAttribute("loginUser", user);
			}
		}catch(Exception e) {
			e.printStackTrace();
			bresult.reject("error.user.update");
		}
		return mav;
	}
	
	/*
	 * 회원 탈퇴
	 * 1.비밀번호 검증 불일치: "비밀번호 오류" 메세지 출력. delete.shop 이동
	 * 2.비밀번호 검증 일치:	회원 db에서  delete
	 * 				본인인 경우 : logout 하고 login.shop 페이지 요청
	 * 				관리자인 경우: main.shop으로 페이지 이동
	 */
	
	@PostMapping("delete")
	public ModelAndView delete(String userid,String password ,HttpSession session) {
		ModelAndView mav= new ModelAndView();
		User loginUser  = (User)session.getAttribute("loginUser");
		if(userid.equals("admin")) {
			throw new LoginException("관리자 탈퇴는 불가능합니다.","main.shop?id="+userid);
		}
		//관리자 로그인 : 관리자 비밀번호 검증
		//사용자 로그인: 본인비밀번호 검증
		
		if(!password.equals(loginUser.getPassword())) {
			throw new LoginException("회원탈퇴시 비밀번호가 틀립니다.","delete.shop?id="+userid);
		}
		
		System.out.println("===============================");
		System.out.println("userid: "+ userid);
		try {
			service.userdelete(userid);		//type만 확인함. 이름은 확인하지않음
		}catch(Exception e) {
			e.printStackTrace();
			throw new LoginException("회원 탈퇴시 오류가 발생했습니다. 전산부에 연락바랍니다.","delete.shop?id="+userid);
		}
		
		if(loginUser.getUserid().equals("admin")) {
			mav.setViewName("redirect:main.shop");
		}else {
		//session.invalidate();  //로그아웃
		throw new LoginException(userid+"회원님 탈퇴 되었습니다.","logout.shop");
		}
		return mav;
}
}
