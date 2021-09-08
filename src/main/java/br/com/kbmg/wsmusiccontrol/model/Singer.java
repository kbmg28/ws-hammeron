package br.com.kbmg.wsmusiccontrol.model;

import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Singer extends AbstractEntity {

	@Column(nullable = false)
	private String name;

}
