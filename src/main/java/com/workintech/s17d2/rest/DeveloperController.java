package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.DeveloperTax;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final DeveloperTax developerTax;

    @Autowired
    public DeveloperController(DeveloperTax developerTax) {
        this.developerTax = developerTax;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public void addDeveloper(@RequestBody Developer developer) {
        int id = developer.getId();
        String name = developer.getName();
        double salary = developer.getSalary();
        Experience exp = developer.getExperience();

        Developer newDev;
        switch (exp) {
            case JUNIOR:
                double netJuniorSalary = salary - (salary * developerTax.getSimpleTaxRate() / 100);
                newDev = new JuniorDeveloper(id, name, netJuniorSalary);
                break;
            case MID:
                double netMidSalary = salary - (salary * developerTax.getMiddleTaxRate() / 100);
                newDev = new MidDeveloper(id, name, netMidSalary);
                break;
            case SENIOR:
                double netSeniorSalary = salary - (salary * developerTax.getUpperTaxRate() / 100);
                newDev = new SeniorDeveloper(id, name, netSeniorSalary);
                break;
            default:
                newDev = developer;
                break;
        }

        developers.put(id, newDev);
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer updatedDeveloper) {
        developers.put(id, updatedDeveloper);
        return updatedDeveloper;
    }

    @DeleteMapping("/{id}")
    public void deleteDeveloper(@PathVariable int id) {
        developers.remove(id);
    }
}
