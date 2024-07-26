package com.barber.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScheduleConverter implements AttributeConverter<Schedule, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Schedule schedule) {
		try {
			return objectMapper.writeValueAsString(schedule);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error al convertir Schedule a JSON", e);
		}
	}

	@Override
	public Schedule convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null; // Maneja el caso en que dbData es nulo o vacío
		}
		try {
			return objectMapper.readValue(dbData, Schedule.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error al convertir JSON a Schedule", e);
		}
	}
}
