package bek.dev.studentcrud.consumer;

import bek.dev.studentcrud.config.MQConfig;
import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import bek.dev.studentcrud.payload.Result;
import bek.dev.studentcrud.repository.StudentRepository;
import bek.dev.studentcrud.repository.VisitRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Consumer {

    private  final VisitRepository visitRepository;



    @RabbitListener(queues = MQConfig.VISIT_COME)
    public void consumerMessageFromQueueAboutStudentCome(String studentString){
        Gson gson=new Gson();
        Student student=gson.fromJson(studentString, Student.class);
        Visit visit = new Visit();
        visit.setComeTime(new Date());
        visit.setStudent(student);
        visitRepository.save(visit);
    }

    @RabbitListener(queues = MQConfig.VISIT_BACK)
    public void consumerMessageFromQueueAboutStudentBack(String visitString){
        Gson gson=new Gson();
        Visit visit=gson.fromJson(visitString, Visit.class);
        visit.setComeTime(new Date());
        visitRepository.save(visit);
    }

}
