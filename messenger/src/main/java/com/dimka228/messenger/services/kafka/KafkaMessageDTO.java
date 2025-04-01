package com.dimka228.messenger.services.kafka;

import com.dimka228.messenger.dto.OperationDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@JsonInclude(Include.NON_DEFAULT)
public class KafkaMessageDTO<T> {

	Integer id;

	OperationDTO<T> change;

}
