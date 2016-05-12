package pojo;

public class IpTable {
	private int ipid;
	private String innerIp;
	private int innerPort;
	private String outerIp;
	private int outerPort;
	private transient ServerTable serverTable;
	
	public int getIpid() {
		return ipid;
	}

	public void setIpid(int ipid) {
		this.ipid = ipid;
	}

	public String getInnerIp() {
		return innerIp;
	}

	public void setInnerIp(String innerIp) {
		this.innerIp = innerIp;
	}

	public int getInnerPort() {
		return innerPort;
	}

	public void setInnerPort(int innerPort) {
		this.innerPort = innerPort;
	}

	public String getOuterIp() {
		return outerIp;
	}

	public void setOuterIp(String outerIp) {
		this.outerIp = outerIp;
	}

	public int getOuterPort() {
		return outerPort;
	}

	public void setOuterPort(int outerPort) {
		this.outerPort = outerPort;
	}

	public ServerTable getServerTable() {
		return serverTable;
	}

	public void setServerTable(ServerTable serverTable) {
		this.serverTable = serverTable;
	}

	public IpTable() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
