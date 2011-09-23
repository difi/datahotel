package no.difi.datahotel.logic.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


@NamedQueries({
	@NamedQuery(name=UserEntity.BY_IDENT, query="SELECT u FROM User u WHERE u.ident = :ident"),
	@NamedQuery(name=UserEntity.BY_OWNER, query = "SELECT u FROM User u WHERE u.owner = :owner ORDER BY u.name")
})

@Entity(name="User")
public class UserEntity implements JPAEntity{

	/**NamedQuery that returns the {@code UserEntity}<p>
	 * identified by the provided {@code ident}.
	 * Parameters:
	 * <ol>
	 * <li> ident, the ident</li>
	 * </ol>*/
	public static final String BY_IDENT = "USER_BY_IDENT";

	public static final String BY_OWNER = "USER_BY_OWNER";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="name", nullable=false)
	@Basic
	private String name;

	@ManyToOne(cascade={MERGE, REFRESH})
	@JoinColumn(name="owner", nullable=false)
	private OwnerEntity owner;

	@Column(unique = true)
	private String ident;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwner(OwnerEntity owner) {
		this.owner = owner;
	}

	public OwnerEntity getOwner() {
		return owner;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof UserEntity))return false;
		UserEntity u = (UserEntity) o;
		return this.name.equals(u.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
