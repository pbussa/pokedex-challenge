package org.truelayer.pokedex.configuration;

public class ServerConfiguration {

	private static ServerConfiguration instance;

	private int port;
	private int idleTimeout;
	private int timeoutRequestOnExternalCall;

	private ServerConfiguration() {
		initializeProperties();
	}

	public static synchronized ServerConfiguration getInstance() {
		if (instance == null) {
			instance = new ServerConfiguration();
		}
		return instance;
	}

	private void initializeProperties() {
		this.port = 8001;
		this.idleTimeout = 30000;
		this.timeoutRequestOnExternalCall = 30000;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public int getTimeoutRequestOnExternalCall() {
		return timeoutRequestOnExternalCall;
	}

	public void setTimeoutRequestOnExternalCall(int timeoutRequestOnExternalCall) {
		this.timeoutRequestOnExternalCall = timeoutRequestOnExternalCall;
	}
}
