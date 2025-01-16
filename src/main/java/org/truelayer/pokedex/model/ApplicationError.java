package org.truelayer.pokedex.model;


public class ApplicationError {

	private String message;
	private int status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Serialize error to json
	 * 
	 * @return
	 */
	public String toJson() {
		return "{\"message\":\"" + this.message + "\",\"status\":" + this.status + "}";
	}

}
