package br.com.kbmg.wshammeron.model;

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
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Event extends AbstractEntity {

	@Column(nullable = false)
	private OffsetDateTime dateTimeEvent;

	@Column(nullable = false)
	private String timeZoneName;

	@Column(nullable = false)
	private String name;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	private Set<EventMusicAssociation> eventMusicList = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	private Set<EventSpaceUserAppAssociation> spaceUserAppAssociationList = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Space space;

}
