package br.com.kbmg.wshammeron.model;

import br.com.kbmg.wshammeron.enums.MusicStatusEnum;
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Singer singer;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "music", fetch = FetchType.LAZY)
	private Set<MusicLink> musicLinkList = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Space space;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToMany(mappedBy = "music", fetch = FetchType.LAZY)
	private Set<EventMusicAssociation> eventMusicList = new HashSet<>();

}
