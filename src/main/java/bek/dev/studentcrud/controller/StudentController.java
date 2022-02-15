package bek.dev.studentcrud.controller;

import bek.dev.studentcrud.ProducerService;
import bek.dev.studentcrud.config.MQConfig;
import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "StudentController", description = "REST APIs related to Student Entity!!!!")
public class StudentController {

    private final StudentService studentService;
    private final ProducerService producerService;


    @GetMapping("/send-message")
    public String sendMessage(){
        producerService.sendMessage(MQConfig.FANOUT_EXCHANGE, "", "Student project send message");
        return "Send message";
    }

    @Operation(summary = "Get list of Students in the System ", tags = "Get Students")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All students",response = Student.class),
            @ApiResponse(code = 400, message = "Page not found") })
    @GetMapping
    public Page<Student> getAllStudentByPage(@Parameter(description = "page of students")@RequestParam("page") Integer  page,
                                             @Parameter(description = "limit of students")@RequestParam("limit") Integer limit){
        return studentService.getAllStudentByPage(page, limit);
    }


    @PostMapping
    @Operation(summary = "Create new student",method = "POST", tags = "Create Employee")
    @ApiResponses(value = {
            @ApiResponse(code = 203, message = "Create student", response = Student.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 405, message = "method not found")})
    public HttpEntity<?> createNewEmployee(
            @Parameter(description = "First name  of students")@RequestParam(value = "firstName", required = false)@NotBlank(message = "Firstname not be empty") String firstName,
            @Parameter(description = "Last name of students")@RequestParam(value = "lastName", required = false)@NotBlank(message = "Lasstname not be empty") String lastName,
            @Parameter(description = "Birth date of students")@RequestParam(value = "birthDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate birthDate,
            @Parameter(description = "Group name of students")@RequestParam(value = "group", required = false)@NotBlank(message = "Group not be empty") String group,
            @Parameter(description = "Studets images")@RequestParam(name = "file") MultipartFile request) throws IOException {
        return studentService.createNewEmployee(firstName, lastName,birthDate, group,request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by id in the System ", tags = "Get Students By Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get by id student",response = Student.class),
            @ApiResponse(code = 400, message = "Student not found by id")})
    public HttpEntity<?> getStudentByID(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Student in the System ", tags = "Update Student")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated student", response = Student.class),
            @ApiResponse(code = 400, message = "Student not found by id")})
    public HttpEntity<Result> updateStudent(@Parameter(description = "id of student that is updating ")@PathVariable Long id,
                                            @Parameter(description = "First name  of students")@RequestParam(value = "firstName", required = false) String firstName,
                                            @Parameter(description = "Last name of students")@RequestParam(value = "lastName", required = false) String lastName,
                                            @Parameter(description = "Birth date of students")@RequestParam(value = "birthDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate birthDate,
                                            @Parameter(description = "Group name of students")@RequestParam(value = "group", required = false) String group,
                                     MultipartHttpServletRequest request) throws IOException {
        return studentService.updateStudent(id, firstName, lastName,birthDate, group, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student in the System ", tags = "Delete Student")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted"),
            @ApiResponse(code = 400, message = "Not deleted")})
    public HttpEntity<Result> delete(@Parameter(description = "Id  of students")@PathVariable Long id) throws Exception {
        return studentService.deleteStudent(id);

    }

    @GetMapping("/preview/{id}")
    @Operation(summary = "Preview image of student  ", tags = "Preview Image")
    public void previewFile(@Parameter(description = "Id  of students")@PathVariable Long id,HttpServletResponse response) throws IOException {
       studentService.previewImage(id, response);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download image of student", tags = "Download Image")
    public void downloadFile(@Parameter(description = "Id  of students")@PathVariable Long id,HttpServletResponse response) throws IOException {
       studentService.downloadImage(id, response);
    }



    @GetMapping("/resume/{id}")
    @Operation(summary = "Download resume of student ", tags = "Download Resume")
    public void downloadResumePdf(@Parameter(description = "Id  of students")@PathVariable Long id,HttpServletResponse response) throws IOException {
        studentService.downloadResumePdf(id, response);
    }

    @GetMapping("/excel")
    @Operation(summary = "Download all student in excel format ", tags = "Download Excel")
    public void downloadAllStudentsExcell(HttpServletResponse response) throws IOException {
        studentService.downloadAllStudentsExcel(response);
    }

    @PostMapping("/write")
    @Operation(summary = "import excel file and write database ", tags = "Import Excel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted", response = Result.class),
            @ApiResponse(code = 409, message = "File is not excel",response = Result.class),
            @ApiResponse(code = 400, message = "File not found",response = Result.class)})
    public HttpEntity<Result> writeDbFromExcel(MultipartHttpServletRequest request) throws IOException {
        return studentService.writeDbFromExcel(request);
    }
}
