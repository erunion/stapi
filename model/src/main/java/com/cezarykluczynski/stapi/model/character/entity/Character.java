package com.cezarykluczynski.stapi.model.character.entity;

import com.cezarykluczynski.stapi.model.common.entity.Gender;
import com.cezarykluczynski.stapi.model.common.entity.MaritalStatus;
import com.cezarykluczynski.stapi.model.common.entity.PageAwareEntity;
import com.cezarykluczynski.stapi.model.common.entity.Status;
import com.cezarykluczynski.stapi.model.page.entity.PageAware;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Character extends PageAwareEntity implements PageAware {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="character_sequence_generator")
	@SequenceGenerator(name="character_sequence_generator", sequenceName="character_sequence", allocationSize = 1)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private Integer yearOfBirth;

	private Integer monthOfBirth;

	private Integer dayfBirth;

	private String placeOfBirth;

	private Integer yearOfDeath;

	private Integer monthOfDeath;

	private Integer dayfDeath;

	private String placeOfDeath;

	private Integer height;

	private Integer weight;

	@Enumerated(EnumType.STRING)
	private Status status;

	private Integer statusDate;

	private String bloodType;

	@Enumerated(EnumType.STRING)
	private MaritalStatus maritalStatus;

	private String serialNumber;

}
