package bek.dev.studentcrud.service;

import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VisitService {



    @Autowired
    VisitRepository visitRepository;
    @Autowired
    StudentRepository studentRepository;

    private static final Integer SET_HOUR =9;
    private static final Integer SET_MINUTE =0;



    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    public Result comeStudent(Long id) {
        boolean existsByComeTimeAndStudentId = visitRepository.existsByComeTimeAndStudentId(new Date(), id);
        if (!existsByComeTimeAndStudentId) {
            Optional<Student> optionalStudent = studentRepository.findById(id);
            if (optionalStudent.isPresent()) {
                Visit visit=new Visit();
                visit.setComeTime(new Date());
                visit.setStudent(optionalStudent.get());
                visitRepository.save(visit);
                return new Result("Visit saved", true);
            }
            return new Result("Student not found" ,false);
        }
        return new Result("Visit already exist" ,false);
    }

    public Result deleteVisit(Long id) {
        try {
        visitRepository.deleteById(id);
        return new Result("Visit deleted", true);
        }catch (Exception e){
            e.printStackTrace();
            return new Result("Error" ,false);
        }
    }

    public List<Visit> getLateAllStudentInThisWeek() {
        return visitRepository.findAllByHourAndMinute(SET_HOUR*60+ SET_MINUTE);
    }

    public List<Visit> getVisitByMonth(Integer monthNumber, Integer yearNumber, Long id) {
        return visitRepository.findAllByMonth(monthNumber,yearNumber, id);
    }

    public Result backStudent( Long id) {
        Optional<Visit> optionalVisit = visitRepository.findById(id);
        if (optionalVisit.isPresent()) {
            Visit visit = optionalVisit.get();
            visit.setBackTime(new Date());
            visitRepository.save(visit);
            return new Result("Visit updated", true);
        }
        return new Result("Visit not found", false);
    }
}
