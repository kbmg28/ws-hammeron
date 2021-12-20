package br.com.kbmg.wsmusiccontrol.model;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserPermission extends AbstractEntity {

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PermissionEnum permission;

	@ManyToOne
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private SpaceUserAppAssociation spaceUserAppAssociation;

}
