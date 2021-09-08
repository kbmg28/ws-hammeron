package br.com.kbmg.wsmusiccontrol.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
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
	private LocalDate date;

	@Column(nullable = false)
	private Boolean isPrincipalEventWeek;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	private Set<EventMusicAssociation> eventMusicList = new HashSet<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	private Set<EventUserAppAssociation> eventUserAppAssociationList = new HashSet<>();

}
