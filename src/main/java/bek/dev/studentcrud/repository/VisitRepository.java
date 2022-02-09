package bek.dev.studentcrud.repository;

import bek.dev.studentcrud.entity.Student;
import bek.dev.studentcrud.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {



    @Query(value = "select * from visit where extract(month from come_time)=:monthNumber and extract(year from come_time)=:yearNumber and student_id=:studentId", nativeQuery = true)
    List<Visit> findAllByMonth( Integer monthNumber, Integer yearNumber, Long studentId );


    @Query(value = "select * from visit where extract(week from come_time)=extract(week from now()) and (extract(hour from come_time)*60+extract(minute from come_time))>=:comeTime", nativeQuery = true)
    List<Visit> findAllByHourAndMinute(Integer comeTime);


    boolean existsByComeTimeAndStudentId(Date comeTime, Long student_id);


    }




