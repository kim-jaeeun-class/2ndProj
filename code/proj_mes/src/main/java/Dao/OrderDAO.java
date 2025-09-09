package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import Dto.OrderDTO;
import emp.dto.EmpDTO;



public class OrderDAO {

	// DB 접속 메소드
	private Connection getConn() {
		Connection conn = null;
		
		try {
			
			Context ctx = new InitialContext();
			
			DataSource dataFactory = (DataSource)ctx.lookup("java:/comp/env/jdbc/oracle"); 
			
			// DB 접속
			conn = dataFactory.getConnection();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}


	// select
	public List selectAll() {
		
		List list = new ArrayList();
		
		try {
			
			// DB 접속
			Connection con = getConn();
			
			// SQL 준비
			String query = " select * from orders";
			PreparedStatement ps = con.prepareStatement(query);
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			while(rs.next()) {
				OrderDTO dto = new OrderDTO();
				
				dto.setOrder_key(rs.getString("order_key"));
				dto.setOrder_number(rs.getString("order_number"));
				dto.setOrder_date(rs.getDate("order_date"));
				dto.setOrder_pay(rs.getDate("order_pay"));
				dto.setOrder_state(rs.getInt("order_state"));
				dto.setClient_id(rs.getString("client_id"));
				dto.setWorker_id(rs.getString("worker_id"));
				dto.setDapart_ID2(rs.getString("dapart_ID2"));
				
				list.add(dto);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	// 상세페이지 하나만 조회
	public OrderDTO selectOneOrder(OrderDTO orderDTO) {
		
		OrderDTO resultDTO = null;
		
		try {
				
			// DB 접속
			Connection conn = getConn();
			
			// SQL 준비
			String query = " select * from orders";
				   query += " where order_number = ?";
				   
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, orderDTO.getOrder_number());
			
			// SQL 실행
			ResultSet rs = ps.executeQuery();
			
			// 결과 활용
			if(rs.next()) {

				// resultDTO가 null로 되어있어서 new 해줘야함
				resultDTO = new OrderDTO();
				
				resultDTO.setOrder_key(rs.getString("order_key"));
				resultDTO.setOrder_number(rs.getString("order_number"));
				resultDTO.setOrder_date(rs.getDate("order_date"));
				resultDTO.setOrder_pay(rs.getDate("order_pay"));
				resultDTO.setOrder_state(rs.getInt("order_state"));
				resultDTO.setClient_id(rs.getString("client_id"));
				resultDTO.setWorker_id(rs.getString("worker_id"));
				resultDTO.setDapart_ID2(rs.getString("dapart_ID2"));
			
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return resultDTO;
		
	}
	
	// delete
		public int deleteEmp(OrderDTO orderDTO) {
			
			int result = -1;
			
			try {
				
				// DB 접속
				Connection conn = getConn();
				
				// SQL 준비
				String query = " delete orders";
					   query += " where order_number = ?";
			   
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, orderDTO.getOrder_number());
				
				// SQL 실행
				result = ps.executeUpdate();
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		// insert
		public int insertEmp(OrderDTO orderDTO) {
			
			int result = -1;
			
			try {
				
				// DB 접속
				Connection conn = getConn();
				
				// SQL 준비
				String query = " insert into emp3 (empno,ename, job, mgr, hiredate, sal, comm, deptno)";
					   query += " values(?, ?, ?, ?, ?, ?, ?, ?)";
				
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setInt(1, empDTO.getEmpno());
				ps.setString(2, empDTO.getEname());
				ps.setString(3, empDTO.getJob());
				ps.setInt(4, empDTO.getMgr());
				ps.setDate(5, empDTO.getHiredate());
				ps.setInt(6, empDTO.getSal());
				ps.setInt(7, empDTO.getComm());
				ps.setInt(8, empDTO.getDeptno());
				
				// SQL 실행
				result = ps.executeUpdate();
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		// update
		public int updateEmp(OrderDTO orderDTO) {
			
			int result = -1;
			
			try {
				
				// DB 접속
				Connection conn = getConn();
				
				// SQL 준비
				String query = " update emp3";
				query += " set ename = ?, ";
				query += "     job = ?, ";
				query += "     mgr = ?, ";
				query += "     hiredate = ?, ";
				query += "     sal = ?, ";
				query += "     comm = ?, ";
				query += "     deptno = ?";
				query += " where empno = ?";
				
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, empDTO.getEname());
				ps.setString(2, empDTO.getJob());
				ps.setInt(3, empDTO.getMgr());
				ps.setDate(4, empDTO.getHiredate());
				ps.setInt(5, empDTO.getSal());
				ps.setInt(6, empDTO.getComm());
				ps.setInt(7, empDTO.getDeptno());
				ps.setInt(8, empDTO.getEmpno());
				
				// SQL 실행
				result = ps.executeUpdate();
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		
		public EmpDTO login(OrderDTO orderDTO) {
			
			EmpDTO resultDTO = null;
			
			try {
				
				// DB 접속
				Connection conn = getConn();
				
				// SQL 준비
				String query = " select ename, empno, job from emp3";
					   query += " where ename = ? and empno= ?";
					   
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, empDTO.getEname());
				ps.setInt(2, empDTO.getEmpno());
				
				// SQL 실행
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {

					// resultDTO가 null로 되어있어서 new 해줘야함
					resultDTO = new EmpDTO();
					
					int empno = rs.getInt("empno");
					resultDTO.setEmpno(empno);
					
					resultDTO.setEname(rs.getString("ename"));
					resultDTO.setJob(rs.getString("job"));
				
				}
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return resultDTO;
		}
	
}
