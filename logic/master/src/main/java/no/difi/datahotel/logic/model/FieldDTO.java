package no.difi.datahotel.logic.model;

public class FieldDTO {

	private String example;
	private String field;

	private String fieldShort;
	private String description;
	private boolean searchable;
	private boolean groupable;
	private boolean primaryKey;
	private int id;
	private long definitionId;

	public FieldDTO() {
	}

	public FieldDTO(String field, String fieldShort, String description,
			boolean searchable, boolean groupable, long definitionId) {
		this.field = field;
		this.fieldShort = fieldShort;
		this.description = description;
		this.searchable = searchable;
		this.groupable = groupable;
		this.definitionId = definitionId;
	}

	public FieldDTO(String example, String field, int id) {
		this.example = example;
		this.field = field;
		this.id = id;
	}

	public FieldDTO(String example, String header) {
		this.example = example;
		this.field = header;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFieldShort() {
		return fieldShort;
	}

	public void setFieldShort(String fieldShort) {
		this.fieldShort = fieldShort.toLowerCase();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public boolean isGroupable() {
		return groupable;
	}

	public void setGroupable(boolean groupable) {
		this.groupable = groupable;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(long definitionId) {
		this.definitionId = definitionId;
	}

}
