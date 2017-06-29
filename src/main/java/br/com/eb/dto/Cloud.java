package br.com.eb.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.annotations.ApiModelProperty;

@Document(collection = "cloud")
public class Cloud {

	@Id
	@ApiModelProperty(hidden = true)
	private String id;
	
	@DBRef
	@ApiModelProperty(hidden = true)
	private Person person;
	
	@NotNull(message = "Informe o nome.")
	@ApiModelProperty(example = "Cloud1", required = true)
	private String name;
	
	@NotNull(message = "Informe o sistema operacional.")
	@ApiModelProperty(example = "Windows", required = true)
	private String operationalSystem;
	
	@NotNull(message = "Informe o status.")
	@ApiModelProperty(example = "true", required = true)
	private boolean active;
	
	@ApiModelProperty(example = "26/06/2017 22:12:13")
	@CreatedDate
	private Date createDate;
	
	@ApiModelProperty(example = "27/06/2017 10:08:40")
	@LastModifiedDate
	private Date updateDate;
	
	@NotNull(message = "Informe a capacidade em GB.")
	@ApiModelProperty(example = "30", required = true)
	private Long capacity;
	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperationalSystem() {
		return this.operationalSystem;
	}

	public void setOperationalSystem(String operationalSystem) {
		this.operationalSystem = operationalSystem;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getCapacity() {
		return this.capacity;
	}

	public void setCapacity(Long capacity) {
		this.capacity = capacity;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Person getPerson() {
		return this.person;
	}
}