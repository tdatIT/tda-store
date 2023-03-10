package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.config.AppVariable;
import com.webapp.tdastore.config.ValidatorUtils;
import com.webapp.tdastore.dto.UserDTO;
import com.webapp.tdastore.entities.ResetPassToken;
import com.webapp.tdastore.entities.User;
import com.webapp.tdastore.entities.VerificationToken;
import com.webapp.tdastore.event.OnRegistrationCompleteEvent;
import com.webapp.tdastore.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;

@Controller
public class AccountController {
    final Logger logger = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    ApplicationEventPublisher event;
    @Autowired
    JavaMailSender sender;
    @Autowired
    ModelMapper mapper;
    @Autowired
    UserService userService;
    @Autowired
    ValidatorUtils validatorUtils;

    @RequestMapping(value = "/dang-nhap", method = RequestMethod.GET)
    public String getLoginPage() {
        return "web/auth/auth-login";
    }

    @RequestMapping(value = "/dang-ky", method = RequestMethod.GET)
    public String getRegisterPage(ModelMap modelMap) {
        UserDTO dto = new UserDTO();
        modelMap.addAttribute("dto", dto);
        return "web/auth/auth-register";
    }

    @PostMapping(value = "/dang-ky")
    public String registerAccount(@ModelAttribute @Valid UserDTO dto,
                                  BindingResult bindingResult,
                                  ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors()
                    .stream()
                    .forEach(element -> logger.error(element.getDefaultMessage()));
            return "redirect:/dang-ky?error=true";
        } else if (dto.getHashPassword().equals(dto.getConfirmPassword())) {
            User user = userService.registerNewUserAccount(dto);
            if (user != null) {
                event.publishEvent(new OnRegistrationCompleteEvent(user, AppVariable.APP_CONTEXT_URL));
                modelMap.addAttribute("msg", "Vui l??ng ki???m tra email ????? x??c th???c !");
                modelMap.addAttribute("link", "/resend-registration");
                return "web/auth/verify-msg";
            }
        }
        modelMap.addAttribute("link", "/dang-nhap");
        modelMap.addAttribute("msg", "Email ???? ????ng k?? t??i kho???n vui l??ng ????ng nh???p l???i");
        return "web/auth/verify-msg";

    }

    @GetMapping("/verify-account")
    public String confirmRegistration(ModelMap model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null || verificationToken.isUsed() == true) {
            String message = "M?? x??c th???c kh??ng kh??? d???ng ho???c h???t h???ng";
            model.addAttribute("message", message);
            return "web/auth/verify-error";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            String messageValue = "M?? x??c th???c kh??ng kh??? d???ng ho???c h???t h???ng";
            model.addAttribute("message", messageValue);
            return "web/auth/verify-error";
        }

        user.setStatus(true);
        userService.registerAccount(user);
        return "web/auth/verify-success";
    }

    @GetMapping(value = "/quen-mat-khau")
    public String getForgotPassPage() {
        return "web/auth/auth-forgot-password";
    }

    @PostMapping(value = "/quen-mat-khau")
    public String forgotPasswordSubmitForm(@RequestParam("email") String email, ModelMap modelMap) {
        if (validatorUtils.checkEmailRegex(email)) {
            userService.createResetToken(email);
        }
        modelMap.addAttribute("email", email);
        return "web/auth/auth-code";
    }

    @PostMapping("/confirm-reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("token") String code,
                                ModelMap modelMap) {
        if (validatorUtils.checkEmailRegex(email)) {
            ResetPassToken resetToken = userService.getResetToken(code);
            if (resetToken.getUser().getEmail().equals(email)) {
                modelMap.addAttribute("email", email);
                modelMap.addAttribute("code", code);
                return "web/auth/auth-reset-password";
            }
        }
        return "web/auth/auth-code";

    }
}
