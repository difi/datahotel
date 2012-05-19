package no.difi.datahotel.logic.slave;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;

import no.difi.datahotel.util.bridge.Metadata;

@Singleton
public class SystemEJB {

	private boolean ready = false;
	private Map<String, Metadata> directory = new HashMap<String, Metadata>();

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public Map<String, Metadata> getDirectory() {
		return directory;
	}

	public void setDirectory(Map<String, Metadata> directory) {
		this.directory = directory;
	}
}
