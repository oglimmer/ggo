package de.oglimmer.ggo.ui;

import java.io.IOException;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.oglimmer.ggo.ui.DiffableInteger.DiffableIntegerSerializer;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@JsonSerialize(using = DiffableIntegerSerializer.class)
@ToString
public class DiffableInteger {

	private Integer val;

	public boolean diffAndUpdate(Integer newVal, Consumer<DiffableInteger> object) {
		if (val != null && !val.equals(newVal)) {
			DiffableInteger di = null;
			if (newVal != null) {
				di = new DiffableInteger(newVal);
			}
			object.accept(di);
			val = newVal;
			return true;
		}
		return false;
	}

	public static DiffableInteger create(Integer b) {
		if (b == null) {
			return null;
		}
		return new DiffableInteger(b);
	}

	static class DiffableIntegerSerializer extends JsonSerializer<DiffableInteger> {

		@Override
		public void serialize(DiffableInteger value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			jgen.writeNumber(value.val);
		}

	}
}
