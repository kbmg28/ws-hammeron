package br.com.kbmg.wsmusiccontrol.model;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
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
public class SpaceUserAppAssociation extends AbstractEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Space space;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private UserApp userApp;

	@Column(nullable = false)
	private Boolean lastAccessedSpace;

	@Column(nullable = false)
	private Boolean active;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "spaceUserAppAssociation", fetch = FetchType.LAZY)
	private Set<EventSpaceUserAppAssociation> eventAssociationList = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "spaceUserAppAssociation", fetch = FetchType.LAZY)
	private Set<UserPermission> userPermissionList = new HashSet<>();

	public boolean isSpaceOwner(){
		return this.userPermissionList.
				stream()
				.anyMatch(up ->
						PermissionEnum.SPACE_OWNER.equals(up.getPermission())
				);
	}
}
