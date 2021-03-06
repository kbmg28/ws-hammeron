package br.com.kbmg.wshammeron.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserApp extends AbstractEntity {

	@Column
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column
	private String password;

	@Column
	private String cellPhone;

	@Column(nullable = false)
	private Boolean enabled;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "userApp", fetch = FetchType.LAZY)
	private Set<SpaceUserAppAssociation> spaceUserAppAssociationList = new HashSet<>();

	@Column(nullable = false)
	private Boolean isSysAdmin;

	@Column
	private LocalDateTime passwordExpireDate;

	public boolean isExpiredPassword() {
		LocalDateTime now = LocalDateTime.now();
		return now.isAfter(passwordExpireDate);
	}
}
