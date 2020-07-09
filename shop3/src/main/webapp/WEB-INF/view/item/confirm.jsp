<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<%-- /webapp/WEB-INF/view/item/confirm.jsp
	상품 삭제 버튼 클릭시 해당 상품을 삭제하기
 --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 삭제 전 확인</title>
</head>
<body>
<h2>상품 삭제 전 확인</h2>
	<table>
	<tr><td><img src="../img/${item.pictureUrl}"></td>		<%--수정과 다른게 form으로 item을 호출하지않기에  item객체를 써주어야함 --%>
	
	<td><table>
		<tr><td>상품명</td><td>${item.name }</td></tr>
		<tr><td>상품 가격</td><td>${item.price }원</td></tr>
		<tr><td>상품 설명</td><td>${item.description }</td></tr>
		<tr><td colspan="2"><input type="button" value="상품 삭제" onclick="location.href='delete.shop?id=${item.id}'">
		<input type="button" value="상품목록" onclick="location.href='list.shop'"></td></tr>
		</table>
	</table>

</body>
</html>