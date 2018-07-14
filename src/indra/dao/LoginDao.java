package indra.dao;

import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

import indra.db.DbConnectionMgr;
import indra.domain.User;

public class LoginDao {

	// —È÷§
	public boolean verify(User user) {
		boolean b = false;
		
		Connection conn = (Connection) DbConnectionMgr.getConnection();
	    String sql = "select * from User where username=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String pwd = null;
		try {
			ps = (PreparedStatement) conn.prepareStatement(sql);
			
			ps.setString(1, user.getUsername());
			rs = (ResultSet) ps.executeQuery();
			if(rs.next()){		
				pwd = rs.getString("pwd");
				if(pwd.equals(user.getPwd())){
					b = true;
				}		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DbConnectionMgr.close(conn, ps,rs);
		}
		
		return b;
	} // verify


	
} // Login
