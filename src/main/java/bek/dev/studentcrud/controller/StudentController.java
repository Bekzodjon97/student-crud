package bek.dev.studentcrud.controller;

import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.AttachmentRepository;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RequestMapping("/api/student")
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {


    @Autowired
    StudentService studentService;


    @GetMapping
    public List<Student> getAllStudent(){
       return studentService.getAllStudent();
    }




    @GetMapping("/{pageId}/{limit}")
    public Page<Student> getAllStudentByPage(@PathVariable Integer pageId, @PathVariable Integer limit){
        return studentService.getAllStudentByPage(pageId, limit);
    }

    @PostMapping
    public Student createNewEmployee(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "birthDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate birthDate,
            @RequestParam(value = "group", required = false) String group,
            MultipartHttpServletRequest request) throws IOException {
        return studentService.createNewEmployee(firstName, lastName,birthDate, group,request);
    }

    @GetMapping("/{id}")
    public HttpEntity<Student> getStudentByID(@PathVariable Long id){
        return studentService.getStudentById(id);
    }

    @PutMapping("/{id}")
    public HttpEntity<Result> updateStudent(@PathVariable Long id,
                                     @RequestParam(value = "firstName", required = false) String firstName,
                                     @RequestParam(value = "lastName", required = false) String lastName,
                                     @RequestParam(value = "birthDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)  LocalDate birthDate,
                                     @RequestParam(value = "group", required = false) String group,
                                     MultipartHttpServletRequest request) throws IOException {
        return studentService.updateStudent(id, firstName, lastName,birthDate, group, request);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<Result> delete(@PathVariable Long id) throws Exception {
        return studentService.deleteEmployee(id);

    }

    @GetMapping("/preview/{id}")
    public void previewFile(@PathVariable Long id,HttpServletResponse response) throws IOException {
       studentService.previewImage(id, response);
    }

    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id,HttpServletResponse response) throws IOException {
       studentService.downloadImage(id, response);
    }



    @GetMapping("/resume/{id}")
    public void downloadResumePdf(@PathVariable Long id,HttpServletResponse response) throws IOException {
        studentService.downloadResumePdf(id, response);
    }
}
