package de.oglimmer.ggo.email;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import de.oglimmer.ggo.util.GridGameOneProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum EmailService {
	INSTANCE;

	private String getUnregister(String confirmId) {
		return "\n\n\n\n\nTo unsubscribe (we actually delete your email in our system) click here " + "http://"
				+ GridGameOneProperties.INSTANCE.getDomain() + GridGameOneProperties.INSTANCE.getUrlPath()
				+ "/UnregisterEmail.action?confirmId=" + confirmId;
	}

	public void sendConfirmation(String email, int id, String confirmId) {
		this.send(email, "[GridGameOne] Confirm notifications",
				"Hi,\n\nplease click this link to confirm your email address for notifications for GridGameOne.\n\n"
						+ "http://" + GridGameOneProperties.INSTANCE.getDomain()
						+ GridGameOneProperties.INSTANCE.getUrlPath() + "/ConfirmEmail.action?confirmId=" + confirmId
						+ "\n\n\nRegards,\nOliZ\n\n\n\n\nBtw, we will always give you the chance to delete your email address (completely) from our system.");
	}

	public void notifyGameCreated(String email, String confirmId) {
		this.send(email, "[GridGameOne] Game created notification",
				"Hi,\n\nyou had asked us to notify you in case someone creates a game on GridGameOne.\n\nThis just happened.\n\n"
						+ "Now go the website and join this game:\nhttp://" + GridGameOneProperties.INSTANCE.getDomain()
						+ "\n\n\nRegards,\nOliZ" + getUnregister(confirmId));
	}

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public void shutdown() {
		log.debug("Stopping Email scheduler...");
		executor.shutdown();
	}

	private HtmlEmail setup() throws EmailException {
		HtmlEmail simpleEmail = new HtmlEmail();
		simpleEmail.setHostName(GridGameOneProperties.INSTANCE.getSmtpHost());
		if (GridGameOneProperties.INSTANCE.getSmtpPort() != -1) {
			simpleEmail.setSmtpPort(GridGameOneProperties.INSTANCE.getSmtpPort());
			simpleEmail.setSslSmtpPort(Integer.toString(GridGameOneProperties.INSTANCE.getSmtpPort()));
		}
		if (!GridGameOneProperties.INSTANCE.getSmtpUser().isEmpty()) {
			simpleEmail.setAuthentication(GridGameOneProperties.INSTANCE.getSmtpUser(),
					GridGameOneProperties.INSTANCE.getSmtpPassword());
		}
		simpleEmail.setSSLOnConnect(GridGameOneProperties.INSTANCE.getSmtpSSL());

		simpleEmail.setFrom(GridGameOneProperties.INSTANCE.getSmtpFrom());

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

			if (!GridGameOneProperties.INSTANCE.isEmailDisabled()) {
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
