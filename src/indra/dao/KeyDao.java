package indra.dao;

import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

import indra.db.DbConnectionMgr;
import indra.domain.Proxy;


public class KeyDao {

	
	/**
	 * 存储密钥
	 * @param proxy
	 * @return
	 */
	public static boolean insertKey(Proxy proxy) {
		boolean result = false;

		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		try {
			
			//使用了数据库中的关键字 需要用``引起来
			String sql ="insert into keygen(keyname,keylen,e,n,d,p,q) values(?,?,?,?,?,?,?)";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, proxy.getName());
			ps.setString(2, proxy.getKeylen());
			ps.setString(3, proxy.getOutput()[0]);
			ps.setString(4, proxy.getOutput()[1]);
			ps.setString(5, proxy.getOutput()[2]);
			ps.setString(6, proxy.getOutput()[3]);
			ps.setString(7, proxy.getOutput()[4]);

			
			int i = ps.executeUpdate();
			if (i > 0) {
				result = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, null);
		}

		return result;
	}
	
	/**
	 * 通过关键字 查询密钥
	 * @param kn
	 * @return
	 */
	public static Proxy findKey(String kn) {
		Proxy proxy = null;
		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql ="select * from keygen where keyname=?";
			ps = (PreparedStatement) conn.prepareStatement(sql);			
			ps.setString(1, kn);
			
			rs = (ResultSet) ps.executeQuery();
					
			if(rs.next()){
				String keyname = rs.getString("keyname");
				String keylen = rs.getString("keylen");
				String e = rs.getString("e");
				String n = rs.getString("n");
				String d = rs.getString("d");
				String p = rs.getString("p");
				String q = rs.getString("q");
				
				String[] output = new String[5];
				output[0] = e;
				output[1] = n;
				output[2] = d;
				output[3] = p;
				output[4] = q;
				proxy = new Proxy(keyname,keylen,output);
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, rs);
		}

		return proxy;
	}
	
	/**
	 * 删除数据库所有数据
	 * @return
	 */
	public static boolean deleteKey(){
		boolean result = false;

		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		try {
			
			//使用了数据库中的关键字 需要用``引起来
			String sql ="delete from keygen";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			int i = ps.executeUpdate();
			if (i > 0) {
				result = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, null);
		}

		return result;
	}

	
	/**
	 * 获取钥密长度
	 * @param keyname
	 * @return
	 */
	public static int getKeyLen(String keyname) {
		int keylen = 0;
		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String sql = "select keylen from keygen where keyname=?";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, keyname);
			
			rs = (ResultSet) ps.executeQuery();
			while(rs.next()){
				keylen = rs.getInt(1);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DbConnectionMgr.close(conn, ps, rs);
		}	
	
		return keylen;
	}
	
	/**
	 * 删除一条数据
	 * @param keyname
	 * @return
	 */
	public static boolean delOneKey(String keyname){
		boolean result = false;

		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			//使用了数据库中的关键字 需要用``引起来
			String sql ="delete from keygen where keyname=?";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			
			ps.setString(1, keyname);
			
			int i = ps.executeUpdate();
			
			if(i>0){
				result = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, null);
		}

		return result;
	}
	
	/**
	 * 添加 name和port的键值对
	 * @param name
	 * @param port
	 * @return
	 */
	public static boolean insertPort(String name,String port) {
		boolean result = false;

		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		try {
			
			//使用了数据库中的关键字 需要用``引起来
			String sql ="insert into port(name,port) values(?,?)";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, port);
			
			int i = ps.executeUpdate();
			if (i > 0) {
				result = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, null);
		}

		return result;
	}
	
	/**
	 * 根据端口号查姓名
	 * @param port
	 * @return
	 */
	public static String getNameByPort(String port){
		String name = "";
		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String sql = "select name from port where port=?";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, port);
			
			rs = (ResultSet) ps.executeQuery();
			while(rs.next()){
				name = rs.getString(1);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DbConnectionMgr.close(conn, ps, rs);
		}	
	
		return name;
	}
	
	/**
	 * 根据姓名查端口号
	 * @param name
	 * @return
	 */
	public static String getPortByName(String name){
		String port = "";
		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String sql = "select port from port where name=?";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, name);
			
			rs = (ResultSet) ps.executeQuery();
			while(rs.next()){
				port = rs.getString(1);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			DbConnectionMgr.close(conn, ps, rs);
		}	
	
		return port;
	}
	
	/**
	 * 删除数据库中端口号和姓名的键值对
	 * @param name
	 * @return
	 */
	public static boolean delByName(String name){
		boolean result = false;

		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		try {
			
			//使用了数据库中的关键字 需要用``引起来
			String sql ="delete from port where name=?";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			
			ps.setString(1, name);
			
			int i = ps.executeUpdate();
			
			if(i>0){
				result = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, null);
		}

		return result;
	}
	
	/**
	 * 判断keygen是否生成成功
	 * @return
	 */
	public static boolean isKeyGenExit() {
		boolean result = false;
		int data = 0;
		Connection conn = (Connection) DbConnectionMgr.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			//使用了数据库中的关键字 需要用``引起来
			String sql ="select count(*) from keygen";
			ps = (PreparedStatement) conn.prepareStatement(sql);
			
			rs = (ResultSet) ps.executeQuery();
			while(rs.next()){
				data = rs.getInt(1);
			}
			
			if(data == 2) {
				result = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbConnectionMgr.close(conn, ps, rs);
		}

		return result;
	}
	
	
	public static void main(String[] args) {
		System.out.println(KeyDao.isKeyGenExit());
	}
	
}
