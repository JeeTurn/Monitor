package pojo;

import java.io.Serializable;
import java.util.Set;

public class ServerTable implements Serializable{
	private int sid;
	private String sInnerIp;
	private int sInnerPort;
	private String sOuterIp;
	private int sOuterPort;
	private String sName;
	public String getsName() {
		return sName;
	}
	public void setsName(String sName) {
		this.sName = sName;
	}
	private transient User user;
	private transient Set<IpTable> ipSet;
	public Set<IpTable> getIpSet() {
		return ipSet;
	}
	public void setIpSet(Set<IpTable> ipSet) {
		this.ipSet = ipSet;
	}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public String getsInnerIp() {
		return sInnerIp;
	}
	public void setsInnerIp(String sInnerIp) {
		this.sInnerIp = sInnerIp;
	}
	public int getsInnerPort() {
		return sInnerPort;
	}
	public void setsInnerPort(int sInnerPort) {
		this.sInnerPort = sInnerPort;
	}
	public String getsOuterIp() {
		return sOuterIp;
	}
	public void setsOuterIp(String sOuterIp) {
		this.sOuterIp = sOuterIp;
	}
	public int getsOuterPort() {
		return sOuterPort;
	}
	public void setsOuterPort(int sOuterPort) {
		this.sOuterPort = sOuterPort;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
