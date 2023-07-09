package org.itstep.projectdeadlinemanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.itstep.projectdeadlinemanagement.command.PartCommand;
import org.itstep.projectdeadlinemanagement.model.Part;
import org.itstep.projectdeadlinemanagement.repository.PartRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblies/parts")
@RequiredArgsConstructor
@Slf4j
public class PartController {
    private final PartRepository partRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("parts", partRepository.findAll());
        return "parts";
    }

    @PostMapping
    String create(@ModelAttribute @Validated PartCommand command,
                  BindingResult bindingResult,
                  RedirectAttributes model) {
        Part part = Part.fromCommand(command);
        log.info(command.toString());
        log.info(bindingResult.toString());
        try {
            if (!bindingResult.hasErrors()) {
                partRepository.save(part);
                model.addFlashAttribute("message", "Part created successfully");
            } else {
                model.addFlashAttribute("error", "Error with fields: %s".formatted(
                        bindingResult.getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getField() + " because of " + fieldError.getDefaultMessage())
                                .distinct()
                                .collect(Collectors.joining(","))));
            }
        } catch (IllegalArgumentException | OptimisticEntityLockException ex) {
            model.addFlashAttribute("error", "Error creating part because of illegal argument or optimistic entry lock");
        } catch (Exception ex) {
            model.addFlashAttribute("error", "Error creating part because of non unique part name");
        }
        return "redirect:/assemblies/parts";
    }

    @GetMapping(("delete/{id}"))
    String delete(@PathVariable Integer id) {
        Optional<Part> optionalPart = partRepository.findById(id);
        optionalPart.ifPresent(part -> partRepository.deleteById(id));
        return "redirect:/assemblies/parts";
    }

//    @GetMapping(("edit/{id}"))
//    String datails(@PathVariable Integer id, Model model) {
//        Optional<Author> optionalAuthor = repository.findById(id);
//        if (optionalAuthor.isPresent()){
//            Author author = optionalAuthor.get();
//            model.addAttribute("author", author);
//        }
//        return "author_edit";
//    }
//    @PostMapping(("edit/{id}"))
//    String update(@PathVariable Integer id, AuthorCommand command) {
//        Optional<Author> optionalAuthor = repository.findById(id);
//        if (optionalAuthor.isPresent()){
//            Author author = optionalAuthor.get();
//            author.setFirstName(command.firstName());
//            author.setLastName(command.lastName());
//            repository.save(author);
//        }
//        return "redirect:/authors";
//    }
}
