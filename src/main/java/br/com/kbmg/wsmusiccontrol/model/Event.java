package br.com.kbmg.wsmusiccontrol.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.LocalTime;
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
	private LocalDate dateEvent;

	@Column(nullable = false)
	private LocalTime timeEvent;

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
