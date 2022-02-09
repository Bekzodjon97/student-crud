package bek.dev.studentcrud.controller;

import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/visit")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class VisitController {

    private final  VisitService visitService;

    @GetMapping("/filter/{id}")
    public List<Visit> getVisitByMonth(@RequestParam(value = "month") Integer monthNumber,
                                       @RequestParam(value = "year") Integer yearNumber,
                                       @PathVariable Long id){
        return visitService.getVisitByMonth(monthNumber,yearNumber, id);
    }

    @PostMapping("/{id}")
    public Result comeStudent(@PathVariable Long id) throws IOException {
        return visitService.comeStudent(id);
    }

    @PutMapping("/{id}")
    public Result backStudent( @PathVariable Long id) throws IOException {
        return visitService.backStudent( id);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) throws Exception {
        return visitService.deleteVisit(id);
    }

    @GetMapping("/late")
    public List<Visit> getLateAllStudentInThisWeek(){
        return visitService.getLateAllStudentInThisWeek();
    }

}
