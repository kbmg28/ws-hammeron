package br.com.kbmg.wsmusiccontrol.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@Data
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(255)")
	private String id;

	@EqualsAndHashCode.Exclude
	protected LocalDateTime createdDate;

	@Column
	@EqualsAndHashCode.Exclude
	protected LocalDateTime updatedDate;

	@EqualsAndHashCode.Exclude
	protected String createdByEmail;

	@EqualsAndHashCode.Exclude
	protected String updatedByEmail;

	protected String getEmailOfUserLogged() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (authentication == null) ? null : authentication.getName();
	}

	@PrePersist
	protected void onSave() {
		this.createdDate = LocalDateTime.now();
		createdByEmail = this.getEmailOfUserLogged();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedDate = LocalDateTime.now();
		updatedByEmail = this.getEmailOfUserLogged();
	}

}
