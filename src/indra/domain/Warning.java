package indra.domain;

import java.io.Serializable;

public class Warning implements Serializable{
	
	private String warnPort;
	private String warningMsg;
	private int flag;
	
	public String getWarnPort() {
		return warnPort;
	}
	public void setWarnPort(String warnPort) {
		this.warnPort = warnPort;
	}
	public String getWarningMsg() {
		return warningMsg;
	}
	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
	
	
}
