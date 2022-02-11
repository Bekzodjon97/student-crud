package bek.dev.studentcrud.controller;

import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.service.VisitService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/visit")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "VisitController", description = "REST APIs related to Visit Entity!!!!")
public class VisitController {

    private final  VisitService visitService;

    @GetMapping("/filter/{id}")
    @Operation(summary = "Get visit by month and year", tags = "Get Visit By Month And Year")
    public List<Visit> getVisitByMonth(@RequestParam(value = "month") Integer monthNumber,
                                       @RequestParam(value = "year") Integer yearNumber,
                                       @PathVariable Long id){
        return visitService.getVisitByMonth(monthNumber,yearNumber, id);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Write visit table that come student", tags = "Come Student")
    public HttpEntity<Result> comeStudent(@PathVariable Long id) throws IOException {
        return visitService.comeStudent(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Write visit table that back student", tags = "Back Student")
    public HttpEntity<Result> backStudent( @PathVariable Long id) throws IOException {
        return visitService.backStudent( id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete visit by id ", tags = "Get Students")
    public HttpEntity<Result> delete(@PathVariable Long id) throws Exception {
        return visitService.deleteVisit(id);
    }

    @GetMapping("/late")
    @Operation(summary = "Get late all student in this week", tags = "Get Late All Student In This Week")
    public List<Visit> getLateAllStudentInThisWeek(){
        return visitService.getLateAllStudentInThisWeek();
    }

}
