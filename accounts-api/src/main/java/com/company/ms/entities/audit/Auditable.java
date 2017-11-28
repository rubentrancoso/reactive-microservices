package com.company.ms.entities.audit;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {
	
    @CreatedDate
    @Temporal(TIMESTAMP)
    @JsonFormat(pattern="EEE yyyy-MM-dd HH:mm:ss.SSSZ")
    protected Date created;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @JsonFormat(pattern="EEE yyyy-MM-dd HH:mm:ss.SSSZ")
    protected Date modified;
    
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}    

}
