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
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserApp extends AbstractEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column
	private String password;

	@Column(nullable = false)
	private String cellPhone;

	@Column(nullable = false)
	private Boolean enabled;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "userApp", fetch = FetchType.LAZY)
	private Set<UserPermission> userPermissionList = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "userApp", fetch = FetchType.LAZY)
	private Set<SpaceUserAppAssociation> spaceUserAppAssociationList = new HashSet<>();

	public boolean isSysAdmin(){
		return this.userPermissionList.
				stream()
				.anyMatch(up ->
					PermissionEnum.SYS_ADMIN.equals(up.getPermission())
				);
	}
}
