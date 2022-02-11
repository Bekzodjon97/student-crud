package bek.dev.studentcrud.controller;

import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name = "StudentController", description = "REST APIs related to Student Entity!!!!")
public class StudentController {

    private final StudentService studentService;


    @Operation(summary = "Get list of Students in the System ", tags = "Get Students")
    @GetMapping
    public Page<Student> getAllStudentByPage(@RequestParam("page") Integer  page, @RequestParam("limit") Integer limit){
        return studentService.getAllStudentByPage(page, limit);
    }


    @PostMapping
    @Operation(summary = "Create new student",method = "POST", tags = "Create Employee")
    public HttpEntity<?> createNewEmployee(
            @RequestParam(value = "firstName", required = false)@NotBlank(message = "Firstname not be empty") String firstName,
            @RequestParam(value = "lastName", required = false)@NotBlank(message = "Lasstname not be empty") String lastName,
            @RequestParam(value = "birthDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate birthDate,
            @RequestParam(value = "group", required = false)@NotBlank(message = "Group not be empty") String group,
            MultipartHttpServletRequest request) throws IOException {
        return studentService.createNewEmployee(firstName, lastName,birthDate, group,request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by id in the System ", tags = "Get Students By Id")
    public HttpEntity<?> getStudentByID(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Student in the System ", tags = "Update Student")
    public HttpEntity<Result> updateStudent(@PathVariable Long id,
                                     @RequestParam(value = "firstName", required = false) String firstName,
                                     @RequestParam(value = "lastName", required = false) String lastName,
                                     @RequestParam(value = "birthDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate birthDate,
                                     @RequestParam(value = "group", required = false) String group,
                                     MultipartHttpServletRequest request) throws IOException {
        return studentService.updateStudent(id, firstName, lastName,birthDate, group, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student in the System ", tags = "Delete Student")
    public HttpEntity<Result> delete(@PathVariable Long id) throws Exception {
        return studentService.deleteStudent(id);

    }

    @GetMapping("/preview/{id}")
    @Operation(summary = "Preview image of student  ", tags = "Preview Image")
    public void previewFile(@PathVariable Long id,HttpServletResponse response) throws IOException {
       studentService.previewImage(id, response);
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download image of student", tags = "Download Image")
    public void downloadFile(@PathVariable Long id,HttpServletResponse response) throws IOException {
       studentService.downloadImage(id, response);
    }



    @GetMapping("/resume/{id}")
    @Operation(summary = "Download resume of student ", tags = "Download Resume")
    public void downloadResumePdf(@PathVariable Long id,HttpServletResponse response) throws IOException {
        studentService.downloadResumePdf(id, response);
    }

    @GetMapping("/excell")
    @Operation(summary = "Download all student in excel format ", tags = "Download Excel")
    public void downloadAllStudentsExcell(HttpServletResponse response) throws IOException {
        studentService.downloadAllStudentsExcell(response);
    }

    @PostMapping("/write")
    @Operation(summary = "import excel file and write database ", tags = "Import Excel")
    public HttpEntity<Result> writeDbFromExcel(MultipartHttpServletRequest request) throws IOException {
        return studentService.writeDbFromExcel(request);
    }
}
