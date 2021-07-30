package br.com.kbmg.wsmusiccontrol.model;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserPermission extends AbstractEntity {

	private PermissionEnum permission;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private User user;

}
