package bek.dev.studentcrud.service;

import bek.dev.studentcrud.config.MQConfig;
import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
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
                rabbitTemplate.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, optionalStudent.get().getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(new Result("Visit saved", true));
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
            visit.setBackTime(new Date());
            visitRepository.save(visit);
            return ResponseEntity.ok(new Result("Visit updated", true));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Result("Visit not found", false));
    }
}
