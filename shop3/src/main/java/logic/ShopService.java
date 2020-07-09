package logic;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import dao.ItemDao;
import dao.UserDao;
import dao.SaleDao;
import dao.SaleItemDao;


@Service // @Component +service(Controller 와 dao 중간 역할)
public class ShopService {

	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SaleDao saleDao;
	
	@Autowired
	private SaleItemDao saleItemDao;

	public List<Item> getItemList() {
		return itemDao.list();
	}

	public Item getItem(Integer id) {

		return itemDao.selectOne(id);
	}

	// 파일 업로드, dao에 데이터 전달
	public void itemCreate(Item item, HttpServletRequest request) {
		if (item.getPicture() != null && !item.getPicture().isEmpty()) {

			uploadFileCreate(item.getPicture(), request, "img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}

		itemDao.insert(item);
	}

	private void uploadFileCreate(MultipartFile picture, HttpServletRequest request, String path) { // picture : 파일의 내용
																									// 저장
		String orgFile = picture.getOriginalFilename();
		String uploadPath = request.getServletContext().getRealPath("/") + path;
		File fpath = new File(uploadPath);
		if (!fpath.exists())
			fpath.mkdirs();
		try {
			// 파일의 내용을 실제 파일로 저장
			picture.transferTo(new File(uploadPath + orgFile));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void itemUpdate(Item item, HttpServletRequest request) {
		if (item.getPicture() != null && !item.getPicture().isEmpty()) {

			uploadFileCreate(item.getPicture(), request, "img/");
			item.setPictureUrl(item.getPicture().getOriginalFilename());
		}

		itemDao.update(item);

	}

	public void itemdelete(String id) {
		itemDao.delete(id);
		
	}

	public void userInsert(User user) {
		userDao.insert(user);   
		
	}

	public User getUser(String userid) {
		
		return userDao.selectOne(userid);
	}

	
	/*
	 * db에 sale 정보와 saleitem 정보 저장. 저장된 내용을  Sale 객체로 리턴
	 * 1. sale 테이블의 saleid 값을 설정 => 최대값+1 
	 * 2. sale의 내용 설정. => insert
	 * 3. Cart 정보로부터 SaleItem 내용 설정 =>insert 
	 * 4. 모든 정보를  Sale 객체로 저장
	 */
	
	public Sale checkend(User loginUser, Cart cart) {
		Sale sale= new Sale();
		int maxno=saleDao.getMaxSaleid();  
		sale.setSaleid(++maxno);
		sale.setUser(loginUser);
		sale.setUserid(loginUser.getUserid());
		saleDao.insert(sale);
		List<ItemSet> itemList=cart.getItemSetList(); //Cart 상품정보
		int i=0;
		for(ItemSet is : itemList) {
			int seq=++i;
			SaleItem saleItem= new SaleItem(sale.getSaleid(),seq,is);
			sale.getItemList().add(saleItem);
			saleItemDao.insert(saleItem); 
		}
		return sale;
	}

	public List<Sale> salelist(String id) {
		
		return saleDao.list(id); //사용자 id
	}

	public List<SaleItem> saleItemList(int saleid) {
		
		return saleItemDao.list(saleid); //saleid 주문번호
	}

	public void userUpdate(User user) {
		userDao.update(user);
		
	}

	public void userdelete(String userid) {
		userDao.delete(userid);
		
	}

	public List<User> getlistAll() {
		return userDao.getlistAll();
	}

	
	
	
	


}