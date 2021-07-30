package br.com.kbmg.wsmusiccontrol.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreationTimestamp
	@EqualsAndHashCode.Exclude
	protected LocalDateTime createdDate;

	@Column
	@UpdateTimestamp
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
		createdByEmail = this.getEmailOfUserLogged();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedByEmail = this.getEmailOfUserLogged();
	}

}
