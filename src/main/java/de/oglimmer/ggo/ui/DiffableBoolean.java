package de.oglimmer.ggo.ui;

import java.io.IOException;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.oglimmer.ggo.ui.DiffableBoolean.DiffableBooleanSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@JsonSerialize(using = DiffableBooleanSerializer.class)
@ToString
public class DiffableBoolean {

	@Getter
	private Boolean val;

	public boolean diffAndUpdate(Boolean newVal, Consumer<DiffableBoolean> object) {
		assert val != null;
		if (val != null && !val.equals(newVal)) {
			DiffableBoolean db = null;
			if (newVal != null) {
				db = new DiffableBoolean(newVal);
			}
			object.accept(db);
			val = newVal;
			return true;
		}
		return false;
	}

	public static DiffableBoolean create(Boolean b) {
		if (b == null) {
			return null;
		}
		return new DiffableBoolean(b);
	}

	static class DiffableBooleanSerializer extends JsonSerializer<DiffableBoolean> {

		@Override
		public void serialize(DiffableBoolean value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			jgen.writeBoolean(value.val);
		}

	}
}