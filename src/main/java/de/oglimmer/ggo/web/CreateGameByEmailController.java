package de.oglimmer.ggo.web;

import de.oglimmer.ggo.db.GameNotificationsDao;
import de.oglimmer.ggo.email.EmailService;
import de.oglimmer.ggo.logic.Game;
import de.oglimmer.ggo.logic.Player;
import de.oglimmer.ggo.logic.Games;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
@Controller
public class CreateGameByEmailController extends BaseController {

    private GameNotificationsDao gameNotificationsDao;
    private EmailService emailService;

    @Data
    public static class CreateGameForm {
        @NotBlank(message = "{email1.required}")
        @Email(message = "{email1.email}")
        private String email1;

        @Email(message = "{email2.email}")
        private String email2;

        private boolean searchForOne;
    }

    @GetMapping("/CreateGameByEmail")
    public String show(Model model) {
        model.addAttribute("createGameForm", new CreateGameForm());
        addCommonAttributes(model);
        return "createGameByEmail";
    }

    @PostMapping("/CreateGameByEmail")
    public String createEmail(@Valid @ModelAttribute CreateGameForm form, BindingResult result,
                              HttpServletResponse response, Model model) {

        if (!form.isSearchForOne() && (form.getEmail2() == null || form.getEmail2().trim().isEmpty())) {
            result.rejectValue("email2", "email2.required",
                    "Either you need to add an opponent's email or select the checkbox to use the player's database");
        }

        if (result.hasErrors()) {
            addCommonAttributes(model);
            return "createGameByEmail";
        }

        Game game = Games.<Game>getGames().createGame();
        Player player1 = game.createPlayer(form.getEmail1());
        response.addCookie(new Cookie("playerId", player1.getId()));

        if (form.isSearchForOne() || form.getEmail2() == null || form.getEmail2().trim().isEmpty()) {
            gameNotificationsDao.allConfirmed().forEach(rec -> emailService.notifyGameCreatedByEmail(rec.getEmail(), rec.getConfirmId(), game.getId()));
            addCommonAttributes(model);
            return "waitForPlayerJoin";
        } else {
            game.createPlayer(form.getEmail2());
            game.startGame();
            return "redirect:/Board?playerId=" + player1.getId();
        }
    }

}