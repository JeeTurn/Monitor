package pojo;

import java.io.Serializable;
import java.util.Set;


public class User implements Serializable{
	private int uid;
	private String uname;
	private String upwd;
	private transient Set<IpTable> ipSet;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getUpwd() {
		return upwd;
	}
	public void setUpwd(String upwd) {
		this.upwd = upwd;
	}
	public Set<IpTable> getIpSet() {
		return ipSet;
	}
	public void setIpSet(Set<IpTable> ipSet) {
		this.ipSet = ipSet;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
