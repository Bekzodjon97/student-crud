package bek.dev.studentcrud.service;

import bek.dev.studentcrud.config.MQConfig;
import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.repository.VisitRepository;
import com.google.gson.Gson;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import netscape.javascript.JSObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final StudentRepository studentRepository;
    private final RabbitTemplate rabbitTemplate;


    private static final Integer SET_HOUR = 9;
    private static final Integer SET_MINUTE = 0;


    public HttpEntity<Result> comeStudent(Long id) {
        boolean existsByComeTimeAndStudentId = visitRepository.existsByComeTimeAndStudentId(new Date(), id);
        if (!existsByComeTimeAndStudentId) {
            Optional<Student> optionalStudent = studentRepository.findById(id);
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                Gson gson=new Gson();
                System.out.println("salom");
                System.out.println(student);
                System.out.println(student);
                String studentString=gson.toJson(student);
                System.out.println(studentString);
                rabbitTemplate.convertAndSend(MQConfig.DIRECT_EXCHANGE, MQConfig.VISIT_COME_KEY,studentString);
                return ResponseEntity.status(HttpStatus.CREATED).body(new Result("Visit send for saving", true));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Student not found", false));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Visit already exist", false));
    }

    public HttpEntity<Result> deleteVisit(Long id) {
        try {
            visitRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Result("Visit deleted", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Error", false));
        }
    }

    public List<Visit> getLateAllStudentInThisWeek() {
        return visitRepository.findAllByHourAndMinute(SET_HOUR * 60 + SET_MINUTE);
    }

    public List<Visit> getVisitByMonth(Integer monthNumber, Integer yearNumber, Long id) {
        return visitRepository.findAllByMonth(monthNumber, yearNumber, id);
    }

    public HttpEntity<Result> backStudent(Long id) {
        Optional<Visit> optionalVisit = visitRepository.findById(id);
        if (optionalVisit.isPresent()) {
            Visit visit = optionalVisit.get();
            Gson gson=new Gson();
            String visitString=gson.toJson(visit);
            rabbitTemplate.convertAndSend(MQConfig.DIRECT_EXCHANGE, MQConfig.VISIT_BACK_KEY,visitString);
            return ResponseEntity.ok(new Result("Visit updated", true));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Visit not found", false));
    }
}
