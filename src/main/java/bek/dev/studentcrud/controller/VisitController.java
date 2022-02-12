package bek.dev.studentcrud.controller;

import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.service.VisitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All visit by month and year",response = Visit.class)})
    public List<Visit> getVisitByMonth(@Parameter(description = "month of visit")@RequestParam(value = "month") Integer monthNumber,
                                       @Parameter(description = "year of visit")@RequestParam(value = "year") Integer yearNumber,
                                       @Parameter(description = "id of student")@PathVariable Long id){
        return visitService.getVisitByMonth(monthNumber,yearNumber, id);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Write visit table that come student", tags = "Come Student")
    @ApiResponses(value = {
            @ApiResponse(code = 203, message = "Student come in", response = Result.class),
            @ApiResponse(code = 400, message = "Student not found", response = Result.class) })
    public HttpEntity<Result> comeStudent(@Parameter(description = "id of student")@PathVariable Long id) throws IOException {
        return visitService.comeStudent(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Write visit table that back student", tags = "Back Student")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Student back out",response = Result.class),
            @ApiResponse(code = 400, message = "Student not found or conflict", response = Result.class) })
    public HttpEntity<Result> backStudent( @Parameter(description = "id of student")@PathVariable Long id) throws IOException {
        return visitService.backStudent( id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete visit by id ", tags = "Get Students")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deleted visit", response = Result.class),
            @ApiResponse(code = 400, message = "Confict", response = Result.class) })
    public HttpEntity<Result> delete(@Parameter(description = "id of student")@PathVariable Long id) throws Exception {
        return visitService.deleteVisit(id);
    }

    @GetMapping("/late")
    @Operation(summary = "Get late all student in this week", tags = "Get Late All Student In This Week")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get Late All Student In This Week", response = Visit.class) })
    public List<Visit> getLateAllStudentInThisWeek(){
        return visitService.getLateAllStudentInThisWeek();
    }

}
