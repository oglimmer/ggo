package de.oglimmer.ggo.email;

import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.util.GridGameOneProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailService {

    private final GridGameOneProperties properties;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static EmailService INSTANCE;

    private String getUnregister(String confirmId) {
        return "\n\n\n\n\nTo unsubscribe (we actually delete your email in our system) click here " + "http://"
                + properties.getApp().getDomain() + properties.getApp().getUrlPath() + "/UnregisterEmail?confirmId=" + confirmId;
    }

    public void sendConfirmation(String email, long id, String confirmId) {
        this.send(email, "[GridGameOne] Confirm notifications",
                "Hi,\n\nplease click this link to confirm your email address for notifications for GridGameOne.\n\n"
                        + "http://" + properties.getApp().getDomain() + properties.getApp().getUrlPath()
                        + "/ConfirmEmail?confirmId=" + confirmId
                        + "\n\n\nRegards,\nOliZ\n\n\n\n\nBtw, we will always give you the chance to delete your email address (completely) from our system.");
    }

    public void notifyGameCreatedRealtime(String email, String confirmId) {
        this.send(email, "[GridGameOne] Game created notification",
                "Hi,\n\nyou had asked us to notify you in case someone creates a game on GridGameOne.\n\nThis just happened.\n\n"
                        + "The game is a real-time game. If you want to join go to our website and do so:\nhttp://"
                        + properties.getApp().getDomain() + "\n\n\nRegards,\nOliZ" + getUnregister(confirmId));
    }

    public void notifyGameCreatedByEmail(String email, String confirmId, String gameId) {
        this.send(email, "[GridGameOne] Game created notification",
                "Hi,\n\nyou had asked us to notify you in case someone creates a game on GridGameOne.\n\nThis just happened.\n\n"
                        + "The game is a play-by-email. If you want to join that, please click here:\nhttp://"
                        + properties.getApp().getDomain() + properties.getApp().getUrlPath() + "/JoinPlayByEmail?gameId=" + gameId
                        + "&confirmId=" + confirmId + "\n\n\nRegards,\nOliZ" + getUnregister(confirmId));
    }

    public void gameNeedsYourAction(Player p) {
        if (p.getEmail() == null || p.getEmail().trim().isEmpty()) {
            return;
        }
        if (p.isFirstEmail()) {
            p.setFirstEmail(false);
            this.send(p.getEmail(), "[GridGameOne] Game invite",
                    "Hi,\n\nyou have been invited to a game of GridGameOne.\n\n"
                            + "Click here to do your first turn:\nhttp://" + properties.getApp().getDomain()
                            + properties.getApp().getUrlPath() + "/Board?playerId=" + p.getId() + "\n\n\nRegards,\nOliZ");
        } else {
            this.send(p.getEmail(), "[GridGameOne] Game needs your command",
                    "Hi,\n\na game of GridGameOne needs your command.\n\n"
                            + "Click here to get back to the board:\nhttp://" + properties.getApp().getDomain()
                            + properties.getApp().getUrlPath() + "/Board?playerId=" + p.getId() + "\n\n\nRegards,\nOliZ");
        }
    }

    public void shutdown() {
        log.debug("Stopping Email scheduler...");
        executor.shutdown();
    }

    private HtmlEmail setup() throws EmailException {
        HtmlEmail simpleEmail = new HtmlEmail();
        simpleEmail.setHostName(properties.getSmtp().getHost());
        if (properties.getSmtp().getPort() != -1) {
            simpleEmail.setSmtpPort(properties.getSmtp().getPort());
            simpleEmail.setSslSmtpPort(Integer.toString(properties.getSmtp().getPort()));
        }
        if (!properties.getSmtp().getUser().isEmpty()) {
            simpleEmail.setAuthentication(properties.getSmtp().getUser(), properties.getSmtp().getPassword());
        }
        simpleEmail.setSSLOnConnect(properties.getSmtp().isSsl());

        simpleEmail.setFrom(properties.getSmtp().getFrom());

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

            if (!properties.isEmailDisabled()) {
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
