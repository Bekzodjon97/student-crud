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
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
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


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.StyleConstants;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StudentService {


    private final StudentRepository studentRepository;

    private static final String IMAGES_DIRECTORY = "files/images";
    private static final Integer DEFAULT_PAGE = 0;
    private static final Integer DEFAULT_LIMIT = 10;


    public HttpEntity<?> createNewEmployee(String firstName,
                                           String lastName,
                                           LocalDate birthDate,
                                           String group,
                                           MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        if (fileNames.hasNext()) {
            MultipartFile file = request.getFile(fileNames.next());
            if (file != null) {
                String originalFilename = file.getOriginalFilename();
                long size = file.getSize();
                String contentType = file.getContentType();
                Attachment attachment = new Attachment();
                attachment.setSize(size);
                attachment.setContentType(contentType);
                attachment.setFileOriginalName(originalFilename);
                assert originalFilename != null;
                String[] split = originalFilename.split("\\.");
                String name = UUID.randomUUID() + "." + split[split.length - 1];
                attachment.setName(name);
                Path path = Paths.get(IMAGES_DIRECTORY + "/" + name);
                Files.copy(file.getInputStream(), path);
                Student newStudent = new Student();
                newStudent.setGroupName(group);
                newStudent.setFirstName(firstName);
                newStudent.setLastName(lastName);
                newStudent.setBirthDate(birthDate);
                newStudent.setAttachment(attachment);
                Student savedStudent = studentRepository.save(newStudent);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
            }
        }
        Student newStudent = new Student();
        newStudent.setGroupName(group);
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setBirthDate(birthDate);
        Student savedStudent = studentRepository.save(newStudent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }


    public HttpEntity<?> getStudentById(Long id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.<HttpEntity<Student>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(400).body(null));
    }

    public HttpEntity<Result> deleteStudent(Long id) {
        try {
            studentRepository.deleteById(id);
            return ResponseEntity.ok(new Result("Successfully deleted", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Not deleted", false));
        }
    }

    public void previewImage(Long id, HttpServletResponse response) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            if (optionalStudent.get().getAttachment() != null) {
                Attachment attachment = optionalStudent.get().getAttachment();
                response.setHeader("Content-Disposition", "inline; filename=\"" + attachment.getFileOriginalName() + "\"");
                response.setContentType(attachment.getContentType());
                FileInputStream inputStream = new FileInputStream(IMAGES_DIRECTORY + "/" + attachment.getName());
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        }
    }

    public void downloadImage(Long id, HttpServletResponse response) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            if (optionalStudent.get().getAttachment() != null) {
                Attachment attachment = optionalStudent.get().getAttachment();
                response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getFileOriginalName() + "\"");
                response.setContentType(attachment.getContentType());
                FileInputStream inputStream = new FileInputStream(IMAGES_DIRECTORY + "/" + attachment.getName());
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        }
    }

    public Page<Student> getAllStudentByPage(Integer pageId, Integer limit) {
        if (pageId == null && limit == null) {
            Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_LIMIT, Sort.by("id"));
            return studentRepository.findAll(pageable);
        } else if (pageId == null) {
            Pageable pageable = PageRequest.of(DEFAULT_PAGE, limit, Sort.by("id"));
            return studentRepository.findAll(pageable);
        } else if (limit == null) {
            Pageable pageable = PageRequest.of(pageId, DEFAULT_LIMIT, Sort.by("id"));
            return studentRepository.findAll(pageable);
        }
        Pageable pageable = PageRequest.of(pageId, limit, Sort.by("id"));
        return studentRepository.findAll(pageable);
    }

    public HttpEntity<Result> updateStudent(Long id, String firstName, String lastName, LocalDate birthDate, String group, MultipartHttpServletRequest request) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Iterator<String> fileNames = request.getFileNames();
            if (fileNames.hasNext()) {
                MultipartFile file = request.getFile(fileNames.next());
                if (file != null) {
                    Student student = optionalStudent.get();
                    Attachment attachment = student.getAttachment();
                    attachment.setSize(file.getSize());
                    attachment.setContentType(file.getContentType());
                    attachment.setFileOriginalName(file.getOriginalFilename());
                    String oldFileName = attachment.getName();
                    File oldFile = new File(IMAGES_DIRECTORY + "/" + oldFileName);
                    String absolutePath = oldFile.getAbsolutePath();
                    File oldFileAbsolutePath = new File(absolutePath);
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
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Object not found", false));
        }
    }


    public void downloadResumePdf(Long id, HttpServletResponse response) throws IOException {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            String name = UUID.randomUUID() + ".pdf";
            try {
                Student student = optionalStudent.get();
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
                document.open();
                Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                Chunk chunk = new Chunk(student.getFirstName() + " " + student.getLastName(), font);
                Attachment attachment = student.getAttachment();
                if (attachment != null) {
                    String imageName = attachment.getName();
                    File imageFile = new File(IMAGES_DIRECTORY + "/" + imageName);
                    Image img = Image.getInstance(imageFile.getAbsolutePath());
                    img.setAlt("Student Image");
                    img.scaleAbsolute(90f, 90f);

                    img.setAbsolutePosition(350, 725);
                    document.add(img);
                }
                document.add(chunk);
                String content = "Birt date: " + student.getBirthDate() + " \nAge: " + student.getAge();
                Paragraph paragraph = new Paragraph(content);
                document.add(paragraph);

                PdfPTable table = new PdfPTable(3);
                Stream.of("Malumoti", "Manzili", "OTM nomi")
                        .forEach(columnTitle -> {
                            PdfPCell header = new PdfPCell();
                            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                            header.setBorderWidth(2);
                            header.setPhrase(new Phrase(columnTitle));
                            table.addCell(header);
                        });
                table.addCell("Oliy");
                table.addCell("Farg'ona");
                table.addCell("Toshkent moliya instituti");
                table.setHorizontalAlignment(200);
                table.setTotalWidth(500);
                PdfContentByte canvas = writer.getDirectContent();
                table.writeSelectedRows(0, -1, 30, 700, canvas);


                document.setHtmlStyleClass("background-color: green");
                document.close();
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadAllStudentsExcell(HttpServletResponse response) {
        String name = "students";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\".xlsx");
        response.setContentType("application/octet-stream");
        try {
            List<Student> studentList = studentRepository.findAll();

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet sheet = xssfWorkbook.createSheet("Students");

            CellStyle cellStyle = xssfWorkbook.createCellStyle();
            XSSFFont font = xssfWorkbook.createFont();
            font.setBold(true);
            font.setFontHeight(16);
            cellStyle.setFont(font);

            XSSFRow mainRow = sheet.createRow(0);

            XSSFCell cell1 = mainRow.createCell(0, CellType.NUMERIC);
            cell1.setCellStyle(cellStyle);
            cell1.setCellValue("Id");

            XSSFCell cell2 = mainRow.createCell(1, CellType.STRING);
            cell2.setCellStyle(cellStyle);
            cell2.setCellValue("Birth date");

            XSSFCell cell3 = mainRow.createCell(2, CellType.STRING);
            cell3.setCellStyle(cellStyle);
            cell3.setCellValue("First name");

            XSSFCell cell4 = mainRow.createCell(3, CellType.STRING);
            cell4.setCellStyle(cellStyle);
            cell4.setCellValue("Last name");

            XSSFCell cell5 = mainRow.createCell(4, CellType.STRING);
            cell5.setCellStyle(cellStyle);
            cell5.setCellValue("Group");


            for (int i = 0; i < studentList.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                row.createCell(0, CellType.NUMERIC).setCellValue(studentList.get(i).getId());
                row.createCell(1, CellType.STRING).setCellValue(studentList.get(i).getBirthDate().toString());
                row.createCell(2, CellType.STRING).setCellValue(studentList.get(i).getFirstName());
                row.createCell(3, CellType.STRING).setCellValue(studentList.get(i).getLastName());
                row.createCell(4, CellType.STRING).setCellValue(studentList.get(i).getGroupName());

            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            xssfWorkbook.write(byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            IOUtils.copy(byteArrayInputStream, response.getOutputStream());
            xssfWorkbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public HttpEntity<Result> writeDbFromExcel(MultipartHttpServletRequest request) throws IOException {

        Iterator<String> fileNames = request.getFileNames();
        if (fileNames.hasNext()) {
            MultipartFile file = request.getFile(fileNames.next());
            if (file != null) {
                Workbook workbook = null;
                InputStream inputStream = file.getInputStream();
                if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                    workbook = new XSSFWorkbook(inputStream);
                } else if (file.getOriginalFilename().endsWith(".xls")) {
                    workbook = new HSSFWorkbook(inputStream);
                }else {
                    return ResponseEntity.status(409).body(new Result("This file is not excell", false));
                }
                Sheet sheet = workbook.getSheet("Students");
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    if (i > 0) {
                        Row row = sheet.getRow(i);
                        Student student = new Student();
                        student.setId((long) row.getCell(0).getNumericCellValue());
                        student.setBirthDate(LocalDate.parse(row.getCell(1).getStringCellValue()));
                        student.setFirstName(row.getCell(2).getStringCellValue());
                        student.setLastName(row.getCell(3).getStringCellValue());
                        student.setGroupName(row.getCell(4).getStringCellValue());
                        studentRepository.save(student);
                    }
                }
            }
            return ResponseEntity.ok(new Result("Saved", true));
        }
        return ResponseEntity.status(400).body(new Result("File is not found", true));
    }
}
