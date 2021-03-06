package br.com.kbmg.wshammeron.model;

import br.com.kbmg.wshammeron.constants.AppConstants;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Space extends AbstractEntity {

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String justification;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SpaceStatusEnum spaceStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private UserApp requestedBy;

	@Column(nullable = false)
	private LocalDateTime requestedByDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	@ToString.Exclude
	private UserApp approvedBy;

	@Column
	private LocalDateTime approvedByDate;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
	private Set<Event> eventList = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
	private Set<Music> musicList = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "space", fetch = FetchType.LAZY)
	private Set<SpaceUserAppAssociation> spaceUserAppAssociationList = new HashSet<>();

	public boolean isApproved() {
		return this.approvedBy != null || name.equals(AppConstants.DEFAULT_SPACE);
	}
}
