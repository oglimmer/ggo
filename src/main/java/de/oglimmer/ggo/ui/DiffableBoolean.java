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
		if (isNewValFromNull(newVal) || isDifferentNewVal(newVal)) {
			// the system is not capable to set a value to null. null is only allowed as the initial value (==undefined)
			assert newVal != null;
			DiffableBoolean db = new DiffableBoolean(newVal);
			object.accept(db);
			val = newVal;
			return true;
		}
		return false;
	}

	private boolean isDifferentNewVal(Boolean newVal) {
		return val != null && !val.equals(newVal);
	}

	private boolean isNewValFromNull(Boolean newVal) {
		return val == null && newVal != null;
	}

	public static DiffableBoolean create(Boolean b) {
		return new DiffableBoolean(b);
	}

	static class DiffableBooleanSerializer extends JsonSerializer<DiffableBoolean> {

		@Override
		public void serialize(DiffableBoolean value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			if (value.val == null) {
				jgen.writeNull();
			} else {
				jgen.writeBoolean(value.val);
			}
		}

	}
}