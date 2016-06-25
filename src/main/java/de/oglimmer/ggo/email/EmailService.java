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

	public void sendConfirmation(String email, int id, String confirmId) {
		this.send(email, "Confirm GridGameOne notifications",
				"Hi,\n\nplease click this link to confirm your email address for notifications for GridGameOne (ggo.oglimmer.de)\n\n"
						+ "http://" + GridGameOneProperties.INSTANCE.getDomain() + "/ConfirmEmail.action?confirmId="
						+ confirmId + "\n\n\nRegards,\nOliZ");
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
