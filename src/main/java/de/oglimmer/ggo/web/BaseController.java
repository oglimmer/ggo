package de.oglimmer.ggo.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ui.Model;

public abstract class BaseController {

	private static String longVersionCache;

	@Getter
	@Setter
	private String longVersion;

	protected void addCommonAttributes(Model model) {
		if (longVersionCache == null) {
			longVersionCache = "??";
		}
		
		longVersion = longVersionCache;
		model.addAttribute("longVersion", longVersion);
	}
}