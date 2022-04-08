package br.com.kbmg.wshammeron.model;

import br.com.kbmg.wshammeron.enums.MusicTypeLinkEnum;
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

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MusicLink extends AbstractEntity {

	@Column(nullable = false)
	private String link;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MusicTypeLinkEnum typeLink;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	@ToString.Exclude
	private Music music;

}
