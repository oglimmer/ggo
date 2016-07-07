package de.oglimmer.ggo.email;

import static de.oglimmer.ggo.util.GridGameOneProperties.PROPERTIES;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import de.oglimmer.ggo.logic.Player;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum EmailService {
	EMAIL;

	private String getUnregister(String confirmId) {
		return "\n\n\n\n\nTo unsubscribe (we actually delete your email in our system) click here " + "http://"
				+ PROPERTIES.getDomain() + PROPERTIES.getUrlPath() + "/UnregisterEmail.action?confirmId=" + confirmId;
	}

	public void sendConfirmation(String email, int id, String confirmId) {
		this.send(email, "[GridGameOne] Confirm notifications",
				"Hi,\n\nplease click this link to confirm your email address for notifications for GridGameOne.\n\n"
						+ "http://" + PROPERTIES.getDomain() + PROPERTIES.getUrlPath()
						+ "/ConfirmEmail.action?confirmId=" + confirmId
						+ "\n\n\nRegards,\nOliZ\n\n\n\n\nBtw, we will always give you the chance to delete your email address (completely) from our system.");
	}

	public void notifyGameCreated(String email, String confirmId) {
		this.send(email, "[GridGameOne] Game created notification",
				"Hi,\n\nyou had asked us to notify you in case someone creates a game on GridGameOne.\n\nThis just happened.\n\n"
						+ "Now go to our website and join this game:\nhttp://" + PROPERTIES.getDomain()
						+ "\n\n\nRegards,\nOliZ" + getUnregister(confirmId));
	}

	public void gameNeedsYourAction(Player p) {
		if (p.isFirstEmail()) {
			p.setFirstEmail(false);
			this.send(p.getEmail(), "[GridGameOne] Game invite",
					"Hi,\n\nyou have been invited to a game of GridGameOne.\n\n"
							+ "Click here to do your first turn:\nhttp://" + PROPERTIES.getDomain()
							+ PROPERTIES.getUrlPath() + "/Board.action?playerId=" + p.getId() + "\n\n\nRegards,\nOliZ");
		} else {
			this.send(p.getEmail(), "[GridGameOne] Game needs your command",
					"Hi,\n\na game of GridGameOne needs your command.\n\n"
							+ "Click here to get back to the board:\nhttp://" + PROPERTIES.getDomain()
							+ PROPERTIES.getUrlPath() + "/Board.action?playerId=" + p.getId() + "\n\n\nRegards,\nOliZ");
		}
	}

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public void shutdown() {
		log.debug("Stopping Email scheduler...");
		executor.shutdown();
	}

	private HtmlEmail setup() throws EmailException {
		HtmlEmail simpleEmail = new HtmlEmail();
		simpleEmail.setHostName(PROPERTIES.getSmtpHost());
		if (PROPERTIES.getSmtpPort() != -1) {
			simpleEmail.setSmtpPort(PROPERTIES.getSmtpPort());
			simpleEmail.setSslSmtpPort(Integer.toString(PROPERTIES.getSmtpPort()));
		}
		if (!PROPERTIES.getSmtpUser().isEmpty()) {
			simpleEmail.setAuthentication(PROPERTIES.getSmtpUser(), PROPERTIES.getSmtpPassword());
		}
		simpleEmail.setSSLOnConnect(PROPERTIES.getSmtpSSL());

		simpleEmail.setFrom(PROPERTIES.getSmtpFrom());

		return simpleEmail;
	}

	private void send(final String to, final String subject, final String body) {
		send(to, subject, body, null);
	}

	private void send(final String to, final String subject, String body, final String htmlBody) {
		try {
			final HtmlEmail email = setup();
			email.addTo(to);
			email.setSubject(subject);
			email.setHtmlMsg(htmlBody != null ? htmlBody : body.replace("\n", "<br/>"));
			email.setTextMsg(body);

			if (!PROPERTIES.isEmailDisabled()) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							log.debug("Email (truely) sent to {} with subject {}", to, subject);
							log.trace(htmlBody);
							email.send();
						} catch (EmailException e) {
							log.error("Failed to send email", e);
						}
					}
				});
			}
		} catch (EmailException e) {
			log.error("Failed to create HtmlEmail", e);
		}
	}

}
