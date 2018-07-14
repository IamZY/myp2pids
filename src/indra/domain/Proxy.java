package indra.domain;

public class Proxy {

	private String name;
	private String keylen;
	private String[] output;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKeylen() {
		return keylen;
	}
	public void setKeylen(String keylen) {
		this.keylen = keylen;
	}
	public String[] getOutput() {
		return output;
	}
	public void setOutput(String[] output) {
		this.output = output;
	}
	public Proxy(String name, String keylen, String[] output) {
		super();
		this.name = name;
		this.keylen = keylen;
		this.output = output;
	}
	public Proxy() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
