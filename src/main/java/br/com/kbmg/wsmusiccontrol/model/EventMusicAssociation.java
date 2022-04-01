package br.com.kbmg.wsmusiccontrol.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EventMusicAssociation extends AbstractEntity {

	@Column(nullable = false)
	private Integer sequentialOrder;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Event event;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Music music;

}
