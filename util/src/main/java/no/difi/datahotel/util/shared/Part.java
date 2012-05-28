package no.difi.datahotel.util.shared;

public enum Part {

	MASTER("master"),
	SLAVE("slave");
	
	private String folder;
	
	private Part(String folder) {
		this.folder = folder;
	}
	
	public String toString() {
		return folder;
	}
}