package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.UserDTO;
import com.example.demo.service.UserService;
import com.mcnz.jee.soap.*;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.endpoint.UserEndpoint.dateToXMLGregorianCalendar;
import static com.example.demo.endpoint.UserEndpoint.xmlGregorianCalendarToSqlDate;

@Controller
public class WebController {

    private final UserService userService;

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String displayIndex(Model model){

//        List<User> users = userService.getAllUsers();
//
//        for(User user:users){
//            System.out.print(user.getUserName());
//        }

        List<User> users = new ArrayList<>();

        model.addAttribute("pageNumber", 0);

        model.addAttribute("message","Welcome to user experience");
        model.addAttribute("users",users);
        return "index";

    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("userDTO") UserDTO userDTO, Model model) {

        User createUser = new User();
        createUser.setFirstName(userDTO.getFirstName());
        createUser.setUserName(userDTO.getUserName());
        createUser.setPrimaryRole(userDTO.getPrimaryRole());
        createUser.setIsAdmin(userDTO.getIsAdmin());
        createUser.setBirthdate(userDTO.getBirthdate());
        createUser.setId(null);

        userService.createUser(createUser);

        model.addAttribute("status", "idk");
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, @RequestParam("id") Long id) {

        Optional<User> getUser = userService.getUserById(id);
        if(getUser.isPresent()){
            User editUser = getUser.get();
            model.addAttribute("userDTO", editUser);
            model.addAttribute("id", id);

            return "edit";
        } else {
            return "index";
        }
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("userDTO") UserDTO userDTO, Model model, @RequestParam("id") Long id) {

        User editUser = new User();

        editUser.setId(id);
        editUser.setFirstName(userDTO.getFirstName());
        editUser.setUserName(userDTO.getUserName());
        editUser.setPrimaryRole(userDTO.getPrimaryRole());
        editUser.setIsAdmin(userDTO.getIsAdmin());
        editUser.setBirthdate(userDTO.getBirthdate());

        userService.updateUser(id, editUser);

        model.addAttribute("status", "something");
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Model model) {

        userService.deleteUser(id);

        return "redirect:/";
    }

    @HxRequest
    @GetMapping("/tableScroll")
    public String infiniteScroll(@RequestParam("page") Long pageNumber, Model model){
        int pageSize = 10;
        List<User> users = new ArrayList<>();
        for(long i = pageSize*(pageNumber-1)+1; i <= pageSize*pageNumber; i++){

            try {

                Optional<User> getUser = userService.getUserById(i);
                getUser.ifPresent(users::add);

            }catch(Exception e){
                System.out.println(e.getMessage());
            }

        }

        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("hasNextPage",users.size()==pageSize);

        model.addAttribute("users", users);

        return "fragments:: userRows";
    }

}
