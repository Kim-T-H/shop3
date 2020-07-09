package aop;


import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import logic.User;
import exception.LoginException;

@Component	//객체화
@Aspect	//AOP 실행 클래스
@Order(1)	// AOp 실행 순서
public class UserLoginAspect {
	//pointcut : controller 패키지의 User 이름으로 시작하는 클래스.
	//			메서드의 이름이 logincheck 로 시작,  매개 변수는 상관없음.
	//args(..,session)  :  매개변수의 목록중 마지막 매개변수가 session 인 메서드
	
	@Around	//기본메서드 실행 전, 후
	("execution(* controller.User*.loginCheck*(..)) && args(..,session)")
	public Object userLoginCheck(ProceedingJoinPoint joinPoint, HttpSession session) throws Throwable{
		User loginUser= (User)session.getAttribute("loginUser");
		if(loginUser==null) {
			throw new LoginException("[userlogin]로그인 후 거래하세요.","login.shop");  
		}
		return joinPoint.proceed();
	}
	
	/*
	 * AOP설정하기
	 *		1.UserController의 check 로 시작하는 메서드에 매개변수가 id,session 인 경우
	 *			-로그인 안된경우: 로그인하세요=> login.shop 페이지 이동
	 *			-admin이 아니면서, 다른 아이디 정보 조회시 . 본인정보만 조회가능 => main.shop 페이지 이동.
	 */
	
	@Around
	("execution(* controller.User*.check*(..)) && args(id,session)")
	public Object myPageCheck(ProceedingJoinPoint joinPoint, String id, HttpSession session) throws Throwable{
		User loginUser= (User)session.getAttribute("loginUser");
		System.out.println("id: " + id );
		System.out.println(loginUser);
		if(loginUser==null) {
			throw new LoginException("[userlogin]로그인 후 거래하세요.","login.shop");  
		}else if(!loginUser.getUserid().equals("admin") && !loginUser.getUserid().equals(id))
			throw new LoginException("[userlogin] 해당 권한이 없습니다.","main.shop");
		return joinPoint.proceed();
	}
	
	/*
	 * 회원 탈퇴
	 * 1.비밀번호 검증 불일치: "비밀번호 오류" 메세지 출력. delete.shop 이동
	 * 2.비밀번호 검증 일치:	회원 db에서  delete
	 * 				본인인 경우 : logout 하고 login.shop 페이지 요청
	 * 				관리자인 경우: main.shop으로 페이지 이동
	 */
	
}
