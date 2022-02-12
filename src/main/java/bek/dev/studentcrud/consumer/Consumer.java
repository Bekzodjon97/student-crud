package bek.dev.studentcrud.consumer;

import bek.dev.studentcrud.config.MQConfig;
import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Consumer {

    private  final StudentRepository studentRepository;
    private  final VisitRepository visitRepository;



    @RabbitListener(queues = MQConfig.QUEUE)
    public void consumerMessageFromQueueAboutStudentCome(Long studentId){
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
        Visit visit = new Visit();
        visit.setComeTime(new Date());
        visit.setStudent(optionalStudent.get());
        visitRepository.save(visit);
        }
    }

}
