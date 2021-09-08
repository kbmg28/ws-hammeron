package br.com.kbmg.wsmusiccontrol.model;

import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import lombok.*;
import org.springframework.boot.actuate.endpoint.web.Link;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Music extends AbstractEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MusicStatusEnum musicStatus;

	@Column(nullable = false)
	private String youTubeLink;

	@Column(nullable = false)
	private String cifraClubLink;

	@Column(nullable = false)
	private String spotifyLink;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Singer singer;

}
