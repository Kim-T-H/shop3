package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import logic.Board;



@Repository
public class boardDao {
	private NamedParameterJdbcTemplate template;
	private RowMapper<Board> mapper= new BeanPropertyRowMapper<>(Board.class);
	private Map<String,Object> param = new HashMap<>();
	private String boardcolumn="select num, name, pass, subject, content, file1 fileurl, regdate, readcnt, grp, grplevel, grpstep"
			+ " from board";
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.template= new NamedParameterJdbcTemplate(dataSource);
	}

	public int maxnum() {
		
		return template.queryForObject("select ifnull(max(num),0) from board", param, Integer.class);
	}

	public void insert(Board board) {
		SqlParameterSource prop= new BeanPropertySqlParameterSource(board);
		String sql="insert into board (num,name,pass,subject,content,regdate,file1,readcnt,grp,grplevel,grpstep)"
				+ " values (:num,:name,:pass,:subject,:content,now(),:fileurl,0,:grp,:grplevel,:grpstep)";
				
		template.update(sql,prop);
		
	}

	public int count(String searchtype,String searchcontent) {
		String sql="select count(*) from board";
		if(searchtype != null && searchcontent !=null) {
			sql+=" where " + searchtype + " like :searchcontent";
			param.clear();
			param.put("searchcontent","%"+searchcontent+"%");
		}
		return template.queryForObject(sql, param, Integer.class);
		
	}

	public List<Board> list(Integer pageNum, int limit, String searchtype,String searchcontent) {
		String boardcolumn="select num,name,pass,subject,content,file1 fileurl,regdate,readcnt,grp,"
				+ "grplevel,grpstep from board ";
		param.clear();
		String sql=boardcolumn;
		if(searchtype != null && searchcontent !=null) {
			sql+=" where " + searchtype + " like :searchcontent";
				param.put("searchcontent","%"+searchcontent+"%");
		}
		sql+=" order by grp desc, grpstep limit :startrow, :limit";
		param.put("startrow",(pageNum-1)*limit);
		param.put("limit",limit);
		return template.query(sql, param,mapper);
	}

	public void readcntadd(Integer num) {
		param.clear();
		param.put("num", num);
		String sql="update board set readcnt=readcnt+1"
				+" where num=:num";
		template.update(sql,param);
		
	}

	public Board selectOne(Integer num) {
		String sql=boardcolumn +" where num= :num";
		param.clear();
		param.put("num",num);
		return template.queryForObject(sql, param, mapper);
	}

	public void updateGrpstep(Board board) {
		String sql="update board set grpstep=grpstep+1"
				+" where grp=:grp and grpstep > :grpstep";
		param.clear();
		param.put("grp", board.getGrp());
		param.put("grpstep", board.getGrpstep());
		template.update(sql,param);
		
	}

	public void boardupdate(Board board) {
		SqlParameterSource prop = new BeanPropertySqlParameterSource(board);
		String sql="update board set name=:name, subject=:subject, content=:content,"
				+ "file1=:fileurl where num=:num";
		template.update(sql,prop);
				
		
	}

	public void delete(int num) {
		String sql="delete from board where num=:num";
		param.clear();
		param.put("num",num);
		template.update(sql, param);
		
		
	}

//	public Board detail(Integer num) {
//		param.clear();
//		param.put("num", num);
//		String sql="select num, name, pass, subject, content, file1 fileurl, regdate, readcnt, grp, grplevel, grpstep"
//				+ " from board where num = :num"; 
//		return template.queryForObject(sql, param, mapper);
//	}
//
//	public void updatereadCnt(Integer num) {
//		param.clear();
//		param.put("num", num);
//		template.update("update board set readcnt = readcnt+1 where num = :num", param);
//	}
	
	
}
