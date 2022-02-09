package bek.dev.studentcrud.service;

import bek.dev.studentcrud.entity.Attachment;
import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.AttachmentRepository;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.repository.VisitRepository;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Text;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import javax.servlet.http.HttpServletResponse;
import javax.swing.text.StyleConstants;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Service
public class StudentService {


    @Autowired StudentRepository studentRepository;
    @Autowired AttachmentRepository attachmentRepository;
    @Autowired VisitRepository visitRepository;



    private static final String IMAGES_DIRECTORY ="files/images";
    private static final String RESUMES_DIRECTORY ="files/resumes";
    private static final Integer DEFAULT_PAGE =0;
    private static final Integer DEFAULT_LIMIT =10;



    public Student createNewEmployee(String firstName,
                                           String lastName,
                                           LocalDate birthDate,
                                           String group,
                                           MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        if (fileNames.hasNext()) {
            MultipartFile file = request.getFile(fileNames.next());
            if (file!=null){
                String originalFilename = file.getOriginalFilename();
                long size = file.getSize();
                String contentType = file.getContentType();
                Attachment attachment=new Attachment();
                attachment.setSize(size);
                attachment.setContentType(contentType);
                attachment.setFileOriginalName(originalFilename);

                assert originalFilename != null;
                String[] split = originalFilename.split("\\.");
                String name = UUID.randomUUID()+"."+split[split.length-1];
                attachment.setName(name);
                Path path= Paths.get(IMAGES_DIRECTORY +"/"+name);
                Files.copy(file.getInputStream(),path);

                Student newStudent=new Student();
                newStudent.setGroupName(group);
                newStudent.setFirstName(firstName);
                newStudent.setLastName(lastName);
                newStudent.setBirthDate(birthDate);
                newStudent.setAttachment(attachment);

                return studentRepository.save(newStudent);

            }
        }

        Student newStudent=new Student();
        newStudent.setGroupName(group);
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setBirthDate(birthDate);
        return studentRepository.save(newStudent);


    }



    public HttpEntity<Student> getStudentById(Long id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.<HttpEntity<Student>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(409).body(null));
    }

    public HttpEntity<Result> deleteEmployee(Long id) {
        try {
            studentRepository.deleteById(id);
            return ResponseEntity.ok(new Result("Successfully deleted", true));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(new Result("Not deleted", false));
        }
    }

    public void previewImage(Long id, HttpServletResponse response) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            if (optionalStudent.get().getAttachment() != null) {
                Attachment attachment = optionalStudent.get().getAttachment();
                response.setHeader("Content-Disposition", "inline; filename=\""+attachment.getFileOriginalName()+"\"");
                response.setContentType(attachment.getContentType());
                FileInputStream inputStream = new FileInputStream(IMAGES_DIRECTORY +"/"+attachment.getName());
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        }
    }

    public void downloadImage(Long id, HttpServletResponse response) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            if (optionalStudent.get().getAttachment()!=null) {
                Attachment attachment = optionalStudent.get().getAttachment();
                response.setHeader("Content-Disposition", "attachment; filename=\""+attachment.getFileOriginalName()+"\"");
                response.setContentType(attachment.getContentType());
                FileInputStream inputStream = new FileInputStream(IMAGES_DIRECTORY +"/"+attachment.getName());
                FileCopyUtils.copy(inputStream, response.getOutputStream());

            }
        }
    }


    public List<Student> getAllStudent() {
        return studentRepository.findAll();
    }

    public Page<Student> getAllStudentByPage(Integer pageId, Integer limit) {
        if (pageId==null&&limit==null){
            Pageable pageable= PageRequest.of(DEFAULT_PAGE, DEFAULT_LIMIT,  Sort.by("id"));
            return studentRepository.findAll(pageable);
        }else if(pageId == null){
            Pageable pageable= PageRequest.of(DEFAULT_PAGE,limit,  Sort.by("id"));
            return studentRepository.findAll(pageable);
        }else if(limit == null){
            Pageable pageable= PageRequest.of(pageId, DEFAULT_LIMIT,  Sort.by("id"));
            return studentRepository.findAll(pageable);
        }
        Pageable pageable= PageRequest.of(pageId,limit,  Sort.by("id"));
        return studentRepository.findAll(pageable);
    }

    public HttpEntity<Result> updateStudent(Long id, String firstName, String lastName, LocalDate birthDate, String group, MultipartHttpServletRequest request) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            Iterator<String> fileNames = request.getFileNames();
            if (fileNames.hasNext()) {
                MultipartFile file = request.getFile(fileNames.next());
                if (file!=null) {
                    Student student = optionalStudent.get();
                    Attachment attachment = student.getAttachment();
                    attachment.setSize(file.getSize());
                    attachment.setContentType(file.getContentType());
                    attachment.setFileOriginalName(file.getOriginalFilename());
                    String oldFileName = attachment.getName();
                    File oldFile=new File(IMAGES_DIRECTORY +"/"+oldFileName);
                    String absolutePath = oldFile.getAbsolutePath();
                    File oldFileAbsolutePath= new File(absolutePath);
                    boolean delete = oldFileAbsolutePath.delete();
                    assert file.getOriginalFilename() != null;
                    String[] split = file.getOriginalFilename().split("\\.");
                    String name = UUID.randomUUID() + "." + split[split.length - 1];
                    attachment.setName(name);
                    Path path = Paths.get(IMAGES_DIRECTORY + "/" + name);
                    Files.copy(file.getInputStream(), path);
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    student.setGroupName(group);
                    student.setBirthDate(birthDate);
                    studentRepository.save(student);
                    return ResponseEntity.ok(new Result("Updated", true));
                }
            }
            Student student = optionalStudent.get();
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setGroupName(group);
            student.setBirthDate(birthDate);
             studentRepository.save(student);
            return ResponseEntity.ok(new Result("Updated", true));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Result("Object not found", false));
        }
    }









    public void downloadResumePdf(Long id, HttpServletResponse response) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {


            String name = UUID.randomUUID() + ".pdf";



            try {

                Student student = optionalStudent.get();






                Document document = new Document();
                PdfWriter.getInstance(document, response.getOutputStream());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");


                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                Chunk chunk = new Chunk(student.getFirstName()+" "+student.getLastName(), font);


                document.add(chunk);


                String content="Birt date: "+student.getBirthDate()+" \nAge: "+student.getAge();
                Paragraph paragraph=new Paragraph(content);
                document.add(paragraph);


                Attachment attachment = student.getAttachment();
                if (attachment!=null) {
                    String imageName = attachment.getName();
                    File imageFile=new File(IMAGES_DIRECTORY +"/"+imageName);
                    Image img = Image.getInstance(imageFile.getAbsolutePath());
                    img.setAlt("Student Image");
                    img.setAbsolutePosition(200,50);
                    document.add(img);

                }






                document.close();


            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }



        }
    }

}
