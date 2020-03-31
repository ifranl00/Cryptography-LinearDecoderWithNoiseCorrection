package p5;

public class Sindrome {

		private String error;
		private String sindrome;
		
	public Sindrome(String error,String sindrome) {
		this.error = error;
		this.sindrome = sindrome;
	}
	
	public Sindrome() {
		this.error = "";
		this.sindrome = "";
	}
	
	public String getError() {
		return this.error;
	}
	
	public String getSindrome() {
		return this.sindrome;
	}
	
	public void setError(String err) {
		this.error = err;
	}
	
	public void setSindrome(String s) {
		this.sindrome = s;
	}
}
