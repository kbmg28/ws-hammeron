package br.com.kbmg.wsmusiccontrol.model;

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
public class EventSpaceAssociation extends AbstractEntity {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Event event;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Space space;

}
