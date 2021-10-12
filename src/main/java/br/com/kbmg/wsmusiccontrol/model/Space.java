package br.com.kbmg.wsmusiccontrol.model;

import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Space extends AbstractEntity {

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String justification;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	@ToString.Exclude
	private UserApp requestedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	@ToString.Exclude
	private UserApp approvedBy;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
	private Set<EventSpaceAssociation> eventSpaceAssociationList = new HashSet<>();

	public boolean isApproved() {
		return this.approvedBy != null || name.equals(KeyMessageConstants.PUBLIC_SPACE);
	}
}
