package com.project.back_end.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;
import com.project.back_end.services.TokenService;

// 1. Set Up the MVC Controller Class:
//    - Annotate the class with `@Controller` to indicate that it serves as an MVC controller returning view names (not JSON).
//    - This class handles routing to admin and doctor dashboard pages based on token validation.
@Controller
public class DashboardController {

  // 2. Autowire the Shared Service:
  //    - Inject the TokenService class, which provides the token validation logic used to authorize access to dashboards.
  private final TokenService tokenService;

  public DashboardController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  // 3. Define the `adminDashboard` Method:
  //    - Handles HTTP GET requests to `/adminDashboard/{token}`.
  //    - Accepts an admin's token as a path variable.
  //    - Validates the token using the shared service for the `"admin"` role.
  //    - If the token is valid (i.e., no errors returned), forwards the user to the `"admin/adminDashboard"` view.
  //    - If invalid, redirects to the root URL, likely the login or home page.
  @GetMapping("/adminDashboard/{token}")
  public Object adminDashboard(@PathVariable String token) {
    // Validate the token for admin role
    if (tokenService.validateToken(token, "admin")) {
      // If valid, forward to the admin dashboard view
      return "admin/adminDashboard";
    } else {
      // If invalid, redirect to the root URL
      return new RedirectView("/");
    }
  }

  // 4. Define the `doctorDashboard` Method:
  //    - Handles HTTP GET requests to `/doctorDashboard/{token}`.
  //    - Accepts a doctor's token as a path variable.
  //    - Validates the token using the shared service for the `"doctor"` role.
  //    - If the token is valid, forwards the user to the `"doctor/doctorDashboard"` view.
  //    - If the token is invalid, redirects to the root URL.
  @GetMapping("/doctorDashboard/{token}")
  public Object doctorDashboard(@PathVariable String token) {
    // Validate the token for doctor role
    if (tokenService.validateToken(token, "doctor")) {
      // If valid, forward to the doctor dashboard view
      return "doctor/doctorDashboard";
    } else {
      // If invalid, redirect to the root URL
      return new RedirectView("/");
    }
  }

}
