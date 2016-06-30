package de.oglimmer.atmospheremvc.com;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommandMessage {
	private String pid;
	private String cmd;
	private String param;
}
